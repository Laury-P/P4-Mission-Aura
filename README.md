# Implémentez l’accès à des données externes pour une application en Kotlin

## 📌 Contexte
Projet réalisé dans le cadre de ma formation **OpenClassrooms – Développeur d’Application Android**.

Ce projet s’inscrit dans un scénario professionnel simulé : reprise du développement d’un MVP d’application Android pour une néobanque fictive (*Aura*), avec pour objectif principal l’implémentation de la consommation d’une API REST en Kotlin, tout en respectant une architecture MVVM.

Le projet est **pédagogique** et ne correspond pas à une application destinée à la production.

---

## 🎯 Objectifs pédagogiques
- Monter en compétences sur le **langage Kotlin** dans un projet Android existant
- Implémenter l’accès à des **données externes via une API REST**
- Appliquer rigoureusement le **pattern MVVM**
- Comprendre et manipuler la **sérialisation / désérialisation JSON**
- Mettre en place des **tests unitaires** sur une partie de la logique métier

---

## ⚙️ Stack technique
- **Langage** : Kotlin
- **UI** : XML (fourni en grande partie)
- **Architecture** : MVVM
- **Réseau** :
  - Retrofit
  - OkHttp
  - Moshi (parsing JSON)
- **Asynchrone** : Coroutines
- **Tests** : Tests unitaires (données mockées)
- **Outils** :
  - Git / GitHub
  - Kanban (Trello fourni par OpenClassrooms)

---

## 🧩 Fonctionnalités implémentées
Dans le cadre du MVP, l’application permet :
- L’authentification d’un utilisateur
- La visualisation de la balance du compte
- L’envoi d’argent à un autre utilisateur

Mon travail s’est principalement concentré sur :
- L’implémentation des **appels réseau** vers l’API REST
- La récupération et le mapping des données JSON
- L’exposition des données vers l’UI via le **ViewModel**
- Le respect strict du **flux de données MVVM (View → ViewModel → Repository)**

> ℹ️ L’interface utilisateur et une partie de la structure du projet étaient fournies.  
> Le projet consistait à compléter et faire évoluer une base existante.

---

## 🧠 Apprentissages et compétences développées
- Utilisation de Kotlin dans un projet Android structuré
- Consommation d’une API REST avec Retrofit
- Gestion de la nullabilité et des modèles de données en Kotlin
- Compréhension du rôle des différentes couches en MVVM
- Mise en place de données mockées pour le développement et les tests
- Lecture et appropriation d’un code existant dans un contexte professionnel simulé

---

## 🔍 Limites du projet
- API utilisée en local avec des **données mockées**
- MVP volontairement limité en fonctionnalités
- Couverture de tests partielle (conformément aux attentes pédagogiques)
- Application non sécurisée pour un contexte réel (authentification simplifiée)

---

## 💡 Ouvertures et pistes d’amélioration
- Gestion avancée des erreurs réseau
- Mise en place de tests d’intégration
- Sécurisation des échanges réseau
- Amélioration de la gestion des états UI (loading / error)
- Migration progressive vers des pratiques plus avancées (ex. StateFlow)

---

## 📚 Ressources et références
- Cours OpenClassrooms :
  - *Initiez-vous à Kotlin*
  - *Récupérez et affichez des données distantes*
- Documentation officielle Android :
  - *Guide to app architecture (MVVM)*

---
