# Modèles de KDoc avec balises

Un en-tête = **le rôle en une phrase** (le pourquoi), puis les balises pertinentes. Pas de balise inventée pour « faire plein ». Les commentaires neufs s'écrivent en français, sans traduire l'existant.

## Sommaire
- Composable complexe
- ViewModel
- Repository (Data)
- Interface et modèle (Domain)
- Fonction d'extension
- Manager
- Balises utiles

## Composable complexe

```kotlin
/**
 * Écran qui montre une tâche : il possède le ViewModel, transmet les actions et navigue sur ses événements.
 *
 * @param navController contrôleur du `NavHost` de `MainActivity`.
 * @param id identifiant de la tâche, lu dans les arguments de la destination.
 */
@Composable
fun TaskDetailsScreen(navController: NavController, id: Int)
```

## ViewModel

```kotlin
/**
 * ViewModel de [TaskDetailsScreen] : charge la tâche et décide de ce que veut dire chaque action de l'écran.
 *
 * @param application nécessaire à `AndroidViewModel`, fournie par `viewModel()`.
 */
class TaskDetailsViewModel(application: Application) : ViewModel<TaskDetailsState>(TaskDetailsState(), application)
```

## Repository (Data)

```kotlin
/**
 * Lit d'abord la tâche en base et n'appelle l'API que si elle n'a jamais été enregistrée :
 * la deuxième visite marche même sans réseau.
 *
 * @throws Exception si la tâche n'existe ni en base ni sur l'API : l'appelant doit s'y attendre.
 * @see TaskApi la source distante.
 * @see TaskLocal la source locale.
 */
internal class TaskRepositoryImpl(/* … */) : TaskRepository
```

## Interface et modèle (Domain) : l'intention métier

Dans le Domain, l'en-tête explique **la logique métier** (jamais l'écran ni l'API).

```kotlin
/**
 * Une tâche que l'utilisateur doit faire : « quelque chose à finir, peut-être déjà fait ».
 * Elle ne sait ni où elle est rangée ni comment elle est affichée.
 *
 * @property id identifiant unique, le même que celui du service distant.
 * @property isDone `true` une fois que l'utilisateur l'a terminée.
 */
data class Task(/* … */)
```

## Fonction d'extension (obligatoire : un en-tête KDoc complet)

```kotlin
/**
 * Retrouve l'[Activity] qui héberge ce [Context].
 *
 * En Compose, `LocalContext.current` peut être emballé dans des [ContextWrapper] : on remonte
 * la chaîne des `baseContext` jusqu'à trouver une activité.
 *
 * @receiver le Context de départ (activité, Context à thème, Context d'application de Koin…).
 * @return l'activité hôte, ou `null` si le Context n'est rattaché à aucune activité.
 */
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
```

## Manager

```kotlin
/**
 * Point d'accès unique au vibreur du téléphone : l'UI ne touche jamais directement l'API Android.
 *
 * @see VibrationManagerImpl l'implémentation (`core:data`) qui utilise le Context de l'application.
 */
interface VibrationManager { fun vibrate(durationMs: Long) }
```

## Balises utiles

`@param` (chaque paramètre non évident) · `@property` (propriétés du constructeur d'une `data class`) · `@return` (et ses cas particuliers : `null`, liste vide…) · `@receiver` (extension) · `@throws` (si une exception est voulue) · `@see` (classe liée). Une balise va sur l'élément qui la porte : `@param` et `@return` sur une fonction, jamais sur un `object`.
