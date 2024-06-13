package xyz.potatoez

import org.bson.types.ObjectId
import xyz.potatoez.application.requests.EventRequest
import xyz.potatoez.application.requests.toDomain
import xyz.potatoez.domain.entity.Event
import xyz.potatoez.utils.parseICS

fun main (){
    val url =  "https://moodle.ncku.edu.tw/calendar/export_execute.php?userid=204431&authtoken=eaa2da79919eefaf40536173ff47f443d2012ae2&preset_what=all&preset_time=custom"
    val mapList = parseICS(url)
    val m = mapList[0]
    val e: Event = EventRequest(m).toDomain(ObjectId("6668541558ab9d64de3b6163"))
    println(e)
}
