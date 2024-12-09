package io.github.taalaydev.drivesafe

import java.io.File
import kotlin.io.path.Path

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

class DesktopFileStorage : FileStorage {
    private val directory: File
        get() {
            val userHome = System.getProperty("user.home")
            val appDir = File(userHome, ".drivesafe")
            if (!appDir.exists()) {
                appDir.mkdirs()
            }
            return appDir
        }

    override fun writeText(fileName: String, text: String) {
        File(directory, fileName).writeText(text)
    }

    override fun readText(fileName: String): String? {
        val file = File(directory, fileName)
        return if (file.exists()) file.readText() else null
    }

    override fun exists(fileName: String): Boolean {
        return File(directory, fileName).exists()
    }

    override fun delete(fileName: String) {
        File(directory, fileName).delete()
    }
}

actual fun createFileStorage(): FileStorage = DesktopFileStorage()

actual fun getStorageFilePath(): String {
    val file =  File(System.getProperty("user.home"), ".drivesafe").absolutePath
    if (!File(file).exists()) {
        File(file).mkdirs()
    }
    return file
}