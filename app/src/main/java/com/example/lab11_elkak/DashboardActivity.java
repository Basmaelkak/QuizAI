package com.example.lab11_elkak;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DashboardActivity extends AppCompatActivity {

    private Button btnProfile, btnMap, btnQuiz, btnGenerateQuiz, btnMesQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // Liens boutons
        btnProfile = findViewById(R.id.btn_profile);
        btnMap = findViewById(R.id.btn_map);
        btnQuiz = findViewById(R.id.btn_quiz);
        btnGenerateQuiz = findViewById(R.id.btn_generate_quiz);
        btnMesQuiz = findViewById(R.id.btn_mes_quiz);

        // Listeners
        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class)));

        btnMap.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, MapsActivity.class)));

        btnQuiz.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, Quiz1.class)));

        btnGenerateQuiz.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, GenerateQuizActivity.class)));

        btnMesQuiz.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, MesQuizActivity.class)));
    }
}
