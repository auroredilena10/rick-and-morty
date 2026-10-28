# Grille d'évaluation (P2028 DI2 P1 Mobile, Eduxim)

**La grille prime toujours** : sur le sujet (`consignes.md`), sur `CLAUDE.md`, sur les skills et sur toute habitude. En cas de doute, viser le niveau **Maîtrisé** de chaque critère. Transcription fidèle du fichier de l'école ; barème : Non réalisé 0 · Non acquis 5 · En cours 9 · Acquis 15 · Maîtrisé 20.

## Sommaire
- Architecture (B.02.C.01) : S-1, S-3, SF-1, SF-3
- Qualité du code (B.02.C.02) : SE-1 à SE-5
- Front-end (B.02.C.03) : S-2, S-4, S-5, S-6, SF-4, SF-5, SF-6

Les critères **S** se jugent surtout à l'oral (savoir expliquer et schématiser) ; les critères **SF** et **SE** se jugent dans le code.

## Architecture (B.02.C.01)

### S-1 · Rôle de la Clean Architecture dans une app Android
Évalue : principes de l'architecture en couches ; rôle distinct de chaque couche (présentation, logique métier, accès aux données) ; séparation des préoccupations ; dépendances orientées vers l'intérieur (indépendance des frameworks, testabilité, maintenance).
Maîtrisé (texte tel quel dans la grille, qui reprend celui des fondamentaux Android) : compréhension approfondie du Manifest, de l'organisation des dossiers, de Gradle ; expertise sur l'Activity et son cycle de vie ; comprend le Context et l'Intent.

### S-3 · Architecture de la couche Domain
Évalue : architecture Domain-Centric, Models et Repositories ; les modèles du domaine portent la logique métier, découplés du stockage et de l'UI ; rôle des interfaces Repository.
Maîtrisé : expertise des Domain Models et des Repositories ; logique métier totalement découplée ; explications claires ; modéliser en anticipant les évolutions ; illustrer comment l'indépendance du Domain garantit scalabilité et maintenabilité.

### SF-1 · Intégration dans une architecture orientée Clean Architecture
Évalue : séparer les préoccupations entre couches ; composants cohérents avec les standards proposés.
Maîtrisé : intégration exemplaire, couplage faible ; toute la boîte à outils utilisée (Gradle, injection de dépendances) ; **Koin parfaitement intégré**, couches parfaitement séparées.

### SF-3 · Intégration et adaptation à une architecture Domain proposée
Évalue : concevoir Domain Models, interfaces Repository et logiques métier indépendantes.
Maîtrisé : modèles et interfaces Repository totalement indépendants des couches externes ; Clean Architecture entièrement respectée (maintenabilité, réutilisabilité) ; adapter le Domain en anticipant les évolutions sans casser la séparation des responsabilités.

## Qualité du code (B.02.C.02)

### SE-1 · Expérience de développement, couche Data
Maîtrisé, tous les points :
- fonctions et services clairement commentés selon les conventions Kotlin ;
- fonctions logiquement découpées et réutilisables ;
- pas de duplication inutile ;
- noms de fonctions et de variables explicites et informatifs ;
- couples DAO/Entity et Service/DTO bien définis et séparés ;
- headers KDoc pertinents sur les composants complexes.
Acquis : cinq points.

### SE-2 · Expérience de développement, couche UI
Maîtrisé, tous les points :
- code intégré de manière indifférenciée par rapport au reste de l'application ;
- écrans et fonctionnalités bien nommés ;
- composants placés au bon endroit ;
- composables découpés selon la philosophie des LEGO ;
- actions du ViewModel nommées de manière claire et informative ;
- State bien formé, qui contient la représentation visuelle des données ;
- un composant complexe couronné d'un header KDoc avec block tags pertinents.
Acquis : six points. En cours : la moitié.

### SE-3 · Expérience de développement, couche Domain
Évalue : séparation des responsabilités ; composants métier (models, interfaces) bien organisés et documentés ; actions du Domain indépendantes des couches externes ; code indifférenciable de l'existant ; headers limités à la logique métier, qui reflètent l'intention derrière les modèles et les interfaces.
Maîtrisé : code indifférencié ; responsabilités métier isolées, composants réutilisables et indépendants ; tous les composants complexes ont un header qui explique précisément la logique métier ; interfaces clairement définies et indépendantes des couches externes.

### SE-4 · Expérience de développement, projet Android
Maîtrisé, tous les points :
- Manifest sans warning, avec des commentaires explicatifs pertinents ;
- Gradle sans warning, sections démarquées par des commentaires ou des regions ;
- versions des dépendances aisément identifiables, nommage pertinent ;
- nommage des ressources qui suit une convention ;
- commentaire clair et informatif sur l'exploitation d'un élément du cycle de vie ;
- fonction d'extension couronnée d'un header KDoc avec block tags pertinents.
Acquis : cinq points. En cours : la moitié.

### SE-5 · Qualité de code
Maîtrisé, tous les points :
- Managers qui gèrent l'accès aux capacités natives ;
- conventions de nommage Kotlin respectées (Lint) ;
- commentaires clairs et informatifs sur le fonctionnement des Managers ;
- extension de `Context` couronnée d'un header KDoc avec block tags pertinents.
Acquis : trois points.

## Front-end (B.02.C.03)

### S-2 · Architecture de la couche Data
Évalue : mécanismes de fetch des repositories ; couples Service/DTO et DAO/Entity, leurs rôles et le mapping ; ce qui transite entre les couches ; découplage du stockage et de la logique métier.
Maîtrisé : expertise sur tout cela, explications claires, précises, qui vont droit au but ; **capacité à schématiser**.

### S-4 · Couche UI et Jetpack Compose
Évalue : paradigme déclaratif ; MVI et flux unidirectionnel (UDF) ; gestion des états ; Single Activity ; navigation ; gestion des événements ; composables qui facilitent la gestion des écrans.
Maîtrisé : expertise sur tout cela ; sait illustrer comment ces éléments s'harmonisent ; **schématise** ; explications claires.

### S-5 · Fondamentaux d'une app Android
Évalue : structure du projet (Manifest, dossiers Kotlin, ressources, Gradle) ; Activity, Context et leur cycle de vie.
Maîtrisé : solide compréhension du Manifest, des ressources et de Gradle ; maîtrise d'Activity, Context et Intent et de leur cycle de vie ; sait appliquer ces connaissances.

### S-6 · Capacités natives du dispositif
Évalue : interaction avec le hardware (capteurs, GPS, caméra, stockage) ; rôle du Context comme médiateur avec les services système ; logique hardware encapsulée dans des Managers ; UI sans logique hardware.
Maîtrisé : compréhension approfondie et nuancée ; sait concevoir des solutions hardware avec une architecture claire ; explications limpides.

### SF-4 · Intégration et adaptation à une architecture Data proposée
Maîtrisé, tous les points :
- couples DAO/Entity et Service/DTO bien structurés et clairement séparés ;
- mécanisme de fetch entre sources locales et distantes cohérent et bien implémenté ;
- mapping entre les couches implémenté ;
- code maintenable, modulaire, conforme à la Clean Architecture.
Acquis : trois points sur quatre.

### SF-5 · Application pratique des fondamentaux Android
Évalue : utiliser efficacement Activity, Context, Intent et cycle de vie ; Manifest et Gradle bien gérés.
Maîtrisé, tous les points :
- Manifest propre et pertinent (**warning interdit**) ;
- Gradle lisible et compréhensible (**warning interdit**) ;
- versions des dépendances centralisées ;
- ressources utilisées systématiquement, au moins une variante (mode sombre, autre langue…) ;
- utilisation judicieuse du cycle de vie (Activity ou Compose), mise en avant par un commentaire spécifique ;
- fonction d'extension liée à une composante Android (ViewModel, Context, Intent).
Acquis : quatre points, ou trois points Maîtrisé et deux En cours.
À éviter (niveau En cours) : warnings tolérés, versions en dur, ressources mal rangées, cycle de vie utilisé sans objectif pertinent (« logger pour logger »).

### SF-6 · Interaction avec les fonctionnalités hardware
Évalue : utiliser le Context pour accéder aux services système ; Managers qui séparent l'accès hardware de l'UI ; UI libre de toute logique hardware ; permissions gérées selon les bonnes pratiques de sécurité et de vie privée.
Maîtrisé, tous les points :
- au moins **deux** capacités natives (son, base de données, GPS, gyroscope…) ;
- accès aux capacités natives centralisé et détaché de la couche UI ;
- au moins une fonction d'extension dont la classe étendue est `Context`.
Acquis : les deux premiers points. En cours : une seule capacité native, ou seulement le premier point.
