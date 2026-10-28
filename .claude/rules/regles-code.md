---
paths:
  - "**/*.kt"
  - "**/*.kts"
  - "**/AndroidManifest.xml"
  - "**/res/**/*.xml"
  - "gradle.properties"
---

# Règles de code (la grille d'évaluation prime toujours ; ces règles la traduisent pour ce projet)

Grille : `.claude/skills/audit-projet/references/grille.md`. Détail et exemples : skill `developpement-android` (`references/`).

- **KDoc** sur chaque composant complexe (ViewModel, Screen, Repository, Manager, mapper, extension) : une phrase de rôle, puis `@param`, `@return`, `@property` pertinents. Dans `domain`, l'en-tête explique la logique métier, jamais l'écran ni l'API.
- **Noms explicites** : écran = ce qu'il montre (`CharacterDetailsScreen`) ; ViewModel et état associés (`CharacterDetailsViewModel`, `CharacterDetailsState`) ; action du ViewModel (un cas de `sealed interface XxxAction`, traité par `handleAction`) = ce que l'utilisateur a fait, comme dans le projet (`CharactersAction.SelectedCharacter`).
- **Pas de duplication** : un second bloc identique se factorise, ou se justifie par écrit à l'endroit de la copie. Les composables se découpent comme des LEGO (Screen, Content, composants, Preview).
- **Textes** : `stringResource(R.string.xxx)` avec `res/values/strings.xml` **et** `res/values-fr/strings.xml` modifiés ensemble (textes des écrans dans `core:ui`) ; une apostrophe s'écrit `\'`.
- **Ressources** : nom en minuscules avec le sujet en préfixe (`location_card_type`), jamais `string1`.
- **Gradle** : sections délimitées par `// region …` / `// endregion` ; aucune version en dur dans le code neuf d'un `build.gradle.kts` (version ajoutée à `gradle.properties` avec un nom explicite, lue par `val xxxVersion: String by project`) ; nouveau module : copier le `build.gradle.kts` du module équivalent (`features:characters` pour une feature) et l'ajouter par `include` dans `settings.gradle.kts`.
- **Manifest** (`app/src/main/AndroidManifest.xml`) : un commentaire par composant ou permission qui explique pourquoi ; aucun avertissement. Tout avertissement (compilation, Manifest, Gradle) se corrige à la cause ; `@Suppress` ou `tools:ignore` seulement quand un outil l'impose, avec un commentaire qui dit pourquoi.
- **Cycle de vie** : au moins une utilisation judicieuse (Activity ou Compose : `LaunchedEffect`, `BackHandler`, `onNewIntent`…), mise en avant par un commentaire spécifique qui dit pourquoi ; jamais un usage sans objectif.
- **Extension** : au moins une fonction d'extension de `Context`, appelée par un Manager, avec en-tête KDoc complet (`@receiver`, `@param`, `@return`).
- **Injection : Koin uniquement** (`by inject()` dans les ViewModels, déclarations dans `dataModule` / `domainModule`, démarrage dans `CleanRmApiApplication`). Aucune annotation Hilt, Dagger ni `@Inject`.
- **UI** : écran (collecte `state`, appelle `viewModel::handleAction`, écoute `events` pour naviguer) → `ViewModel<State>` (`updateState`, `fetchData`, `collectData`, `sendEvent`) → Repository. Un composable privé reçoit des données et des callbacks : il ne navigue pas et ne prend aucune décision d'application. Pas de mécanisme parallèle.
- **Android et Managers** : `Context`, `Intent`, services système uniquement dans `core:data` (Managers, repositories) et dans l'UI (`app`, `features`) quand Compose l'impose ; l'interface d'un Manager est dans `core:domain`, son implémentation dans `core:data`.
- **Commentaires** : une phrase qui dit pourquoi, en français pour tout commentaire neuf (l'existant reste en anglais), sans ton d'assistant ; jamais de commentaire qui répète le code.
- **Previews** : un `@Preview` pour chaque `Content` d'écran et composant réutilisable, enveloppé dans `PreviewContent`.
- **Domain pur** : aucun import `android.*`, `io.ktor`, `io.realm`, `androidx.compose` ; une feature n'importe jamais `core:data`.
