package xyz.potatoez.infrastructure.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.bson.BsonValue
import org.bson.types.ObjectId
import xyz.potatoez.domain.entity.User
import xyz.potatoez.domain.ports.UserRepository

class UserRepositoryImpl (
    private val mongoDatabase: MongoDatabase
) : UserRepository {
    companion object {
        const val USER_COLLECTION = "user"
    }

    override suspend fun createUser(user: User): BsonValue? {
        try {
            val result = mongoDatabase.getCollection<User>(USER_COLLECTION).insertOne(user)
            return result.insertedId
        } catch (e: MongoException) {
            System.err.println("Unable to insert due to an error: ${e.message}")
        }
        return null
    }

    override suspend fun readUser(id: ObjectId): User? =
        mongoDatabase.getCollection<User>(USER_COLLECTION).withDocumentClass<User>()
            .find(Filters.eq("_id", id))
            .firstOrNull()

    override suspend fun updateUser(id: ObjectId, user: User): Long {
        try {
            val query = Filters.eq("_id", id)
            val updates = Updates.combine(
                Updates.set(User::name.name, user.name),
                Updates.set(User::refreshToken.name, user.refreshToken),
                Updates.set(User::user.name, user.user),
            )
            val options = UpdateOptions().upsert(true)
            val result =
                mongoDatabase.getCollection<User>(USER_COLLECTION)
                    .updateOne(query, updates, options)

            return result.modifiedCount
        } catch (e: MongoException) {
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun deleteUser(id: ObjectId): Long {
        try {
            val result = mongoDatabase.getCollection<User>(USER_COLLECTION)
                .deleteOne(Filters.eq("_id", id))
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: ${e.message}")
            return 0
        }
    }
}