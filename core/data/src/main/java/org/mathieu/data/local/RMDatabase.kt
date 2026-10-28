package org.mathieu.data.local

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.TypedRealmObject
import org.mathieu.data.local.objects.CharacterObject
import org.mathieu.data.local.objects.LocationObject
import kotlin.reflect.KClass

/**
 * Base Realm de l'application : la liste des classes stockées et la version du schéma sont déclarées ici, une seule fois.
 */
internal class RMDatabase : RealmDatabase(
    "rick and morty",
    setOf(
        CharacterObject::class,
        LocationObject::class
    ),
    // Version 2 : ajout de LocationObject. Realm efface alors l'ancienne base (deleteRealmIfMigrationNeeded) :
    // acceptable ici car tout ce qu'elle contient se recharge depuis l'API.
    2
)

/**
 * Ouvre la base Realm à la première requête seulement, puis la réutilise : ouvrir une base coûte cher,
 * et toutes les classes `XxxLocal` partagent ainsi la même instance.
 *
 * @param name nom du fichier de la base sur l'appareil.
 * @param schema classes Realm que la base peut contenir.
 * @param schemaVersion version du schéma, à augmenter à chaque ajout ou modification d'une classe.
 */
open class RealmDatabase(name: String, schema: Set<KClass<out TypedRealmObject>>, schemaVersion: Long) {
    private val configuration = RealmConfiguration.Builder(schema)
        .name(name)
        .schemaVersion(schemaVersion)
        //We'd rather prefer performing real schema migration in production environment, see : https://www.mongodb.com/developer/products/realm/realm-sdk-schema-migration-android/
        .deleteRealmIfMigrationNeeded()
        .build()

    private var realm: Realm? = null

    /**
     * @param block requête à exécuter sur la base ouverte.
     * @return le résultat de [block].
     */
    suspend fun <R> use(block: suspend Realm.() -> R): R {
        realm = realm ?: Realm.open(configuration)
        return try {
            block(realm!!)
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Toute modification passe par une transaction Realm, pour qu'une écriture interrompue ne laisse pas la base à moitié modifiée.
     *
     * @param block modifications à appliquer.
     * @return le résultat de [block].
     */
    suspend fun <R> write(block: MutableRealm.() -> R): R = use { this.write(block) }

}