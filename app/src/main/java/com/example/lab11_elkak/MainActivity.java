package com.example.lab11_elkak;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText etlogin, etPassword;
    private Button bLogin;
    private TextView tvRegister;
    private FirebaseAuth mAuth;  // Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialisation de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Récupération des IDs
        etlogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        bLogin = findViewById(R.id.blogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Association des listeners pour les boutons
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupération des valeurs entrées
                String email = etlogin.getText().toString();
                String password = etPassword.getText().toString();

                // Authentification avec Firebase
                loginUser(email, password);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class)); // Redirection vers l'activité Register
            }
        });
    }

    // Méthode pour l'authentification avec Firebase
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity", "Login successful, starting DashboardActivity");

                        startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                        finish(); // On termine l’activité actuelle pour ne pas revenir avec le bouton back


                } else {
                        // Échec de la connexion
                        Toast.makeText(MainActivity.this, "Login ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "Login failed: " + task.getException());
                    }
                });
    }
}