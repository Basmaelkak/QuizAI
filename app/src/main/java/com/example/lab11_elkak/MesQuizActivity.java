package com.example.lab11_elkak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MesQuizActivity extends AppCompatActivity {

    ListView listView;
    Button btnSupprimerTout;
    ArrayList<String> listeQuiz; // Contient le JSON de chaque quiz
    ArrayList<String> titresAffiches; // Liste simplifiée pour l’affichage
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_quiz);

        listView = findViewById(R.id.listViewQuiz);
        btnSupprimerTout = findViewById(R.id.btnSupprimerTout); // le bouton dans le layout

        SharedPreferences prefs = getSharedPreferences("MesQuizPref", MODE_PRIVATE);
        String json = prefs.getString("quizzes", null);

        if (json == null) {
            Toast.makeText(this, "Aucun quiz trouvé.", Toast.LENGTH_SHORT).show();
            listeQuiz = new ArrayList<>();
            titresAffiches = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            listeQuiz = gson.fromJson(json, type);
            titresAffiches = new ArrayList<>();

            for (int i = 0; i < listeQuiz.size(); i++) {
                try {
                    String quizJson = listeQuiz.get(i);
                    ArrayList<Question> questions = gson.fromJson(quizJson, new TypeToken<ArrayList<Question>>(){}.getType());
                    String titre = "Quiz " + (i + 1) + ": " + questions.get(0).getQuestion();
                    titresAffiches.add(titre.length() > 50 ? titre.substring(0, 50) + "..." : titre);
                } catch (Exception e) {
                    titresAffiches.add("Quiz " + (i + 1));
                }
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titresAffiches);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String quizJson = listeQuiz.get(position);
            Intent intent = new Intent(MesQuizActivity.this, QuizDynamicActivity.class);
            intent.putExtra("questions_json", quizJson);
            startActivity(intent);
        });

        // Suppression de tous les quiz
        btnSupprimerTout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer tous les quiz ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        SharedPreferences prefs1 = getSharedPreferences("MesQuizPref", MODE_PRIVATE);
                        prefs1.edit().remove("quizzes").apply();

                        listeQuiz.clear();
                        titresAffiches.clear();
                        adapter.notifyDataSetChanged();

                        Toast.makeText(this, "Tous les quiz ont été supprimés.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    // Classe interne pour modéliser une Question (comme dans le JSON)
    public static class Question {
        private String question;
        private ArrayList<String> options;
        private String answer;

        public String getQuestion() {
            return question;
        }

        public ArrayList<String> getOptions() {
            return options;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
