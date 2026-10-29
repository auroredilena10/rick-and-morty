package org.mathieu.data.local

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.TypedRealmObject
import org.mathieu.data.local.objects.CharacterObject
import org.mathieu.data.local.objects.LocationObject
import kotlin.reflect.KClass

/**
 * La base Realm de l'app. C'est ici, et seulement ici, qu'on liste les tables et la version du schéma.
 */
internal class RMDatabase : RealmDatabase(
    "rick and morty",
    setOf(
        CharacterObject::class,
        LocationObject::class
    ),
    // Version 2 depuis l'ajout de LocationObject. Au changement de version, Realm vide la base
    // (deleteRealmIfMigrationNeeded) : ce n'est pas grave ici, tout se recharge depuis l'API.
    2
)

/**
 * Ouvre la base à la première requête, puis garde la même : ouvrir une base prend du temps,
 * et comme ça tous les `XxxLocal` travaillent sur la même instance.
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
     * Lance une lecture sur la base, en l'ouvrant si besoin.
     *
     * @param block requête à exécuter.
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
     * Écrit dans la base, toujours dans une transaction : si l'écriture s'arrête au milieu, rien n'est gardé
     * et la base n'est pas à moitié modifiée.
     *
     * @param block modifications à appliquer.
     * @return le résultat de [block].
     */
    suspend fun <R> write(block: MutableRealm.() -> R): R = use { this.write(block) }

}