package xyz.potatoez

import xyz.potatoez.application.requests.EventRequest
import xyz.potatoez.application.requests.toDomain
import xyz.potatoez.domain.entity.Event
import xyz.potatoez.utils.ParserICS

fun main (){
    val url: String =  "https://moodle.ncku.edu.tw/calendar/export_execute.php?userid=204431&authtoken=eaa2da79919eefaf40536173ff47f443d2012ae2&preset_what=all&preset_time=custom"
    val mapList = ParserICS(url)
    val m = mapList[0]
    val e: Event = EventRequest(m).toDomain()
    println(e)
}
