package xyz.potatoez.infrastructure.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.bson.BsonValue
import org.bson.types.ObjectId
import xyz.potatoez.domain.entity.Event
import xyz.potatoez.domain.ports.EventRepository

class EventRepositoryImpl(
    private val mongoDatabase: MongoDatabase
) : EventRepository {
    companion object {
        const val EVENT_COLLECTION = "event"
    }

    override suspend fun createEvent(event: Event): BsonValue? {
        try {
            val result = mongoDatabase.getCollection<Event>(EVENT_COLLECTION).insertOne(event)
            return result.insertedId
        } catch (e: MongoException) {
            System.err.println("Unable to insert due to an error: ${e.message}")
        }
        return null
    }

    override suspend fun readEvent(id: ObjectId): Event? =
        mongoDatabase.getCollection<Event>(EVENT_COLLECTION).find(Filters.eq("_id", id)).firstOrNull()

    override suspend fun updateEvent(id: ObjectId, event: Event): Long {
        try {
            val query = Filters.eq("_id", id)
            val updates = Updates.combine(
                Updates.set(Event::title.name, event.title),
                Updates.set(Event::start.name, event.start),
                Updates.set(Event::end.name, event.end),
                Updates.set(Event::description.name, event.description)
            )
            val options = UpdateOptions().upsert(true)
            val result = mongoDatabase.getCollection<Event>(EVENT_COLLECTION).updateOne(query, updates, options)

            return result.modifiedCount
        } catch (e: MongoException) {
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun deleteEvent(id: ObjectId): Long {
        try {
            val result = mongoDatabase.getCollection<Event>(EVENT_COLLECTION).deleteOne(Filters.eq("_id", id))
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: ${e.message}")
            return 0
        }
    }
}
