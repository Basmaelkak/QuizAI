# 📱 QuizAI - Application Android

## 📑 Table des matières
- [Description](#description)
- [Fonctionnalités principales](#fonctionnalités-principales)
- [Technologies utilisées](#technologies-utilisées)
- [Permissions nécessaires](#permissions-nécessaires)
- [Installation & Exécution](#installation--exécution)
- [Captures d’écran](#captures-décran)
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
1. **Cloner le projet** :  
   ```bash
   git clone https://github.com/tonNom/MonProjetAndroid.git


