package xyz.potatoez.domain.ports

import org.bson.BsonValue
import org.bson.types.ObjectId
import xyz.potatoez.domain.entity.User

interface UserRepository {
    suspend fun createUser(user: User): BsonValue?
    suspend fun readUser(id: ObjectId): User?
    suspend fun deleteUser(id: ObjectId): Long
    suspend fun updateUser(id: ObjectId, user: User): Long
}