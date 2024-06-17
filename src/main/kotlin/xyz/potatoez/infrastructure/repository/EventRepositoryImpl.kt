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
        return try {
            val result = mongoDatabase.getCollection<Event>(EVENT_COLLECTION).insertOne(event)
            result.insertedId
        } catch (e: MongoException) {
            System.err.println("Unable to insert due to an error: ${e.message}")
            null
        }
    }

    override suspend fun readEvent(id: ObjectId): Event? {
        return try {
            mongoDatabase.getCollection<Event>(EVENT_COLLECTION).find(Filters.eq("_id", id)).firstOrNull()
        } catch (e: MongoException) {
            System.err.println("Unable to read due to an error: ${e.message}")
            null
        }
    }

    override suspend fun updateEvent(id: ObjectId, event: Event): Long {
        return try {
            val query = Filters.eq("_id", id)
            val updates = Updates.combine(
                Updates.set(Event::title.name, event.title),
                Updates.set(Event::start.name, event.start),
                Updates.set(Event::end.name, event.end),
                Updates.set(Event::description.name, event.description),
                Updates.set(Event::eventClass.name, event.eventClass),
                Updates.set(Event::color.name, event.color),
                Updates.set(Event::tags.name, event.tags),

            )
            val options = UpdateOptions().upsert(true)
            val result = mongoDatabase.getCollection<Event>(EVENT_COLLECTION).updateOne(query, updates, options)
            result.modifiedCount
        } catch (e: MongoException) {
            System.err.println("Unable to update due to an error: ${e.message}")
            0
        }
    }

    override suspend fun deleteEvent(id: ObjectId): Long {
        return try {
            val result = mongoDatabase.getCollection<Event>(EVENT_COLLECTION).deleteOne(Filters.eq("_id", id))
            result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: ${e.message}")
            0
        }
    }
}
