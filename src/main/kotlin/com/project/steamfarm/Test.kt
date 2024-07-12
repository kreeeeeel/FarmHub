package com.project.steamfarm

import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.steam.impl.DefaultGuardSteam
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Suppress("unused")
class Test {

    private val steamPath: String = "\"H:\\Program Files (x86)\\Steam\\steam.exe\""
    private val command: String = "-w 380 -h 285 -sw -console -novid -low -nosound"
    private val gameId: Int = 570

    private val guard = DefaultGuardSteam()

    fun start() {

        UserRepository.findAll()[1].let {

            val text = "login ${it.steam.accountName} ${it.steam.password} ${guard.getCode(it.steam.sharedSecret)}"
            val stringSelection = StringSelection(text)
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(stringSelection, null)

            //val command = listOf(steamPath, "-applaunch", "570")
            /*val command = listOf(
                steamPath,
                "-nofriendsui",
                "-vgui",
                "-noreactlogin",
                "-noverifyfiles",
                "-nobootstrapupdate",
                "-skipinitialbootstrap",
                "-norepairfiles",
                "-overridepackageurl",
                "-disable-winh264",
                "-language",
                "english"
            )

            val process = ProcessBuilder(command).start()
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            val er = BufferedReader(InputStreamReader(process.errorStream))

            var s: String
            while ((`in`.readLine().also { s = it }) != null) {
                println(s)
            }
            while ((er.readLine().also { s = it }) != null) {
                println(s)
            }

            val status = process.waitFor()
            println("Exited with status: $status")*/

        }

    }

    /*fun parse() {

        val gson = GsonBuilder().setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()


        val chrome = ChromeDriver()
        chrome.get("https://dota2.fandom.com/ru/wiki/Мини-карта")

        chrome.findElement(By.xpath("//table[@class='wikitable']"))
            .findElements(By.tagName("img"))
            .also {
                val jsExecutor = chrome as JavascriptExecutor
                val elementPosition = it[0].location.y
                jsExecutor.executeScript("window.scrollTo(0, $elementPosition);")

                Thread.sleep(2000)
            }
            .forEach {
                val name = it.getAttribute("alt").replace(" minimap icon", "")
                val lowerName = name.trim().replace(" ", "_").replace("-", "_").lowercase()
                val link = it.getAttribute("src")

                val heroModel = HeroModel(
                    name,
                    "/config/Dota 2/heroes/images/$lowerName.png",
                    link
                )

                val file = File(System.getProperty("user.dir") + "/config/Dota 2/heroes/" + lowerName + ".json")
                file.parentFile.mkdirs()
                FileWriter(file).use { writer ->  writer.write(gson.toJson(heroModel))}
                getPhotoFromLink(link, lowerName)
            }



    }

    private fun getPhotoFromLink(link: String, name: String) {

        val bytes = getBytesFromUrl(link) ?: return
        val file = File(System.getProperty("user.dir") + "/config/Dota 2/heroes/images/" + name + ".png").apply {
            parentFile.mkdirs()
            createNewFile()
        }
        FileOutputStream(file).use { outputStream -> outputStream.write(bytes) }
    }

    private fun getBytesFromUrl(url: String): ByteArray? {
        try {
            val outputStream = ByteArrayOutputStream()
            URL(url).openStream().use { inputStream ->
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
            return outputStream.toByteArray()
        } catch (e: IOException) {
            return null
        }
    }*/
}

fun main() {
    //Test().parse()//.start()
}