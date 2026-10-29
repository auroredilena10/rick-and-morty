# Exemples de code par couche (stack du projet : Ktor, Realm, Koin)

Exemples génériques (« tâches ») : adapter les noms et reprendre la structure de `CharacterRepositoryImpl`, `CharacterLocal`, `CharacterApi` et `CharacterObject`. Commentaires neufs en français (l'existant reste en anglais). Ce sont des modèles de forme.

## Sommaire
- Domain
- Data : réponse, API, objet Realm, accès local, mappers
- Repository, local d'abord
- Koin

## Domain (aucun import `android.*`, Ktor, Realm, Compose)

Modèles dans `core/domain/.../models/<sujet>/`, contrats dans `core/domain/.../repositories/`.

```kotlin
/**
 * Une tâche que l'utilisateur doit faire. Elle ne sait ni où elle est rangée ni comment elle est affichée.
 *
 * @property id identifiant unique, le même que celui du service distant.
 * @property isDone `true` une fois que l'utilisateur l'a terminée.
 */
data class Task(val id: Int, val title: String, val isDone: Boolean)

/** Contrat pour obtenir des tâches : c'est la couche Data qui décide d'où elles viennent. */
interface TaskRepository {
    /**
     * @param id identifiant de la tâche.
     * @return la tâche, lue d'abord en base, demandée à l'API seulement si elle n'a jamais été enregistrée.
     * @throws Exception si la tâche n'existe ni en base ni sur l'API.
     */
    suspend fun getTask(id: Int): Task
}
```

## Data : trois classes distinctes pour une même donnée

```kotlin
// core:data remote/responses : la forme du JSON envoyé par le serveur (DTO)
@Serializable
internal data class TaskResponse(val id: Int, val title: String, val done: Boolean)

// core:data remote : le Service, qui fait l'appel HTTP
internal class TaskApi(private val client: HttpClient) {
    suspend fun getTask(id: Int): TaskResponse? = client
        .get("task/$id")
        .accept(HttpStatusCode.OK)
        .body()
}

// core:data local/objects : la forme d'un enregistrement Realm (Entity)
internal class TaskObject : RealmObject {
    @PrimaryKey
    var id: Int = -1
    var title: String = ""
    var done: Boolean = false
}

// core:data local : les requêtes sur la base (rôle du DAO)
internal class TaskLocal(private val database: RealmDatabase) {
    suspend fun getTask(id: Int): TaskObject? = database.use {
        query<TaskObject>("id == $id").first().find()
    }

    suspend fun insert(task: TaskObject) {
        database.write { copyToRealm(task, UpdatePolicy.ALL) }
    }
}
```

Ajouter `TaskObject::class` au `setOf(...)` de `RMDatabase` et augmenter son `schemaVersion` (la configuration efface la base au changement de schéma : `deleteRealmIfMigrationNeeded`).

Mappers : fonctions d'extension, à côté de l'objet Realm (comme `CharacterResponse.toRealmObject()` et `CharacterObject.toModel()` dans `CharacterObject.kt`).

```kotlin
internal fun TaskResponse.toRealmObject() = TaskObject().also { obj ->
    obj.id = this.id
    obj.title = this.title
    obj.done = this.done
}

internal fun TaskObject.toModel() = Task(id = this.id, title = this.title, isDone = this.done)
```

## Repository, local d'abord

Même forme que `CharacterRepositoryImpl.getCharacter`.

```kotlin
internal class TaskRepositoryImpl(
    private val taskApi: TaskApi,
    private val taskLocal: TaskLocal
) : TaskRepository {

    /** D'abord la base : l'API ne sert que si la tâche n'a jamais été enregistrée. */
    override suspend fun getTask(id: Int): Task =
        taskLocal.getTask(id)?.toModel()
            ?: taskApi.getTask(id)?.let { response ->
                val obj = response.toRealmObject()
                taskLocal.insert(obj)
                obj.toModel()
            }
            ?: throw Exception("Task not found.")
}
```

## Koin

Tout se déclare dans `dataModule` (`core/data/.../di/DataModule.kt`), à la suite de l'existant :

```kotlin
single { TaskLocal(get()) }
single { TaskApi(get()) }
single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
```

Le ViewModel le reçoit par `private val taskRepository: TaskRepository by inject()`. `startKoin` (`CleanRmApiApplication`) n'a pas à changer tant qu'aucun nouveau module Koin n'est créé.
