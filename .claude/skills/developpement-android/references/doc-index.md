# Index de la doc personnelle (lecture seule)

Base : `C:\Users\auror\Documents\documentation\brain_bean\doc_kotlin` (hors projet, **ne jamais modifier**). La grille d'évaluation prime toujours (`../../audit-projet/references/grille.md`), puis le sujet et `CLAUDE.md` ; cette doc explique le comment.

## Méthode (coût minimal)
1. Prendre **1 à 3 fichiers** dans la table ci-dessous. Jamais un dossier entier, jamais tout.
2. Fichier marqué ★ (plus de 2 000 tokens) : `grep -n "^#"` pour les titres, puis lire seulement la plage utile.
3. Question précise : `grep -rn -i "mot" <base>` plutôt que d'ouvrir un fichier.
4. Ne pas relire un fichier déjà lu dans la session.

## Quelle tâche, quels fichiers (chemins relatifs à la base)
| Tâche | Fichiers |
|---|---|
| Écran, ViewModel, état | `architecture/04 La couche UI (MVI et UDF).md` ★, `architecture/05 Koin (injection de dépendances).md`, `compose/04 MainActivity.md` ★ |
| Découpage en composants, Preview | `compose/02 Les composants.md`, `compose/03 Le modifier.md`, `compose/05 Les previews.md` ★ |
| Domain, Repository, mapper, fetch | `architecture/02 La couche Domain.md` ★, `architecture/03 La couche Data.md` ★, `architecture/00 Architecture.md` |
| Capacité native (vibration, GPS…) | `la base/2 système et matériel/04 Les capacités natives (Managers).md` ★, puis `4.1 Le vibreur pas à pas.md` ★ ou `4.2 La localisation pas à pas.md` ★ |
| Permission, Manifest, Intent Android | `la base/2 système et matériel/03 Les Permissions.md`, `la base/1 l'app/01 Le manifest.md`, `la base/2 système et matériel/02 Les Intents.md` |
| Context, extension de `Context` | `la base/2 système et matériel/01 Le Context.md`, `code/04 Les fonctions d'extension.md` ★ |
| KDoc, commentaires | `code/05 KDoc et commentaires.md` ★ |
| Nommage, lint, avertissements | `code/06 Conventions de nommage et Lint.md` ★, `code/03 Les logs.md` |
| Gradle, versions, dépendances | `gradle/01 Les fichiers Gradle.md`, `gradle/02 Ajouter une dépendance.md`, `gradle/03 Le bloc android.md`, `gradle/04 Commandes et erreurs Gradle.md`, `gradle/00 Gradle.md` |
| Textes, langues, mode sombre | `la base/1 l'app/4.1 Les ressources en pratique.md` ★, `la base/1 l'app/04 Les Ressources.md` |
| Cycle de vie, Activity | `la base/1 l'app/03 Le cycle de vie.md` ★, `la base/1 l'app/02 Les Activities.md` |
| Organisation du projet, APK | `la base/1 l'app/06 Organisation du projet.md`, `la base/00 Les composants de base.md`, `la base/1 l'app/05 Classes.dex.md` |
| Bases Kotlin | `code/01 Kotlin vs Java.md`, `code/02 Les classes.md`, `compose/00 Compose.md`, `compose/01 UI déclarative.md`, `architecture/01 Architecture (Screen - ViewModel - Model).md` |

## Où la doc diffère du projet (le projet gagne)
- **MVI** : la doc hésite entre MVI strict et « une fonction par action ». Le projet a sa chaîne `Screen → ViewModel (handleAction) → Repository`, avec `updateState` et `sendEvent` (`docs/ui-architecture.md`) : **la suivre**.
- **État** : l'écran collecte `viewModel.state` par `collectAsState()` et écoute `events` dans un `LaunchedEffect` ; au moins une utilisation judicieuse du cycle de vie est mise en avant par un commentaire spécifique.
- **Base locale** : le projet utilise **Realm** (`RMDatabase`, `XxxLocal`, `XxxObject`), pas Room : garder Realm.
- **Gradle multi-module** : la doc n'en parle pas ; suivre les modules existants (`app`, `core:{domain,data,ui}`, `features:characters`) et les versions de `gradle.properties`.
- **Use case** : « optionnel » dans la doc comme dans le code existant (le ViewModel appelle le Repository) : n'en créer que pour une vraie règle métier.
- **Extension** : au moins une extension de `Context` est exigée, appelée par un Manager de `core:data`.
- Les exemples de la doc viennent d'une autre app : reprendre la démarche, pas les noms.
