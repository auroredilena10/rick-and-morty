# Clean RmApi + MVI

Projet Android en Kotlin (Jetpack Compose, Koin, Ktor, Realm, Navigation Compose) qui consomme l'API Rick et Morty, étendu pour le sujet « Sandbox » (voir `consignes.md`). Ce n'est **pas** un projet multiplateforme : tout le code est dans `src/main`. Écrire pour le développeur qui reprendra le code.

**Ordre de priorité, toujours :** 1. la **grille d'évaluation** (`.claude/skills/audit-projet/references/grille.md`), en visant le niveau Maîtrisé de chaque critère ; 2. le sujet (`consignes.md`) ; 3. les exigences ci-dessous, qui traduisent la grille pour ce projet. Si une demande en contredit une, le dire **avant** d'agir et proposer la version conforme.

## Au début de la session
1. Lire `consignes.md` (épreuves, structure du code existant, points à trancher), puis `git log --oneline -10` et `git branch --show-current`.
2. Travailler **une épreuve à la fois**, dans l'ordre 1, 2, 3 : ne commencer la suivante qu'après validation de l'utilisatrice.
3. Avant d'écrire, ouvrir **un fichier existant du même type** que celui à créer et reprendre ses noms, son dossier, son style.
4. Architecture imposée par le projet : `docs/ui-architecture.md` avant de toucher à l'UI ; `settings.gradle.kts` et le `build.gradle.kts` du module équivalent avant de créer un module.

## Plan avant toute action
- Avant de créer ou modifier un fichier, de lancer une commande ou de changer de sujet, présenter un **plan numéroté par étapes**, puis **attendre l'accord de l'utilisatrice** avant la première étape. Une étape à la fois : après chacune, dire ce qui est fait et vérifier avant la suivante.
- Chaque étape tient en quatre lignes : **quoi** (une action), **à quoi ça sert** (en langage simple, l'utilisatrice débute en Kotlin), **comment** (fichiers touchés, commande) et **comment vérifier**.
- Une question seule ou une lecture de fichiers ne demande pas de plan.

## Suivre l'architecture existante, ajouter le nécessaire
- Tout fichier neuf suit l'architecture du projet : mêmes modules, dossiers, noms et patterns que l'existant. Chaîne UI : `Screen → ViewModel (handleAction) → Repository`, retour par `State` et événements (`sendEvent`). Un nouvel écran a la structure de `CharacterDetails*` (`XxxScreen.kt` avec `Content` privé et `@Preview`, `XxxViewModel.kt` avec son `XxxState`), un nouveau repository celle de `CharacterRepositoryImpl` (avec `XxxApi`, `XxxResponse`, `XxxLocal`, `XxxObject`), une nouvelle feature celle de `features:characters` (un module, un package par écran). Noms à confirmer en lisant le code.
- N'ajouter que ce que l'épreuve et les exigences demandent : modèle, API, objet Realm, Manager, extension de `Context`, commentaires, Previews, strings. Aucun outil remplacé (Realm reste Realm), aucune réorganisation sans demande.
- Si le code existant contredit une exigence, corriger seulement ce point, au plus petit.

## Exigences : la grille traduite pour ce projet
Règles de code détaillées : `.claude/rules/regles-code.md` (chargé dès qu'un `.kt`, un Gradle, le Manifest ou une ressource est lu). Exemples par couche : skill `developpement-android`. À ne jamais perdre de vue :
- **Couches** : `core:domain` (modèles, contrats de Repository et de Manager, aucun framework) ← `core:data` (Ktor, Realm, implémentations) ; `features:*` dépend de `core:domain` et `core:ui`, jamais de `core:data` ; `app` démarre Koin (`CleanRmApiApplication`) et porte le `NavHost`. Dépendances **vers l'intérieur**. Koin uniquement, Hilt et Dagger interdits.
- **Data** : fetch **local d'abord** (Realm), sinon distant, insertion en base, mapping, sinon erreur, comme `CharacterRepositoryImpl.getCharacter`.
- **UI** : MVI du projet (`Screen → ViewModel.handleAction → updateState / sendEvent`), Single Activity (`MainActivity`), une `Destination` par écran, aucun texte en dur (`R.string`, `values` + `values-fr`).
- **Android** : au moins **deux capacités natives** dans des Managers, une **extension de `Context`** utilisée par un Manager, un **`Intent` Android réel**, un usage du **cycle de vie** commenté ; zéro avertissement (Manifest, Gradle, compilation).
- **DX** : KDoc avec balises sur les composants complexes, commentaires qui disent pourquoi ; le `README.md` (épreuve 3) schématise les couches et le flux UI. Aucune mention d'IA dans le code, le README ou les commits (ni `Co-Authored-By` ni « Generated with »). Le programme doit **build** ; sinon, mocker la partie bloquante.

## Skills (chargés à la demande)

| Skill | Pour quoi |
|---|---|
| `developpement-android` | Toute demande qui touche au code : ordre de travail d'une épreuve ou feature, garde-fous, `references/` (exemples par couche, KDoc, Manifest et Gradle, capacités natives, index de la doc personnelle). |
| `audit-projet` | Vérifier le projet critère par critère de la grille (`references/grille.md`) avec preuves (`fichier:ligne`) et lister les écarts. Seul endroit où le projet est compilé. |

## Commandes

```bash
bash .claude/skills/audit-projet/scripts/audit_projet.sh .            # audit automatique (lecture seule)
```
La commande de compilation est dans le skill `audit-projet` : elle compile tous les modules et lance le Lint, avec `--rerun-tasks`, sinon les avertissements des tâches à jour ne s'affichent pas.

### Commandes sobres (chaque sortie reste dans le contexte et est relue à chaque tour)
- **Compilation** : seulement pendant un audit (skill `audit-projet`) ou quand l'utilisatrice la demande explicitement. Jamais entre deux étapes, jamais pour un commentaire ou de la documentation.
- **Fin de chaque épreuve** : proposer l'audit, qui compile et lit les avertissements (ils comptent). **Fin de l'évaluation** : audit final.
- **Une compilation échoue** : lire toutes les erreurs d'un coup, les corriger ensemble, puis relancer une seule fois. Même échec deux fois : s'arrêter et expliquer.
- Ne pas relancer une commande réussie si rien n'a changé.
- Sortie longue : `-q`, redirection vers `build/last.log`, puis `grep -n -A 5 -E "^e: |^w: |FAILED|warning:" build/last.log | head -60`. Git : `status -sb`, `diff --stat`, `log --oneline -10`.

## Règles de travail
- **Une seule session pour toute l'évaluation**, une épreuve à la fois. Une demande sans rapport avec l'évaluation se signale sans la traiter. La session est longue : lire les fichiers par plages (`grep` puis lignes ciblées) et ne jamais relire un fichier déjà lu. Pour une notion du cours, lire `.claude/skills/developpement-android/references/doc-index.md` puis 1 à 3 fichiers de la doc, jamais tout, et répondre par un schéma court (`A → B`).
- **Après chaque épreuve** : résumer à l'utilisatrice ce qui est fait, les décisions prises et ce qui reste. Après une compaction du contexte, relire `consignes.md` et `git log` pour retrouver l'état.
- Ne jamais déclarer une exigence « respectée » sans preuve (fichier et ligne, ou sortie de commande).
- Pas de commit, de push ni de modification de fichier de réglages sans demande explicite.
- Préférences personnelles : `CLAUDE.local.md` (non versionné).
