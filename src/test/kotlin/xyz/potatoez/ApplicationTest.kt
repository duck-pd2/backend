package xyz.potatoez

import xyz.potatoez.utils.parseICS

fun main (){
    val m = parseICS("https://moodle.ncku.edu.tw/calendar/export_execute.php?userid=204679&authtoken=0f105a7bc63d4b70e5a6bd30a26a68ae2b46f2f5&preset_what=all&preset_time=recentupcoming")
    println(m)
}
