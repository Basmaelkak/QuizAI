package com.example.lab11_elkak;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Score extends AppCompatActivity {
    Button bLogout, bTry;
    ProgressBar progressBar;
    TextView tvScore;
    int score;
    FirebaseFirestore db;
    DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Initialisation des vues
        tvScore = findViewById(R.id.tvScore);
        progressBar = findViewById(R.id.progressBar);
        bLogout = findViewById(R.id.bLogout);
        bTry = findViewById(R.id.bTry);

        // Récupération du score et du total
        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);
        int total = intent.getIntExtra("total", 5); // 5 par défaut
        int percentage = (int) ((double) score / total * 100);

        // Mise à jour de l'interface
        progressBar.setProgress(percentage);
        tvScore.setText(percentage + " %");

        // Récupération de l'utilisateur actuel
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(userId);

        // Mise à jour du score dans Firestore
        updateScoreInFirestore(score);

        bLogout.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Merci de votre participation !", Toast.LENGTH_SHORT).show();
            finish();
        });

        bTry.setOnClickListener(v -> {
            startActivity(new Intent(Score.this, Quiz1.class));
        });
    }

    private void updateScoreInFirestore(int score) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("score", score);

        userRef.set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Score.this, "Score mis à jour", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Score.this, "Erreur lors de la mise à jour du score", Toast.LENGTH_SHORT).show();
                });

        bTry.setOnClickListener(v -> {
            Intent intent = new Intent(Score.this, ProfileActivity.class);
            intent.putExtra("score", score);  // Passer le score
            startActivity(intent);
        });
    }

}

