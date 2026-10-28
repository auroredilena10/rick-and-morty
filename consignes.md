# Consignes

Sujet « Sandbox » : application Rick et Morty. Ce fichier dit **quoi** faire ; la grille d'évaluation (`.claude/skills/audit-projet/references/grille.md`) **prime toujours**, `CLAUDE.md` la traduit pour ce projet ; le code existant dit **comment**. Si une consigne contredit la grille ou une exigence de `CLAUDE.md`, le signaler avant d'agir.

## Code existant (à confirmer en lisant le code)
- Projet Android (pas multiplateforme) : modules `app`, `core:domain`, `core:data`, `core:ui`, `features:characters`. Architecture et chaîne UI : voir `CLAUDE.md` et `docs/ui-architecture.md`. Modèles à copier : `CharacterDetailsScreen` + `CharacterDetailsViewModel` (écran), `CharacterRepositoryImpl` + `CharacterLocal` + `CharacterApi` (repository, Realm et Ktor), `features:characters` (feature). Aucune feature `locations` n'existe encore ; le modèle `Location` est déjà dans `core:domain` (`residents` = `List<Character>`).
- Textes : aujourd'hui seul `app/src/main/res/values/strings.xml` existe ; les textes des écrans iront dans `core/ui/src/main/res/values` et `values-fr` (à créer au premier texte), lus par `stringResource(R.string.xxx)`. KDoc existante en anglais ; KDoc et commentaires neufs en français.

## Objectifs du sujet (les trois épreuves y répondent)
Interpréter les besoins de l'application en se centrant sur les demandes du Domain, et s'intégrer au travail déjà réalisé.
1. Créer un nouveau Model et adapter la couche Domain déjà existante.
2. Gérer des modifications au sein des sources de données en intégrant de nouveaux appels API et schémas de base de données.
3. Répondre aux attentes de la couche Domain en jonglant avec ces différentes sources.
4. Créer une nouvelle Feature au sein de l'app.
5. Manipuler les concepts MVI.
6. Implémenter une fonctionnalité liée au hardware Android.

Référence API : https://rickandmortyapi.com/documentation/#character-schema

## Épreuve 1 : mettre à jour les données
- Ajouter un nouvel objet `LocationPreview` au sein de la définition de la classe `Character`.
- Une `LocationPreview` est une version light du model `Location`, caractérisée par un `id`, une chaîne de caractères `name`, une chaîne de caractères `type` et une chaîne de caractères `dimension`.
- Une `Location` est caractérisée par tous les éléments ci-dessus, ainsi qu'une liste de `Character`.
- Respecter la séparation des préoccupations.
- Au niveau de la data, manipuler 2 sources de données et mettre en place ce mécanisme de fetch :
  1. Chercher la donnée en local.
     - a. Si pas présente, chercher à distance.
       - i. Si pas présente, jeter une erreur.
       - ii. Si présente, insérer en base, puis mapper et retourner la donnée.
     - b. Si présente, mapper et retourner la donnée.

## Épreuve 2 : visualiser les locations et accéder à leurs détails
- Dans l'écran `CharacterDetails`, faire apparaître la location relative au personnage sélectionné.
- Afficher une card avec le `name` de la location et son `type`.
- Au clic sur la `LocationCard`, naviguer vers un nouvel écran nommé `LocationDetails` qui affiche exactement les mêmes données, ainsi que la liste des personnages de cette `Location`.
- Lors du clic sur la card, l'appareil émet une vibration.

## Épreuve 3 : DX
- Rédiger un `README.md` contenant une explication de l'architecture du projet Android.
- Faire attention aux commentaires : ils décrivent le pourquoi, pas le comment.
- Ranger le code, le commenter et bien nommer.
- Utiliser KDoc pour commenter les blocs.
- Utiliser des Managers lorsque c'est nécessaire.
- Utiliser des fonctions d'extension lorsque c'est nécessaire.

## Décisions prises
- Travail directement sur `dev`, sans branche par épreuve.
- Managers des capacités natives : interface dans `core:domain` (`managers/`, sans import Android), implémentation qui reçoit le `Context` dans `core:data` (`managers/`), déclarée dans `dataModule` ; même schéma que `CharacterRepository` / `CharacterRepositoryImpl`.

## Points à trancher (proposer une solution dans le plan de l'épreuve concernée)
- `LocationPreview` remplacera `Character.location` (aujourd'hui `Pair<String, Int>`) : décider à l'épreuve 1 de l'emplacement du modèle et de `LocationRepository` (`core:domain`, à côté de `CharacterRepository`).
- Realm efface la base à chaque changement de schéma (`deleteRealmIfMigrationNeeded` dans `RMDatabase`) alors que la page suivante de personnages reste dans le `DataStore` : vérifier à l'épreuve 1 que la liste se recharge bien après l'ajout d'un objet.
- Il n'y a pas de `README.md` : l'épreuve 3 le crée, avec l'explication de l'architecture.
- Aucun test au départ : n'en ajouter que si une configuration de test existe déjà.
