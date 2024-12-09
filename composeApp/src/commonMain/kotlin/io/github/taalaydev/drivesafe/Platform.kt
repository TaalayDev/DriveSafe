package io.github.taalaydev.drivesafe


interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

interface FileStorage {
    fun writeText(fileName: String, text: String)
    fun readText(fileName: String): String?
    fun exists(fileName: String): Boolean
    fun delete(fileName: String)
}

expect fun createFileStorage(): FileStorage

expect fun getStorageFilePath(): String