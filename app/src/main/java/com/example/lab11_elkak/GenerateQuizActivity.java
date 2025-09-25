package com.example.lab11_elkak;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class GenerateQuizActivity extends AppCompatActivity {

    EditText etNbQuestions, etSujet;
    RadioGroup rgNiveau;
    Button btnGenerer;

    private static final String OPENAI_API_KEY = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_quiz);

        etNbQuestions = findViewById(R.id.etNbQuestions);
        etSujet = findViewById(R.id.etSujet);
        rgNiveau = findViewById(R.id.rgNiveau);
        btnGenerer = findViewById(R.id.btnGenerer);

        btnGenerer.setOnClickListener(v -> {
            String nb = etNbQuestions.getText().toString().trim();
            String sujet = etSujet.getText().toString().trim();
            int selectedId = rgNiveau.getCheckedRadioButtonId();

            if (nb.isEmpty() || sujet.isEmpty() || selectedId == -1) {
                Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            String niveau = ((RadioButton) findViewById(selectedId)).getText().toString();
            genererQuestionsAvecIA(Integer.parseInt(nb), sujet, niveau);
        });
    }

    private void genererQuestionsAvecIA(int nb, String sujet, String niveau) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Génération en cours...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(() -> {
            try {
                URL url = new URL("https://openrouter.ai/api/v1/chat/completions");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
                conn.setRequestProperty("HTTP-Referer", "https://tonapp.com");
                conn.setRequestProperty("X-Title", "QuizGeneratorApp");
                conn.setDoOutput(true);

                String prompt = "Génère " + nb + " questions de quiz sur le thème '" + sujet + "', niveau " + niveau +
                        ". Retourne uniquement un tableau JSON valide sans texte introductif ni commentaire. Format : " +
                        "[{\"question\": \"...\", \"options\": [\"a\", \"b\", \"c\", \"d\"], \"answer\": \"a\"}]";

                String body = "{\n" +
                        "  \"model\": \"deepseek/deepseek-chat-v3-0324\",\n" +
                        "  \"messages\": [\n" +
                        "    {\"role\": \"user\", \"content\": \"" + prompt.replace("\"", "\\\"") + "\"}\n" +
                        "  ]\n" +
                        "}";

                OutputStream os = conn.getOutputStream();
                os.write(body.getBytes());
                os.flush();

                int responseCode = conn.getResponseCode();
                InputStream stream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

                Scanner scanner = new Scanner(stream);
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                conn.disconnect();

                Log.e("IA_RESPONSE", response.toString());

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.has("error")) {
                    String errorMessage = jsonResponse.getJSONObject("error").optString("message", "Erreur inconnue");
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Erreur API : " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                JSONArray choices = jsonResponse.optJSONArray("choices");
                if (choices == null || choices.length() == 0) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Aucune réponse générée.", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                JSONObject messageObj = choices.getJSONObject(0).optJSONObject("message");
                String content = messageObj != null ? messageObj.optString("content", "") : "";

                if (content.isEmpty()) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Contenu vide dans la réponse.", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                Log.d("IA_CONTENT", content);

                runOnUiThread(() -> {
                    try {
                        dialog.dismiss();

                        String cleanedContent = content
                                .replace("```json", "")
                                .replace("```", "")
                                .trim();

                        // Correction : vérifier si c’est un tableau direct ou une chaîne JSON
                        JSONArray quizArray;
                        try {
                            quizArray = new JSONArray(cleanedContent);
                        } catch (Exception ex) {
                            // Si le tableau est encodé sous forme de string
                            quizArray = new JSONArray(new JSONObject("{\"data\":" + cleanedContent + "}").getString("data"));
                        }

                        String finalJson = quizArray.toString();

                        // Enregistrement local
                        SharedPreferences prefs = getSharedPreferences("MesQuizPref", MODE_PRIVATE);
                        Gson gson = new Gson();
                        ArrayList<String> quizList;

                        String savedQuizzesJson = prefs.getString("quizzes", null);
                        if (savedQuizzesJson != null) {
                            Type type = new TypeToken<ArrayList<String>>() {}.getType();
                            quizList = gson.fromJson(savedQuizzesJson, type);
                        } else {
                            quizList = new ArrayList<>();
                        }

                        quizList.add(finalJson);
                        prefs.edit().putString("quizzes", gson.toJson(quizList)).apply();

                        // Lancer l'activité du quiz
                        Intent intent = new Intent(GenerateQuizActivity.this, QuizDynamicActivity.class);
                        intent.putExtra("questions_json", finalJson);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erreur de parsing : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}
