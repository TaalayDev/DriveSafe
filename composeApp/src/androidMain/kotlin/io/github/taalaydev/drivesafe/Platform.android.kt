package io.github.taalaydev.drivesafe

import android.os.Build
import android.content.Context
import java.io.File

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

class AndroidFileStorage(private val context: Context) : FileStorage {
    private val directory: File
        get() = context.filesDir

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

actual fun createFileStorage(): FileStorage = AndroidFileStorage(AndroidApp.instance)

actual fun getStorageFilePath(): String = AndroidApp.instance.filesDir.absolutePath