# 📱 QuizAI - Application Android

## 📑 Table des matières
- [Description](#description)
- [Fonctionnalités principales](#fonctionnalités-principales)
- [Technologies utilisées](#technologies-utilisées)
- [Permissions nécessaires](#permissions-nécessaires)
- [Installation & Exécution](#installation--exécution)
- [ Démonstration vidéo](#Démonstration-vidéo)
- [Auteur](#auteur)
- [Licence](#licence)

---

## 📝 Description
Ce projet est une application Android réalisée avec **Android Studio**.  
Elle combine plusieurs fonctionnalités modernes :  
- Authentification des utilisateurs avec **Firebase Authentication**.  
- **Google Maps** avec localisation en temps réel.  
- Génération de **quiz dynamiques via une IA** (OpenRouter API).  
- Sauvegarde locale des quiz générés et possibilité de les rejouer.  
- Interface simple et intuitive avec un **Dashboard** central.  

---

## 🚀 Fonctionnalités principales
- 🔑 **Authentification** (login & inscription avec Firebase).  
- 🗺️ **Carte interactive Google Maps** affichant la localisation en direct.  
- 📝 **Quiz fixes** (Quiz1 → Quiz5) et **quiz générés automatiquement via IA**.  
- 💾 **Sauvegarde des quiz générés** en local (via `SharedPreferences`).  
- 📂 **Gestion des quiz créés** (consulter, rejouer, supprimer tous).  
- 👤 **Gestion du profil utilisateur**.  
- 📊 **Score** affiché après un quiz.  

---

## 🛠️ Technologies utilisées
- **Langage** : Java (Android SDK)
- **Firebase Authentication** (connexion / inscription)
- **Google Maps SDK** (géolocalisation & affichage de carte)
- **FusedLocationProviderClient** (mise à jour de la localisation)
- **OpenRouter API (IA)** : génération de quiz dynamiques
- **Gson** : parsing et sérialisation JSON
- **SharedPreferences** : persistance locale
- **Material Design** : design moderne et intuitif

---

## ⚙️ Permissions nécessaires
L’application utilise plusieurs permissions (déclarées dans `AndroidManifest.xml`) :
- 📍 `ACCESS_FINE_LOCATION` et `ACCESS_COARSE_LOCATION` → Localisation GPS.  
- 🌍 `INTERNET` → Communication avec Firebase & API IA.  
- 📸 `CAMERA` → Accès à l’appareil photo (si nécessaire).  
- 📂 `READ_MEDIA_*` → Gestion de fichiers multimédias.  

---

## ▶️ Installation & Exécution
2. **Ouvrir dans Android Studio** :  
   - Fichier → Ouvrir → sélectionner le dossier cloné.

3. **Configurer Firebase** :  
   - Copier le fichier `google-services.json` dans le dossier `app/`.  
   - Activer l’authentification Email/Mot de passe dans Firebase Console.

4. **Configurer Google Maps** :  
   - Activer l’API Google Maps dans Google Cloud Console.  
   - Renseigner la clé API dans le `AndroidManifest.xml` :  
     ```xml
     <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="YOUR_API_KEY_HERE" />
     ```

5. **Configurer l’API OpenRouter** :  
   - Ajouter ta clé API dans `GenerateQuizActivity.java` :  
     ```java
     private static final String OPENAI_API_KEY = "YOUR_API_KEY_HERE";
     ```

6. **Gérer les permissions Android** :  
   - L’application demandera automatiquement l’accès caméra et localisation.  
   - Autoriser ces permissions pour que toutes les fonctionnalités fonctionnent correctement.

7. **Lancer l’application** :  
   - Sélectionner un émulateur ou un appareil Android connecté.  
   - Cliquer sur **Run**.  
   - Tester l’authentification, la génération de quiz, la localisation et la détection faciale.

---

## 📷 Détection faciale pendant le quiz
- Utilisation de **CameraX** et **ML Kit Face Detection**.  
- Empêche l’utilisateur de passer les questions si aucun visage n’est détecté.  
- Affiche un indicateur visuel (`✅ Visage détecté` ou `❌ Aucun visage détecté`).  
- Garantie l’intégrité du quiz et limite les tentatives de triche.

---

## 🎥 Démonstration vidéo



https://github.com/user-attachments/assets/c712b0d8-e39e-410b-9d8c-acb18226b5f7


https://github.com/user-attachments/assets/81474eb2-4136-48b9-9483-0190f66e3d8f

---

## 👩‍💻 Auteur
**Basma El Kak**  
- Étudiante en Génie Réseau et Systèmes d’Information  
- Email : kak.basma08@gmail.com  
- LinkedIn : [Basma El Kak](https://www.linkedin.com/in/basma-el-kak)

---

## 📄 Licence
Ce projet est sous **MIT License**.  




