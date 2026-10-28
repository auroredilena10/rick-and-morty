---
name: audit-projet
description: Audite un projet Kotlin/Android critère par critère de la grille d'évaluation (architecture, qualité du code, front-end) avec preuves fichier:ligne, puis liste les écarts du plus rentable au moins rentable. À utiliser pour « audite le projet », « est-ce conforme ? », « qu'est-ce qui manque ? », avant une livraison ou une relecture, ou après une grosse modification. Lance un script en lecture seule et ne corrige rien sans demande.
allowed-tools: Bash(bash .claude/skills/audit-projet/scripts/audit_projet.sh *)
---

# Audit du projet

La référence est la **grille d'évaluation** : [references/grille.md](references/grille.md). **Elle prime toujours**, sur le sujet, sur `CLAUDE.md` et sur ce skill ; `CLAUDE.md` ne fait que la traduire pour ce projet. Le but est le niveau **Maîtrisé** de chaque critère.

## Déroulé

1. Lance `bash .claude/skills/audit-projet/scripts/audit_projet.sh .` depuis la racine du projet (lecture seule). Il donne par exigence : OK, A VERIFIER ou MANQUE.
2. Pour chaque ligne A VERIFIER ou MANQUE, lis le critère correspondant dans la grille et le code concerné.
3. Relève des **preuves** (`fichier:ligne`) pour chaque point : ✅ acquis (preuve), ❌ écart (ce qui manque), ❓ non jugeable avec ce qui a été lu (« non vérifié »). Vérifie par un `grep` que chaque nom cité par le code existe vraiment (`android:name` du Manifest, classes liées dans les modules Koin, imports). Ne conclus jamais « tout est conforme » s'il reste un ❓.
4. Compile **tous les modules** et lance le **Lint** (le script ne le fait pas), en une seule commande, avec un JDK 17 (AGP 8.1) :
   ```bash
   ./gradlew compileDebugKotlin :app:lintDebug --rerun-tasks --no-configuration-cache --warning-mode all --continue > build/last.log 2>&1
   ```
   `--no-configuration-cache` : sinon Gradle réutilise sa configuration en cache et n'affiche pas les avertissements de configuration. `--continue` : une erreur n'arrête pas les autres tâches, pour lire toutes les erreurs d'un coup.
   La tâche sans chemin `compileDebugKotlin` s'exécute dans **tous** les modules (`app`, `core:*`, `features:*`). `--rerun-tasks` est obligatoire, car une tâche `UP-TO-DATE` ne réaffiche pas ses avertissements. Résultat attendu : **zéro erreur et zéro avertissement** (aucune ligne `e:`, `w:`, `WARNING`, `warning:`, `deprecated` ni `FAILED`) et un rapport Lint sans erreur ni avertissement (résumé en fin de `lintDebug`) ; une seule ligne rend l'audit non conforme. C'est le seul moment où le projet est compilé, avec une demande explicite de l'utilisatrice.
5. Rends le rapport au format de [references/rapport-audit.md](references/rapport-audit.md), classé par gain. **Ne corrige rien** tant qu'on ne te le demande pas.

## Écarts fréquents

- **Koin** est obligatoire, **Hilt et Dagger sont interdits** : une annotation `@Inject` ou `hiltViewModel()` est un écart net.
- **MVI** est obligatoire, sous la forme du projet : un écran qui appelle le Repository sans passer par son ViewModel (`handleAction`, `updateState`, `sendEvent`), un composable qui navigue ou décide pour l'application, ou un mécanisme parallèle est un écart net.
- **Erreur ou avertissement** dans le Manifest, Gradle ou la compilation : exigence non remplie, même s'il est « inoffensif ». L'audit n'est conforme qu'avec zéro erreur et zéro avertissement.
- **Un seul texte en dur**, même dans un ViewModel, suffit à contredire « utilisation systématique des ressources ».
- **KDoc sans balise** : ne vaut pas « block tags pertinents ».
- Code neuf qui ne ressemble pas à l'existant : exigence « indifférencié » non remplie.
