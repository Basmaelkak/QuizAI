package com.example.lab11_elkak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword, etConfirmPassword;
    Button bRegister;
    TextView tvLogin;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialisation des vues
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etPassword1);
        bRegister = findViewById(R.id.bRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);

        // Initialisation Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bRegister.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Validation
            if (username.isEmpty()) {
                etUsername.setError("Champ requis");
                return;
            }
            if (email.isEmpty()) {
                etEmail.setError("Champ requis");
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Champ requis");
                return;
            }
            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Les mots de passe ne correspondent pas");
                return;
            }

            // Lancement de l'inscription
            progressBar.setVisibility(View.VISIBLE);
            bRegister.setEnabled(false);

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        bRegister.setEnabled(true);

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            String uid = user.getUid();

                            // Sauvegarder le pseudo dans Firestore
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("pseudo", username);
                            userMap.put("email", email);

                            db.collection("users").document(uid).set(userMap)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(Register.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Register.this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(Register.this, "Erreur Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        } else {
                            Toast.makeText(Register.this, "Erreur: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvLogin.setOnClickListener(view -> {
            startActivity(new Intent(Register.this, MainActivity.class));
            finish();
        });
    }
}
