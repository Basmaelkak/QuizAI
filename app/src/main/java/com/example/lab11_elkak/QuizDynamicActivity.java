package com.example.lab11_elkak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class QuizDynamicActivity extends AppCompatActivity {

    TextView tvQuestion, tvFaceStatus;
    RadioGroup rgOptions;
    RadioButton rb1, rb2, rb3, rb4;
    Button btnNext;
    JSONArray questionsArray;
    int currentIndex = 0;
    int score = 0;
    String correctAnswer = "";

    private boolean isFaceDetected = false;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_dynamic);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvFaceStatus = findViewById(R.id.tvFaceStatus);
        rgOptions = findViewById(R.id.rgOptions);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        btnNext = findViewById(R.id.btnNext);

        String questionsJson = getIntent().getStringExtra("questions_json");

        try {
            questionsArray = new JSONArray(questionsJson);
            showQuestion(currentIndex);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur dans le format des questions", Toast.LENGTH_LONG).show();
            finish();
        }

        btnNext.setOnClickListener(v -> {
            if (!isFaceDetected) {
                Toast.makeText(this, "Veuillez activer votre caméra pour détecter un visage", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = rgOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Choisis une réponse", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selected = findViewById(selectedId);
            if (selected.getText().toString().equalsIgnoreCase(correctAnswer)) {
                score++;
            }

            currentIndex++;
            if (currentIndex < questionsArray.length()) {
                showQuestion(currentIndex);
            } else {
                Intent intent = new Intent(QuizDynamicActivity.this, Score.class);
                intent.putExtra("score", score);
                intent.putExtra("total", questionsArray.length());
                startActivity(intent);
                finish();
            }
        });

        // Vérifier les permissions avant de démarrer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startFaceDetection();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startFaceDetection();
            } else {
                Toast.makeText(this, "Permission caméra refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void startFaceDetection() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .build();

                FaceDetector faceDetector = FaceDetection.getClient(options);

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
                    if (imageProxy.getImage() != null) {
                        InputImage inputImage = InputImage.fromMediaImage(
                                imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                        faceDetector.process(inputImage)
                                .addOnSuccessListener(faces -> {
                                    isFaceDetected = !faces.isEmpty();
                                    updateFaceStatus();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Erreur de détection", Toast.LENGTH_SHORT).show();
                                })
                                .addOnCompleteListener(task -> imageProxy.close());
                    } else {
                        imageProxy.close();
                    }
                });

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur caméra", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void updateFaceStatus() {
        runOnUiThread(() -> {
            if (isFaceDetected) {
                tvFaceStatus.setText("✅ Visage détecté");
                tvFaceStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                btnNext.setEnabled(true);
            } else {
                tvFaceStatus.setText("❌ Aucun visage détecté");
                tvFaceStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                btnNext.setEnabled(false);
            }
        });
    }


    private void showQuestion(int index) {
        rgOptions.clearCheck();
        try {
            JSONObject questionObj = questionsArray.getJSONObject(index);
            String questionText = questionObj.getString("question");
            JSONArray options = questionObj.getJSONArray("options");
            correctAnswer = questionObj.getString("answer");

            tvQuestion.setText((index + 1) + ". " + questionText);
            rb1.setText(options.getString(0));
            rb2.setText(options.getString(1));
            rb3.setText(options.getString(2));
            rb4.setText(options.getString(3));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur de parsing de la question", Toast.LENGTH_SHORT).show();
        }
    }
}
