package xyz.potatoez.utils

import java.io.*
import java.net.URI
import java.net.URL

fun parseICS(urlString: String): MutableList<MutableMap<String, String>> {
    val mapList: MutableList<MutableMap<String, String>> = ArrayList()
    val filePath = "temp.ics" // Path to save the downloaded file

    // Download the .ics file
    val url: URL = URI(urlString).toURL()
    downloadFile(url, filePath)

    // Parse the downloaded .ics file
    parseICSFile(filePath, mapList)
    File(filePath).delete()

    return mapList
}

fun downloadFile(url: URL, filePath: String) {
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
fun parseICSFile(fileName: String, mapList: MutableList<MutableMap<String, String>>) {
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

                                while (br.readLine().also { line = it } != null && line!!.isNotEmpty()
                                    && !line!!.startsWith("CLASS:") && line != "END:VEVENT") {
                                    descriptionBuilder.append(line!!.replace("\\t".toRegex(), ""))
                                }
                                event("DESCRIPTION:$descriptionBuilder", contentMap)
                                continue
                            }

                            event(line!!, contentMap)
                        }
                        contentMap.put("color", "#F7F7F7")
                        contentMap.put("tags", "[]")
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
    val line = inp.replace("\\s+".toRegex(), " ")
    val parts = line.split(":", limit = 2)
    if (parts.size != 2) {
        return
    }
    when {
        parts[0].contains("SUMMARY") -> {
            contentMap["title"] = parts[1]
        }
        parts[0].contains("DTSTART") -> {
            val time = formatTime(parts[1])
            contentMap["start"] = time
        }
        parts[0].contains("DTEND") -> {
            val time = formatTime(parts[1])
            contentMap["end"] = time
        }
        parts[0].contains("CATEGORIES") -> {
            contentMap["eventClass"] = parts[1]
        }
        parts[0].contains("DESCRIPTION") -> {
            contentMap["description"] = parts[1]
        }
    }
}

fun formatTime(timeInp: String): String {
    return if (timeInp.contains("T")) {
        val year = timeInp.substring(0, 4)
        val month = timeInp.substring(4, 6)
        val day = timeInp.substring(6, 8)
        val hour = timeInp.substring(9, 11)
        val minute = timeInp.substring(11, 13)
        val second = timeInp.substring(13, 15)
        "${year}-${month}-${day}T${hour}:${minute}:${second}"
    } else {
        val date = timeInp.split("(?<=\\G.{2})".toRegex())
        "${date[0]}${date[2]}-${date[1]}-${date[2]}"
    }
}