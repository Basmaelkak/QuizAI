package com.example.lab11_elkak;

public class ScoreEntry {
    private Long score;
    private com.google.firebase.Timestamp date;

    public ScoreEntry() {} // Nécessaire pour Firestore

    public ScoreEntry(Long score, com.google.firebase.Timestamp date) {
        this.score = score;
        this.date = date;
    }

    public Long getScore() {
        return score;
    }

    public com.google.firebase.Timestamp getDate() {
        return date;
    }
}
