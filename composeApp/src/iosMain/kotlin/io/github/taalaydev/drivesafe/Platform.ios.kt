package io.github.taalaydev.drivesafe

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIDevice
import platform.Foundation.*

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

class IosFileStorage : FileStorage {
    private val fileManager = NSFileManager.defaultManager
    private val documentsPath: String
        get() = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).first() as String

    @OptIn(ExperimentalForeignApi::class)
    override fun writeText(fileName: String, text: String) {
        val filePath = "${documentsPath}/${fileName}"
        (text as NSString).writeToFile(
            filePath,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun readText(fileName: String): String? {
        val filePath = "${documentsPath}/${fileName}"
        return NSString.stringWithContentsOfFile(
            filePath,
            NSUTF8StringEncoding,
            null
        ) as? String
    }

    override fun exists(fileName: String): Boolean {
        val filePath = "${documentsPath}/${fileName}"
        return fileManager.fileExistsAtPath(filePath)
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun delete(fileName: String) {
        val filePath = "${documentsPath}/${fileName}"
        fileManager.removeItemAtPath(filePath, null)
    }
}

actual fun createFileStorage(): FileStorage = IosFileStorage()

actual fun getStorageFilePath(): String = NSSearchPathForDirectoriesInDomains(
    NSDocumentDirectory,
    NSUserDomainMask,
    true
).first() as String