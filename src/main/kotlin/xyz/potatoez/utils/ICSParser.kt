package xyz.potatoez.utils

import java.io.*
import java.net.URL
import java.net.URI

fun ParserICS(urlString: String): MutableList<MutableMap<String, String>> {
    val mapList: MutableList<MutableMap<String, String>> = ArrayList()
    val filePath = "icalexport.ics" // Path to save the downloaded file

    // Download the .ics file
    val url: URL = URI(urlString).toURL()
    downloadFile(url, filePath)

    // Parse the downloaded .ics file
    parseICSFile(filePath, mapList)

    return mapList
}

fun downloadFile(url: URL, filePath: String?) {
    try {
        BufferedInputStream(url.openStream()).use { `in` ->
            FileOutputStream(filePath).use { fileOutputStream ->
                val dataBuffer = ByteArray(1024)
                var bytesRead: Int
                while (`in`.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead)
                }
                println("File downloaded successfully")
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

// Function to parse the downloaded .ics file
fun parseICSFile(fileName: String?, mapList: MutableList<MutableMap<String, String>>) {
    try {
        FileReader(fileName).use { fr ->
            BufferedReader(fr).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    line = line!!.replace("\\s+".toRegex(), " ").trim()
                    if (line!!.contains("BEGIN:VEVENT")) {
                        val contentMap = HashMap<String, String>()

                        while (br.readLine().also { line = it } != null && line != "END:VEVENT") {
                            line = line!!.replace("\\s+".toRegex(), " ").trim()
                            if (line!!.contains("DESCRIPTION")) {
                                val descriptionBuilder = StringBuilder()
                                val parts = line!!.split(":", limit = 2)
                                descriptionBuilder.append(parts[1])

                                while (br.readLine().also { line = it } != null && line!!.trim().isNotEmpty() && !line!!.startsWith("CLASS:") && line != "END:VEVENT") {
                                    descriptionBuilder.append(" ").append(line!!.trim())
                                }

                                event("DESCRIPTION:$descriptionBuilder", contentMap)
                                continue
                            }

                            event(line!!, contentMap)
                        }
                        mapList.add(contentMap)
                    }
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun event(inp: String, contentMap: MutableMap<String, String>) {
    var line = inp.replace("\\s+".toRegex(), " ").trim()
    val parts = line.split(":", limit = 2)
    if (parts.size != 2) {
        return
    }
    when {
        parts[0].contains("SUMMARY") -> {
            contentMap["title"] = "'${parts[1]}'"
        }
        parts[0].contains("DTSTART") -> {
            val time = if (parts[1].contains("T")) {
                val year = parts[1].substring(0, 4)
                val month = parts[1].substring(4, 6)
                val day = parts[1].substring(6, 8)
                val hour = parts[1].substring(9, 11)
                val minute = parts[1].substring(11, 13)
                val second = parts[1].substring(13, 15)
                "${year}-${month}-${day}T${hour}:${minute}:${second}"
            } else {
                val date = parts[1].split("(?<=\\G.{2})".toRegex())
                "${date[0]}${date[2]}-${date[1]}-${date[2]}"
            }
            contentMap["start"] = "'$time'"
        }
        parts[0].contains("DTEND") -> {
            val time = if (parts[1].contains("T")) {
                val year = parts[1].substring(0, 4)
                val month = parts[1].substring(4, 6)
                val day = parts[1].substring(6, 8)
                val hour = parts[1].substring(9, 11)
                val minute = parts[1].substring(11, 13)
                val second = parts[1].substring(13, 15)
                "${year}-${month}-${day}T${hour}:${minute}:${second}"
            } else {
                val date = parts[1].split("(?<=\\G.{2})".toRegex())
                "${date[0]}${date[2]}-${date[1]}-${date[2]}"
            }
            contentMap["end"] = "'$time'"
        }
        parts[0].contains("CATEGORIES") -> {
            contentMap["EventClass"] = "'${parts[1]}'"
        }
        parts[0].contains("DESCRIPTION") -> {
            contentMap["description"] = "'${parts[1]}'"
        }
    }
}
