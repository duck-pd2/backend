package xyz.potatoez

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import xyz.potatoez.plugins.*
import xyz.potatoez.utils.ParserICS

fun main (){
    //val MapList : MutableList<MutableMap<String,String>> = ArrayList()
    val url :String =  "https://moodle.ncku.edu.tw/calendar/export_execute.php?userid=204431&authtoken=eaa2da79919eefaf40536173ff47f443d2012ae2&preset_what=all&preset_time=custom"
    val MapList = ParserICS(url)
    println(MapList)
}
