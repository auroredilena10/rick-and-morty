# Recettes : Manifest, Gradle, ressources, cycle de vie

## Sommaire
- Manifest sans avertissement, commenté
- Gradle lisible, sans version en dur
- Ressources : textes et variantes
- Commentaire de cycle de vie
- Avertissements : où les voir

## Manifest sans avertissement, commenté

Le Manifest est `app/src/main/AndroidManifest.xml`. Un commentaire **par permission et par composant** qui dit *pourquoi* ; toute permission d'une capacité native y est déclarée.

```xml
<!-- Accès réseau : nécessaire pour interroger l'API Rick et Morty. -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- Permission normale : autorise le Manager de vibration à faire vibrer l'appareil, aucune demande à l'utilisateur. -->
<uses-permission android:name="android.permission.VIBRATE" />

<!--
    Single Activity : tous les écrans sont des destinations du NavHost de cette activité.
    exported="true" car c'est le point d'entrée du lanceur (MAIN / LAUNCHER).
-->
<activity android:name="org.mathieu.cleanrmapi.MainActivity" android:exported="true" />
```

## Gradle lisible, sans version en dur

Les modules utilisent `com.android.library` + `org.jetbrains.kotlin.android` (et `com.android.application` pour `app`). Les versions sont centralisées dans `gradle.properties` et lues par `val xxxVersion: String by project`.

```kotlin
// region Plugins
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
// endregion

// region Dépendances
val koinVersion: String by project

dependencies {
    implementation("io.insert-koin:koin-android:$koinVersion")
}
// endregion
```

`gradle.properties` est le **seul** endroit où figure un numéro de version, avec des noms explicites (`koinVersion`, `ktorVersion`, `realmVersion`). Une nouvelle bibliothèque y ajoute sa ligne ; ne jamais inventer un numéro, vérifier la version réelle.

Un nouveau module copie le `build.gradle.kts` de `features:characters` (`namespace` propre, `compileSdk` et `minSdk` lus par `compileVersion` / `minVersion`) et s'ajoute par `include(":features:x")` dans `settings.gradle.kts`.

## Ressources : textes et variantes

- Textes des écrans : `core/ui/src/main/res/values/strings.xml` (langue par défaut) et `values-fr/strings.xml`, modifiés **ensemble**. Lecture : `stringResource(R.string.xxx)` avec `import org.mathieu.ui.R` (`android.nonTransitiveRClass=true` : chaque module a son propre `R`).
- Au moins **une variante** (`values-fr`, `values-night`…) doit rester vraie pour tout texte ajouté.
- Nom en minuscules avec le sujet en préfixe : `location_card_type`, jamais `title1`. Apostrophe : `\'`.
- Aucun texte visible en dur, **y compris** dans un ViewModel : il renvoie un état ou une erreur, le composable la traduit avec `stringResource`.

## Commentaire de cycle de vie

La grille demande au moins une utilisation **judicieuse** du cycle de vie (Activity ou Compose), mise en avant par un commentaire spécifique ; un usage sans objectif (« logger pour logger ») fait baisser la note. Un préfixe fixe la rend facile à retrouver :

```kotlin
// Cycle de vie : LaunchedEffect(viewModel) est annulé quand l'écran quitte la composition,
// donc les événements du ViewModel ne sont plus écoutés une fois l'écran fermé.
LaunchedEffect(viewModel) { viewModel.events.onEach { /* ... */ }.collect() }
```

## Avertissements : où les voir

La grille interdit tout warning (Manifest, Gradle, compilation) et demande des conventions Kotlin respectées (Lint, critère SE-5). On ne compile que pendant un audit ou sur demande explicite : la commande (tous les modules, Lint compris, avec `--rerun-tasks`) est dans le skill `audit-projet`.
- Compilation : aucune ligne `w:` ni `e:`.
- Lint (`:app:lintDebug`) : aucune erreur ni avertissement dans son résumé.
- Gradle : aucune mention « deprecated » ni `WARNING`.
- Manifest : le Gradle Android signale les erreurs de fusion ; elles se corrigent à la cause.
