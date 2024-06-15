package xyz.potatoez.domain.ports

import org.bson.BsonValue
import org.bson.types.ObjectId
import xyz.potatoez.domain.entity.Event

interface EventRepository {
    suspend fun createEvent(event: Event): BsonValue?
    suspend fun readEvent(id: ObjectId): Event?
    suspend fun updateEvent(id: ObjectId, event: Event): Long
    suspend fun deleteEvent(id: ObjectId): Long
}
