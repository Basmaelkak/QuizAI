package com.example.lab11_elkak;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView scoreTextView;
    private TextView nameTextView;
    private EditText editPseudo;
    private static final int PICK_IMAGE_REQUEST = 1001;
    private static final int REQUEST_MEDIA_PERMISSION = 2001;
    private Button btnUpdate, btnChangePhoto;

    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profile_image);
        scoreTextView = findViewById(R.id.profile_score);
        nameTextView = findViewById(R.id.profile_name);
        editPseudo = findViewById(R.id.edit_pseudo);
        btnUpdate = findViewById(R.id.btn_update_profile);
        btnChangePhoto = findViewById(R.id.btn_change_photo);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(user.getUid());
        storageRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + user.getUid() + ".jpg");

        loadUserProfile();

        btnUpdate.setOnClickListener(v -> updatePseudo());
        btnChangePhoto.setOnClickListener(v -> openGallery());
    }

    private void loadUserProfile() {
        if (user != null) {
            userRef.get().addOnSuccessListener(document -> {
                if (document.exists()) {
                    String pseudo = document.getString("pseudo");
                    Long score = document.getLong("score");
                    String photoUrl = document.getString("photoUrl");

                    nameTextView.setText(pseudo != null ? pseudo : "Nom non défini");
                    editPseudo.setText(pseudo != null ? pseudo : "");

                    scoreTextView.setText("Score : " + (score != null ? score : 0));

                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(this).load(photoUrl).circleCrop().into(profileImageView);
                    }
                }
            });
        }
    }

    private void updatePseudo() {
        String newPseudo = editPseudo.getText().toString().trim();
        if (!newPseudo.isEmpty()) {
            userRef.update("pseudo", newPseudo).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Pseudo mis à jour", Toast.LENGTH_SHORT).show();
                nameTextView.setText(newPseudo);
            });
        }
    }

    private void openGallery() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_MEDIA_PERMISSION);
            } else {
                pickImageFromGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MEDIA_PERMISSION);
            } else {
                pickImageFromGallery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Permission de lecture du stockage refusée.", Toast.LENGTH_SHORT).show();

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Nécessaire")
                            .setMessage("L'accès au stockage est nécessaire pour choisir une photo de profil.")
                            .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MEDIA_PERMISSION))
                            .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Nécessaire")
                            .setMessage("L'accès au stockage a été refusé. Veuillez l'activer manuellement dans les paramètres de l'application.")
                            .setPositiveButton("Ouvrir les Paramètres", (dialog, which) -> {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            })
                            .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Log.d("ProfileActivity", "Selected Image Uri: " + imageUri.toString());

            Glide.with(this).load(imageUri).circleCrop().into(profileImageView);

            uploadProfileImage(imageUri);
        }
    }

    private void uploadProfileImage(Uri imageUri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(imageUri);
            if (inputStream != null) {
                StorageReference uploadTaskRef = storageRef;
                UploadTask uploadTask = uploadTaskRef.putStream(inputStream);
                uploadTask.addOnSuccessListener(taskSnapshot -> uploadTaskRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    userRef.update("photoUrl", downloadUrl)
                                            .addOnSuccessListener(aVoid -> {
                                                Glide.with(this).load(downloadUrl).circleCrop().into(profileImageView);
                                                Toast.makeText(this, "Photo mise à jour", Toast.LENGTH_SHORT).show();
                                            });
                                }))
                        .addOnFailureListener(e -> Toast.makeText(this, "Erreur d'upload: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Impossible d'ouvrir le flux de données de l'image.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ProfileActivity", "Erreur lors de l'ouverture du flux: " + e.getMessage());
            Toast.makeText(this, "Erreur lors de la lecture de l'image.", Toast.LENGTH_SHORT).show();
        }
    }
}
