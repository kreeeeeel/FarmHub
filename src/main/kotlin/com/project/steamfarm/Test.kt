package com.project.steamfarm

import com.project.steamfarm.model.ConfigModel
import com.project.steamfarm.model.LangModel
import com.project.steamfarm.repository.impl.LangRepository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.farm.steam.impl.DefaultAuthSteamDesktop
import com.project.steamfarm.service.steam.impl.DefaultGuardSteam
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser.INPUT
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent


@Suppress("unused")
class Test {

    private val steamPath: String = "\"H:\\Program Files (x86)\\Steam\\steam.exe\""
    private val command: String = "-w 380 -h 285 -sw -console -novid -low -nosound"
    private val gameId: Int = 570

    private val guard = DefaultGuardSteam()

    fun start() {
        UserRepository.findAll()[1].let {
            /*val command = listOf(
                steamPath,
                "-login",
                it.steam.accountName,
                it.steam.password,
                guard.getCode(it.steam.sharedSecret)
            )*/

            //val process = ProcessBuilder(command).start()
            /*val `in` = BufferedReader(InputStreamReader(process.inputStream))
            val er = BufferedReader(InputStreamReader(process.errorStream))

            var s: String
            while ((`in`.readLine().also { s = it }) != null) {
                println(s)
            }
            while ((er.readLine().also { s = it }) != null) {
                println(s)
            }*/


            // Добавьте задержку, чтобы убедиться, что Steam запустился и находится в фокусе
            Thread.sleep(20000)

            val robot = Robot()

            // Установите данные в буфер обмена и вставьте их
            fun pasteText(text: String) {
                val stringSelection = StringSelection(text)
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(stringSelection, null)

                robot.keyPress(KeyEvent.VK_CONTROL)
                robot.keyPress(KeyEvent.VK_V)
                robot.keyRelease(KeyEvent.VK_V)
                robot.keyRelease(KeyEvent.VK_CONTROL)
                println(text)
            }

            // Вводим имя пользователя
            pasteText(it.steam.accountName)
            robot.keyPress(KeyEvent.VK_TAB)
            robot.keyRelease(KeyEvent.VK_TAB)

            // Вводим пароль
            pasteText(it.steam.password)
            robot.keyPress(KeyEvent.VK_ENTER)
            robot.keyRelease(KeyEvent.VK_ENTER)

            //val status = process.waitFor()
            //println("Exited with status: $status")
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

    /*fun testScreen() {

        ProcessBuilder(listOf(
            steamPath,
            "-appLaunch",
            "570",
            "-silent",
            "-w",
            "480",
            "-h",
            "360",
            "-sw",
            "-console",
            "-novid",
            "-low",
            "-nosound",
            "-x",
            "0",
            "-y",
            "0"
        )).start()

        val path = "C:\\Users\\nemty\\Desktop\\Projects\\Steam Farm Desktop\\src\\main\\resources\\com\\project\\steamfarm\\pages\\auth"

        val screen = Screen()
        val window = Pattern("$path\\main.png")
        val match = screen.wait(window, 100.0)

        val login = match.wait("$path\\login.png", 100.0).wait("$path\\field.png", 100.0)
        val password = match.wait("$path\\password.png", 100.0).wait("$path\\field.png", 100.0)

        val userModel = UserRepository.findAll()[0]
        match.type(login, userModel.steam.accountName)
        match.type(password, userModel.steam.password)

        val signIn = match.wait("$path\\signIn.png", 100.0)
        signIn.click()

        val guardField = screen.wait("$path\\guard.png", 100.0)
        guardField.type(guard.getCode(userModel.steam.sharedSecret))

        /*val screen = Screen()
        val path = "C:\\Users\\nemty\\Desktop\\Projects\\Steam Farm Desktop\\src\\main\\resources\\com\\project\\steamfarm\\pages\\auth"
        val pattern = Pattern("$path\\wk.png")

        val region = Region(47, 75, 390, 192)

        val similarities = listOf(0.7)
        val resizeFactors = listOf(1F)

        var found = false

        for (similarity in similarities) {
            for (resizeFactor in resizeFactors) {
                try {
                    val adjustedPattern = pattern.similar(similarity).resize(resizeFactor)
                    val pudge = region.wait(adjustedPattern, 10.0)
                    pudge.click()
                    println("Image found and clicked with similarity: $similarity and resize: $resizeFactor")
                    found = true
                    break
                } catch (e: FindFailed) {
                    println("Image not found with similarity: $similarity and resize: $resizeFactor")
                }
            }
            if (found) break
        }

        if (!found) {
            println("Image not found with any of the given similarity and resize values.")
        }*/
    }*/

    fun api () {
        val authSteamDesktop = DefaultAuthSteamDesktop()
        val userModel = UserRepository.findAll()[3]
        authSteamDesktop.start(userModel.steam.accountName, 730)
        authSteamDesktop.signIn(userModel.steam.accountName, userModel.steam.password)
        authSteamDesktop.guard(userModel.steam.sharedSecret)
    }

    fun type(value: String): CharArray {
        val textBuffer: CharArray = value.toCharArray()
        val lParamBuffer = CharArray(textBuffer.size + 1)
        System.arraycopy(textBuffer, 0, lParamBuffer, 0, textBuffer.size)
        lParamBuffer[textBuffer.size] = '\u0000'
        return lParamBuffer
    }

    private fun sendInput(input: INPUT) {
        val inputs = arrayOf(input)
        User32.INSTANCE.SendInput(WinDef.DWORD(1), inputs, input.size())
    }
}

fun main() {

    val configModel: ConfigModel = ConfigModel().fromFile()
    langApplication = LangRepository.findById(configModel.langApp) ?: LangModel()

    Test().api()
    //val userModel = UserRepository.findAll()[0]
    //println("login ${userModel.steam.accountName} ${userModel.steam.password} ${DefaultGuardSteam().getCode(userModel.steam.sharedSecret)}")
    //Test().testScreen()
}