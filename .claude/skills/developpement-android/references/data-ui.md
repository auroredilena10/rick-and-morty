# Chaîne UI complète : écran, ViewModel, état, action

Le projet applique `Screen → ViewModel (handleAction) → Repository`, retour par `State` et `events` (`docs/ui-architecture.md`). Exemple générique (« lieux ») : reprendre la structure de `CharacterDetails*` et `Characters*`, pas les noms.

## Sommaire
- État et actions
- ViewModel
- Écran, Content et Preview
- Navigation

## État et actions

L'état décrit ce que l'écran affiche, déjà prêt pour l'affichage. Les actions décrivent ce que l'utilisateur a fait.

```kotlin
/**
 * Ce que l'écran de détail d'un lieu affiche.
 *
 * @property error message à afficher à la place du contenu, `null` si tout va bien.
 */
data class PlaceDetailsState(
    val isLoading: Boolean = true,
    val name: String = "",
    val type: String = "",
    val error: String? = null
)

sealed interface PlaceDetailsAction {
    data object ClickedCard : PlaceDetailsAction
}
```

## ViewModel

Hérite de `ViewModel<State>` (`core:ui`), qui fournit `updateState`, `sendEvent`, `fetchData` et `collectData`. Les dépendances arrivent par `by inject()`.

```kotlin
/**
 * ViewModel de [PlaceDetailsScreen] : charge le lieu et décide de ce que veut dire chaque action de l'écran.
 *
 * @param application nécessaire à `AndroidViewModel`, fournie par `viewModel()`.
 */
class PlaceDetailsViewModel(application: Application) : ViewModel<PlaceDetailsState>(
    PlaceDetailsState(), application) {

    private val placeRepository: PlaceRepository by inject()

    fun init(placeId: Int) {
        fetchData(source = { placeRepository.getPlace(placeId) }) {
            onSuccess { updateState { copy(name = it.name, type = it.type, error = null) } }
            onFailure { updateState { copy(error = it.toString()) } }
            updateState { copy(isLoading = false) }
        }
    }

    fun handleAction(action: PlaceDetailsAction) {
        when (action) {
            PlaceDetailsAction.ClickedCard -> sendEvent(Destination.PlaceResidents(/* id */))
        }
    }
}
```

## Écran, Content et Preview

```kotlin
/**
 * Écran qui montre un lieu : il possède le ViewModel, transmet les actions et navigue sur ses événements.
 *
 * @param navController contrôleur du `NavHost` de `MainActivity`.
 * @param id identifiant du lieu, lu dans les arguments de la destination.
 */
@Composable
fun PlaceDetailsScreen(navController: NavController, id: Int) {
    val viewModel: PlaceDetailsViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    // Cycle de vie : LaunchedEffect(viewModel) est annulé quand l'écran quitte la composition,
    // donc les événements ne sont plus écoutés une fois l'écran fermé.
    LaunchedEffect(viewModel) {
        viewModel.init(placeId = id)
        viewModel.events
            .onEach { event -> if (event is Destination) navController.navigate(event) }
            .collect()
    }

    PlaceDetailsContent(state = state, onAction = viewModel::handleAction)
}

@Composable
private fun PlaceDetailsContent(
    state: PlaceDetailsState = PlaceDetailsState(),
    onAction: (PlaceDetailsAction) -> Unit = { }
) { /* Card(...) avec stringResource(R.string.xxx) et onAction(PlaceDetailsAction.ClickedCard) */ }

@Preview
@Composable
private fun PlaceDetailsPreview() = PreviewContent {
    PlaceDetailsContent(PlaceDetailsState(isLoading = false, name = "Citadel", type = "Space station"))
}
```

Les composables privés (`Content`, cartes) reçoivent seulement l'état et des callbacks : ils ne naviguent pas et ne connaissent pas le ViewModel.

## Navigation

- Une destination est un cas de `sealed class Destination` (`core/ui/.../Navigation.kt`), avec sa route et ses `navArgument`, comme `CharacterDetails`.
- Elle s'ajoute au `NavHost` de `MainActivity` par `composable(Destination.Xxx()) { backStackEntry -> XxxScreen(navController, backStackEntry.arguments?.getInt("xxxId") ?: -1) }`.
- Le ViewModel ne connaît pas le `NavController` : il envoie la destination par `sendEvent`, l'écran navigue.
- Un clic qui doit aussi déclencher une capacité native passe par `handleAction` du ViewModel, qui appelle le Manager, jamais par le composable (voir `capacites-natives.md`).
