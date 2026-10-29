# Clean RmApi + MVI

Application Android qui affiche les personnages et les lieux de la série Rick et Morty, à partir de l'[API Rick et Morty](https://rickandmortyapi.com/documentation).
Elle est écrite en Kotlin avec Jetpack Compose, et découpée en modules selon la Clean Architecture.

Ce README explique comment le projet est rangé, comment les données circulent et où ajouter du code. Si tu reprends le projet, commence par là.

## Sommaire

- [Ce que fait l'application](#ce-que-fait-lapplication)
- [Technologies](#technologies)
- [Lancer le projet](#lancer-le-projet)
- [Les modules](#les-modules)
- [Où est rangé le code](#où-est-rangé-le-code)
- [Les couches](#les-couches)
- [Comment arrivent les données](#comment-arrivent-les-données)
- [Comment marche un écran (MVI)](#comment-marche-un-écran-mvi)
- [Le matériel et Android : les Managers](#le-matériel-et-android--les-managers)
- [Le projet Android](#le-projet-android)
- [Choix d'architecture](#choix-darchitecture)
- [Ajouter quelque chose](#ajouter-quelque-chose)
- [Conventions](#conventions)
- [Limites connues](#limites-connues)

## Ce que fait l'application

| Fonctionnalité | Ce que voit l'utilisateur | Comment ça marche |
|---|---|---|
| **Liste des personnages** | La liste de tous les personnages, avec leur avatar. | `CharactersScreen` observe la table Realm des personnages. Si elle est vide, le repository charge la première page de l'API et l'enregistre : la liste se met à jour toute seule. |
| **Détail d'un personnage** | L'avatar en grand, le nom, et une carte avec son dernier lieu connu (nom et type). | `CharacterDetailsViewModel` demande le personnage au repository. Le lieu est un `LocationPreview`, chargé à part (voir plus bas). Si le lieu est inconnu, la carte n'est pas affichée. |
| **Clic sur la carte du lieu** | Le téléphone vibre, puis l'écran du lieu s'ouvre. | La carte envoie l'action `SelectedLocation`. Le ViewModel appelle `VibrationManager`, puis envoie `Destination.LocationDetails` à l'écran, qui navigue. |
| **Détail d'un lieu** | Le nom et le type du lieu, puis la liste de ses résidents. | Module `features:locations`. `LocationDetailsViewModel` demande le lieu complet à `LocationRepository`. L'écran affiche un chargement, une erreur, « Personne n'habite ici » ou la liste. |
| **Clic sur l'avatar** | L'image du personnage s'ouvre en grand dans le navigateur. | Action `SelectedAvatar`. Le ViewModel appelle `LinkManager`, qui lance un `Intent` `ACTION_VIEW` : c'est Android qui choisit l'application qui ouvre l'image. |
| **Langue et mode sombre** | L'application est en français ou en anglais selon le téléphone, et passe en sombre si le téléphone l'est. | Les textes sont dans `values` et `values-fr`. Les couleurs viennent du thème (`LeTheme`), qui suit le réglage du téléphone. |

## Technologies

| Outil | Version | À quoi il sert |
|---|---|---|
| Kotlin | 1.8.10 | Le langage. |
| Android Gradle Plugin | 8.1.4 | Construire l'application. |
| Jetpack Compose (BOM) | 2023.03.00 | Les écrans, en code déclaratif. |
| Navigation Compose | 2.5.3 | Passer d'un écran à l'autre dans une seule activité. |
| Koin | 3.5.0 | L'injection de dépendances : chaque classe reçoit ce dont elle a besoin. |
| Ktor (moteur CIO) | 2.3.4 | Les appels à l'API. |
| kotlinx.serialization | 1.5.1 | Transformer le JSON de l'API en objets Kotlin. |
| Realm Kotlin | 1.11.0 | La base de données locale. |
| DataStore | 1.0.0 | Retenir la prochaine page de personnages à charger. |
| Coil | 2.3.0 | Afficher les images depuis une URL. |
| Coroutines | 1.7.2 | Faire les chargements en arrière-plan. |

Toutes ces versions sont dans `gradle.properties`, et nulle part ailleurs.

## Lancer le projet

Ce qu'il faut :

- Android Studio, avec un **JDK 17** pour Gradle (**Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**). AGP 8.1 et Kotlin 1.8 ne tournent pas avec un JDK plus récent.
- Un émulateur ou un téléphone en Android 9 minimum (`minSdk 28`, `targetSdk 34`), avec un accès à Internet.

Ensuite, ouvre le dossier du projet dans Android Studio, attends la synchronisation Gradle et lance la configuration **app**. En ligne de commande :

```bash
./gradlew assembleDebug
```

Pour vérifier qu'il n'y a ni erreur ni avertissement, dans tous les modules :

```bash
./gradlew compileDebugKotlin lintDebug --rerun-tasks
```

Il n'y a pas encore de tests automatiques dans le projet.

## Les modules

Le projet est découpé en six modules Gradle. Chaque flèche veut dire « dépend de » : les dépendances vont toujours vers le Domain, jamais dans l'autre sens.

```
                    app
     (MainActivity, NavHost, démarrage de Koin)
          │                 │
          ▼                 ▼
 features:characters   features:locations
 (liste + détail       (détail d'un lieu)
  d'un personnage)
          │                 │
          └──────┬──────────┘
                 ▼
     core:ui ─────────► core:domain ◄───────── core:data
 (ViewModel de base,   (modèles, interfaces    (Ktor, Realm,
  navigation, thème,    Repository et Manager,  repositories,
  composants partagés,  aucun framework)        managers)
  textes)
```

| Module | Rôle |
|---|---|
| `app` | Le point d'entrée. `CleanRmApiApplication` démarre Koin, `MainActivity` est la seule activité et porte le `NavHost` avec toutes les destinations. C'est le seul module qui connaît tous les autres. |
| `core:domain` | Le cœur métier : les modèles (`Character`, `Location`, `LocationPreview`) et les interfaces (`CharacterRepository`, `LocationRepository`, `VibrationManager`, `LinkManager`). Il ne connaît ni Android, ni Ktor, ni Realm, ni Compose. |
| `core:data` | Les implémentations : appels réseau avec Ktor, base locale avec Realm, repositories et managers. C'est le seul module qui touche au réseau, à la base et au matériel. |
| `core:ui` | Ce que tous les écrans partagent : le `ViewModel<State>` de base, les `Destination` de navigation, le thème, les textes et les composants réutilisables (`CharacterCard`, `LocationCard`, `BackTopBar`, `ErrorMessage`). |
| `features:characters` | Les écrans des personnages : un package par écran (`list`, `details`). |
| `features:locations` | L'écran de détail d'un lieu (`details`). |

Une feature ne dépend jamais d'une autre feature, ni de `core:data`. Elle n'utilise que `core:domain` pour les données et `core:ui` pour l'affichage. C'est pour ça que les cartes affichées à la fois dans les personnages et dans les lieux sont rangées dans `core:ui`.

## Où est rangé le code

Le code de chaque module est dans `src/main/java/org/mathieu/<module>/` :

```
app/            cleanrmapi/        MainActivity, CleanRmApiApplication
core/domain/    models/            Character, Location, LocationPreview
                repositories/      les interfaces CharacterRepository, LocationRepository
                managers/          les interfaces VibrationManager, LinkManager
core/data/      remote/            les Services (XxxApi) et le client Ktor
                remote/responses/  les DTO (XxxResponse)
                local/             la base (RMDatabase) et les DAO (XxxLocal)
                local/objects/     les Entity (XxxObject) et leurs mappers
                repositories/      les XxxRepositoryImpl
                managers/          les XxxManagerImpl et les extensions de Context
                di/                dataModule (Koin)
core/ui/        composables/       les composants partagés
                theme/             les couleurs et le thème
                                   Navigation.kt, ViewModel.kt
                res/values(-fr)/   les textes
features/xxx/   <écran>/           XxxScreen.kt et XxxViewModel.kt
```

## Les couches

- **Domain** : il dit *ce dont l'application a besoin*. Par exemple, `LocationRepository.getLocation(id)` promet un lieu avec ses résidents, sans dire d'où il vient. Comme les ViewModels ne connaissent que ces interfaces, on peut changer de base ou d'API sans toucher à l'UI.
- **Data** : il dit *d'où viennent les données*. Chaque donnée existe sous trois formes, chacune dans sa couche :

| Forme | Exemple | Rôle |
|---|---|---|
| Réponse de l'API (DTO) | `LocationResponse` | La forme exacte du JSON, lue par le Service `LocationApi`. |
| Objet Realm (Entity) | `LocationObject` | La forme enregistrée en base, lue et écrite par le DAO `LocationLocal`. |
| Modèle du Domain | `Location`, `LocationPreview` | Ce que manipule le reste de l'application. |

Le passage d'une forme à l'autre se fait par des mappers, des fonctions d'extension rangées à côté de l'objet Realm (`toRealmObject()`, `toModel()`, `toPreview()` dans `LocationObject.kt`).

- **UI** : les écrans, leurs ViewModels et leurs états. Elle ne voit que le Domain.

## Comment arrivent les données

Les repositories cherchent toujours **en local d'abord** : l'API ne sert que pour ce qui n'a jamais été enregistré. Exemple avec un lieu (`LocationRepositoryImpl.getLocationObject`) :

```
getLocation(id)
   │
   ├─► Realm (LocationLocal) ── trouvé ──────────────────────────► mapping ─► Location
   │
   └─ pas trouvé ─► API (LocationApi) ── trouvé ─► insertion en base ─► mapping ─► Location
                          │
                          └─ pas trouvé ─► exception « Location not found. »
```

Les résidents d'un lieu suivent la même idée : on lit en base ceux qui y sont déjà, en une seule requête, puis on demande tous les autres à l'API en un seul appel (`character/[1,2,3]`), et on les enregistre pour la prochaine fois.

La liste des personnages, elle, est **observée** : `CharacterLocal.getCharacters()` renvoie un `Flow` Realm, donc chaque page enregistrée remet l'écran à jour sans rien redemander. La prochaine page à charger est retenue dans un DataStore.

## Comment marche un écran (MVI)

Chaque écran suit le même chemin, toujours dans le même sens :

```
 Screen ──(action)──► ViewModel.handleAction ──► Repository / Manager
   ▲                        │
   │                        ├─ updateState { copy(...) } ──► State ──► l'écran se redessine
   │                        │
   └──── navigate ◄─────────┴─ sendEvent(Destination) ──► events (une seule fois)
```

- **L'écran** (`XxxScreen`) collecte `state`, transmet les clics à `handleAction` et écoute `events` pour naviguer. Son `Content` privé ne reçoit que l'état et des callbacks : il affiche, il ne décide rien.
- **Le ViewModel** (`XxxViewModel`, qui hérite de `ViewModel<State>`) reçoit une action, appelle le repository ou un manager, puis met à jour l'état avec `updateState` ou envoie un événement avec `sendEvent`.
- **L'état** (`XxxState`) est une `data class` qui contient exactement ce que l'écran affiche.
- **Les événements** passent par un canal, pas par l'état : une navigation ne doit se faire qu'une fois, même si l'écran se recompose.

Exemple complet avec le clic sur la carte du lieu :

```
LocationCard (clic)
  └─► onAction(CharacterDetailsAction.SelectedLocation)
        └─► CharacterDetailsViewModel.handleAction
              ├─► vibrationManager.vibrate(50)
              └─► sendEvent(Destination.LocationDetails("3"))
                    └─► CharacterDetailsScreen écoute events
                          └─► navController.navigate(...) ──► LocationDetailsScreen
```

**Navigation** : il n'y a qu'une activité (`MainActivity`). Chaque écran est une `Destination` déclarée dans `core:ui/Navigation.kt` et enregistrée dans le `NavHost` de `MainActivity`. Seul l'identifiant voyage dans la route (`locationDetail/3`) : l'écran recharge ses données lui-même.

## Le matériel et Android : les Managers

L'UI ne touche jamais au matériel. Elle passe par un **Manager** : l'interface est dans le Domain, l'implémentation dans Data, et Koin donne à l'implémentation le `Context` de l'application.

```
CharacterDetailsViewModel ──► VibrationManager (core:domain, interface)
                                     ▲
                              VibrationManagerImpl (core:data)
                                     │  reçoit le Context par Koin
                                     ▼
                     context.hasPermission(VIBRATE) puis context.vibrator()
                                     │
                                     ▼
                            service de vibration d'Android
```

| Manager | Ce qu'il fait | Ce qu'il utilise |
|---|---|---|
| `VibrationManager` | Fait vibrer 50 ms au clic sur la carte d'un lieu. Ne fait rien sans vibreur ni permission. | La permission normale `VIBRATE` (déclarée et commentée dans le Manifest de `app`), et deux extensions de `Context` : `hasPermission()` et `vibrator()`, qui cache la différence d'API apparue avec Android 12. |
| `LinkManager` | Ouvre une adresse dans une autre application. | Un `Intent` implicite `ACTION_VIEW` avec `FLAG_ACTIVITY_NEW_TASK`, obligatoire quand on lance depuis le `Context` de l'application. |

La base locale Realm est la deuxième capacité native utilisée par l'application, elle aussi rangée dans `core:data`.

Le `Context` donné aux Managers est celui de l'application et pas celui d'une activité : il vit aussi longtemps que le Manager, alors que celui d'une activité est détruit à chaque rotation d'écran.

## Le projet Android

- **Manifest** (`app/src/main/AndroidManifest.xml`) : c'est le seul Manifest du projet. Chaque permission et chaque composant a un commentaire qui dit pourquoi il est là.
- **Gradle** : toutes les versions sont dans `gradle.properties`, et nulle part ailleurs. Les plugins y lisent aussi leur version, via le `pluginManagement` de `settings.gradle.kts`. Chaque fichier Gradle est découpé en `// region`.
- **Cycle de vie** : les écrans lancent leur chargement dans un `LaunchedEffect(id)`, pour ne le faire qu'à l'entrée de l'écran et pas à chaque recomposition. L'écoute des événements est dans un `LaunchedEffect(viewModel)`, annulé automatiquement quand on quitte l'écran.
- **Ressources** : aucun texte en dur. Tous les textes des écrans sont dans `core/ui/src/main/res/values` (anglais) et `values-fr` (français).
- **Lint** : `lint.xml` ignore seulement les alertes « une version plus récente existe », parce que monter AGP ou Kotlin demanderait de migrer tout le projet vers Kotlin 2. Le seul `@SuppressLint` du code est sur `VibrationManagerImpl.vibrate` : la permission y est bien vérifiée, mais par l'extension `hasPermission`, que Lint ne sait pas lire.

## Choix d'architecture

Quelques décisions qui ne se devinent pas en lisant le code :

- **`LocationPreview` à part de `Location`.** Un personnage n'a besoin que de présenter son lieu. La forme complète chargerait tous les résidents, eux-mêmes des personnages. Une `Location` avec une liste vide aurait voulu dire « personne n'habite ici », ce qui serait faux.
- **`Character.location` peut être `null`.** Certains personnages n'ont pas de dernier lieu connu (l'API répond « unknown »). Un faux lieu vide obligerait chaque écran à le reconnaître.
- **`Character.origin` reste un couple nom / identifiant.** Rien ne l'affiche en détail, et en faire un `LocationPreview` coûterait un appel de plus par personnage. Il suffira de changer son type comme pour `location` le jour où il le faudra.
- **`LocationRepositoryImpl` n'utilise pas `CharacterRepository`.** `CharacterRepositoryImpl` dépend déjà de `LocationRepository` pour remplir `location`, et Koin ne sait pas construire deux classes qui ont besoin l'une de l'autre. Les résidents passent donc directement par `CharacterApi` et `CharacterLocal`.
- **La pagination repart de la page 1 quand la base est vide.** Realm efface la base à chaque changement de schéma, alors que le DataStore garde la page suivante : sans ça, les premières pages ne seraient jamais rechargées.
- **Les cartes, la barre du haut et le message d'erreur sont dans `core:ui`.** Plusieurs features les affichent, et une feature ne peut pas utiliser le code d'une autre.
- **Le Manager vérifie lui-même la permission.** Même si `VIBRATE` est toujours accordée une fois déclarée, le Manager ne vibre pas si elle manque : pas de crash si quelqu'un l'enlève du Manifest.

## Ajouter quelque chose

**Un écran dans une feature existante** : créer un package avec `XxxScreen.kt` (écran public, `Content` privé, `@Preview`) et `XxxViewModel.kt` (le ViewModel, son `XxxState` et sa `sealed interface XxxAction`), sur le modèle de `CharacterDetails`. Ajouter sa `Destination` dans `core:ui/Navigation.kt`, puis son `composable(...)` dans le `NavHost` de `MainActivity`. Les textes vont dans les deux fichiers `strings.xml`.

**Une nouvelle donnée** : dans `core:domain`, le modèle et l'interface du repository. Dans `core:data`, la réponse de l'API et son Service, l'objet Realm (à ajouter au schéma de `RMDatabase`, en augmentant `schemaVersion`) avec son `XxxLocal` et ses mappers, puis le `XxxRepositoryImpl`. Tout se déclare dans `dataModule`.

**Une nouvelle feature** : un module `features:xxx`, avec un `build.gradle.kts` copié de `features:locations`, ajouté dans `settings.gradle.kts` et dans les dépendances de `app`.

**Un accès au matériel** : une interface `XxxManager` dans `core:domain/managers`, son implémentation dans `core:data/managers` qui reçoit le `Context`, déclarée dans `dataModule`. La permission se déclare dans le Manifest de `app`, avec un commentaire. Une permission dangereuse (caméra, localisation…) doit en plus être demandée à l'utilisateur pendant que l'app tourne.

**Une nouvelle bibliothèque** : sa version va dans `gradle.properties`, lue dans le `build.gradle.kts` par `val xxxVersion: String by project`.

## Conventions

- **Noms** : un écran s'appelle comme ce qu'il montre (`LocationDetailsScreen`), avec son `LocationDetailsViewModel` et son `LocationDetailsState`. Une action dit ce que l'utilisateur a fait (`SelectedLocation`).
- **Textes** : `stringResource(R.string.xxx)`, ajoutés en même temps dans `values` et `values-fr`, avec le nom de l'écran en préfixe (`location_details_error`).
- **Commentaires** : en français, ils disent *pourquoi*, pas ce que fait la ligne. Chaque classe et chaque fonction un peu complexe a sa KDoc avec ses balises (`@param`, `@return`, `@receiver`…).
- **Aucun avertissement** : la commande de vérification plus haut doit rester propre.

## Limites connues

- **Il faut Internet au premier lancement.** Tant qu'un personnage ou un lieu n'a jamais été chargé, il n'est pas en base. Hors ligne, si le lieu d'un personnage n'a jamais été chargé, toute la liste passe en erreur.
- **La base est vidée à chaque changement de schéma** (`deleteRealmIfMigrationNeeded`). C'est sans risque ici puisque tout se recharge depuis l'API, mais une vraie application ferait une migration.
- **`origin` n'est pas détaillé** : on ne connaît que son nom et son identifiant.
- **Pas de tests automatiques** pour l'instant.
