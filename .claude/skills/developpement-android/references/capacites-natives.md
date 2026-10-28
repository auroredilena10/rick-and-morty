# Capacités natives : Managers, permissions, extension de Context

## Sommaire
- Principe
- Manager complet (exemple : vibration)
- Permissions
- Fonction d'extension de Context
- Intent Android

## Principe

L'UI reste **simple et sans logique matérielle**. Chaque capacité (vibration, GPS, capteurs, caméra, fichiers, base de données) s'accède par un **Manager** : interface `XxxManager` dans `core:domain` (`managers/`, aucun import Android), implémentation `XxxManagerImpl` dans `core:data` (`managers/`) qui reçoit le `Context` par Koin. C'est le même schéma que `CharacterRepository` / `CharacterRepositoryImpl` : les features ne voient que l'interface.

```
ViewModel (features) ──▶ interface VibrationManager (core:domain) ◀── VibrationManagerImpl (core:data, reçoit le Context)
```

## Manager complet (exemple : vibration)

```kotlin
// core:domain managers/ : aucun import Android
/** Point d'accès unique au vibreur, pour qu'aucun écran ne touche à l'API Android. */
interface VibrationManager {
    /** Fait vibrer pendant [durationMs] millisecondes ; ne fait rien si l'appareil n'a pas de vibreur. */
    fun vibrate(durationMs: Long)
}

// core:data managers/ : le Context est injecté par Koin (androidContext()), jamais passé par l'écran
internal class VibrationManagerImpl(private val context: Context) : VibrationManager {
    override fun vibrate(durationMs: Long) {
        context.vibrator().vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
```

Branchement dans `dataModule` :

```kotlin
single<VibrationManager> { VibrationManagerImpl(androidContext()) }
```

Le ViewModel qui reçoit le clic dans `handleAction` appelle `vibrationManager.vibrate(...)` (`by inject()`), puis navigue par `sendEvent`. Le composable ne connaît pas le Manager.

## Permissions

**Normale** (`VIBRATE`, `INTERNET`) : déclarée dans `app/src/main/AndroidManifest.xml` avec un commentaire qui dit pourquoi ; Android ne demande rien à l'utilisateur, mais sans la déclaration l'appel est bloqué.

**Dangereuse** (localisation, caméra, notifications sur Android 13+) : déclarée **et** demandée à l'exécution, depuis l'UI (`MainActivity` ou écran) :

```kotlin
// Cycle de vie : la demande se fait au démarrage de l'activité, avant que la capacité soit utilisée.
private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
```

## Fonction d'extension de Context

Exigée : **au moins une** extension de `Context`, appelée par un Manager (dans `core:data`, à côté du Manager). Elle cache le `getSystemService` et la différence de version Android, avec son header KDoc complet (modèle dans `kdoc.md`). `minSdk` du projet : 28.

```kotlin
/**
 * Donne le vibreur de l'appareil, en cachant la différence d'API apparue avec Android 12.
 *
 * @receiver n'importe quel Context ; celui de l'application suffit.
 * @return le service de vibration de l'appareil.
 */
internal fun Context.vibrator(): Vibrator =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        getSystemService(VibratorManager::class.java).defaultVibrator
    else
        getSystemService(Vibrator::class.java)
```

## Intent Android

Au moins un `Intent` Android réel (par exemple un partage avec `Intent.ACTION_SEND` et `Intent.createChooser`, ou l'ouverture d'un lien), dans un Manager de `core:data` derrière une interface de `core:domain`. Lancé depuis le `Context` de l'application, il lui faut `Intent.FLAG_ACTIVITY_NEW_TASK`. À ne pas confondre avec les actions d'UI (`XxxAction`).
