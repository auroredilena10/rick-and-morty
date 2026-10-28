---
name: developpement-android
description: "Guide l'écriture, la modification et la relecture du code Kotlin Android de ce projet : épreuve ou feature complète, écran (Screen, Content, ViewModel, State, Action), Repository, objet Realm et API Ktor, Manager et capacité native Android, navigation, Manifest, Gradle, ressources, KDoc. À utiliser pour toute demande qui touche au code (« fais l'épreuve N », « ajoute l'écran X », « branche l'API », « fais vibrer au clic », « commente », « corrige l'avertissement », « explique l'architecture »)."
---

# Développement Android (Kotlin, Jetpack Compose)

**La grille d'évaluation prime toujours** ([../audit-projet/references/grille.md](../audit-projet/references/grille.md)) : viser le niveau Maîtrisé de chaque critère. Viennent ensuite le sujet (`consignes.md`) et `CLAUDE.md`. Ce skill donne l'ordre de travail ; le détail est dans `references/` (lire seulement le fichier utile, chacun commence par un sommaire).

## Suivre l'architecture existante, ajouter le nécessaire
Chaque fichier neuf reprend les noms, le dossier et les patterns d'un fichier existant du même type : le code neuf est indifférencié de l'existant. Ajouter uniquement ce que l'épreuve et les exigences demandent (modèle, API, objet Realm, Manager, extension de `Context`, commentaires, Previews, strings) ; ni refonte ni nouvel outil.

## 0. Avant d'écrire
- Lire `consignes.md`, puis `docs/ui-architecture.md` si l'UI est concernée.
- Ouvrir **un seul** fichier existant du même type (autre écran, repository, objet Realm, `build.gradle.kts`).
- Présenter le plan par étapes (format de `CLAUDE.md`) en y précisant : modèle métier, écrans, états (chargement, vide, erreur, données), sources (API, Realm), capacité native. Attendre l'accord avant l'étape 1. Une seule question groupée si la consigne ne suffit pas.

## 1. Écrire (copier cette liste dans la réponse et cocher ; aucun build entre les étapes)
- [ ] **1. Domain** (`core:domain`) : modèle dans `models/<sujet>/`, `interface XxxRepository` dans `repositories/`. Aucun import `android.*`, Ktor, Realm, Compose. En-tête qui explique la logique métier. → `couches.md`
- [ ] **2. Data** (`core:data`) : `XxxResponse` (`remote/responses/`) et `XxxApi` (`remote/`, Ktor), `XxxObject : RealmObject` (`local/objects/`, ajouté au schéma de `RMDatabase`) et `XxxLocal` (`local/`), mappers en fonctions d'extension à côté de l'objet, `XxxRepositoryImpl` (`repositories/`) : **local d'abord ; sinon distant, insertion en base, mapping ; sinon erreur**. → `couches.md`
- [ ] **3. Capacité native** (si utilisée) : `interface XxxManager` dans `core:domain` (`managers/`), `XxxManagerImpl` dans `core:data` (`managers/`) qui reçoit le `Context` par Koin, extension de `Context` utilisée par le Manager. Permission **normale** : déclarée et commentée ; **dangereuse** : déclarée et demandée à l'exécution. → `capacites-natives.md`
- [ ] **4. Koin** : chaque `XxxApi`, `XxxLocal`, `XxxRepository` et `XxxManager` déclaré dans `dataModule` (`core:data/di/DataModule.kt`) ; les ViewModels les reçoivent par `by inject()`.
- [ ] **5. Navigation** : nouvelle `Destination` dans `core:ui/Navigation.kt` (arguments par `navArgument`), `composable(...)` ajouté au `NavHost` de `MainActivity`. Le ViewModel navigue par `sendEvent(Destination.Xxx(...))`, l'écran écoute ses `events`.
- [ ] **6. Présentation** (`features:<feature>/<écran>/`) : `XxxViewModel` (hérite de `ViewModel<XxxState>`, `sealed interface XxxAction` + `handleAction`), `XxxState` (`data class` avec la représentation visuelle), `XxxScreen` (collecte `state`, écoute `events`, `Content` privé, `@Preview` avec `PreviewContent`). → `data-ui.md`
- [ ] **7. Textes et Manifest** : `core/ui/src/main/res/values/strings.xml` et `values-fr/strings.xml`, `stringResource(R.string.xxx)` (`org.mathieu.ui.R`) ; un commentaire par permission et par composant du Manifest. → `projet-android.md`
- [ ] **8. Gradle** (nouveau module) : `include` dans `settings.gradle.kts`, `build.gradle.kts` copié de `features:characters`, versions lues dans `gradle.properties`. → `projet-android.md`
- [ ] **9. Commentaires** : KDoc avec balises sur chaque composant complexe ; au moins un usage judicieux du cycle de vie mis en avant par un commentaire spécifique. → `kdoc.md`

Le projet n'a pas de tests au départ : n'en ajouter que si la configuration de test du module existe déjà.

## 2. Garde-fous
- Une feature ne dépend ni de `core:data` ni d'une autre feature : elle utilise `core:domain` et `core:ui`.
- Un modèle, une réponse API et un objet Realm sont trois classes distinctes, reliées par des mappers.
- Un composable partagé de `core:ui` ne reçoit jamais un type du domain : seulement des types simples ou son propre état.
- Un nouvel objet Realm s'ajoute au schéma de `RMDatabase` et augmente `schemaVersion` ; la base est alors effacée (`deleteRealmIfMigrationNeeded`).

## 3. Où lire le détail (`references/`)
| Besoin | Fichier |
|---|---|
| Grille d'évaluation (prime toujours) | `.claude/skills/audit-projet/references/grille.md` |
| Exemples domain, data, Realm, Koin | `couches.md` |
| Chaîne UI complète (écran, ViewModel, état, action), navigation, Preview | `data-ui.md` |
| Manager, permissions, extension de `Context`, `Intent` Android | `capacites-natives.md` |
| Modèles de KDoc | `kdoc.md` |
| Manifest, Gradle, versions, ressources, cycle de vie, avertissements | `projet-android.md` |
| Pas à pas détaillé et notions du cours (doc personnelle, hors projet) | `doc-index.md` |

## 4. Vérifier, corriger, revérifier
Aucun build pendant l'écriture : la compilation n'a lieu que dans le skill `audit-projet` (avec `--rerun-tasks`) ou sur demande explicite de l'utilisatrice. Fin d'épreuve : proposer l'audit. S'il signale des écarts, les corriger tous, puis relancer l'audit, qui recompile. S'arrêter quand il ne reste que des points « non vérifiables ».

## 5. Rendre compte
Fichiers créés par couche, résultat de la vérification, ce qui n'a pas pu être vérifié. Le `README.md` n'existe pas : c'est l'épreuve 3 qui le crée, et son schéma montre alors chaque flux et chaque capacité native ajoutés. Terminer par 3 lignes « à expliquer à l'oral » : rôle de la feature dans les couches, son flux (écran → ViewModel → Repository → State → écran), son lien avec le domain.
