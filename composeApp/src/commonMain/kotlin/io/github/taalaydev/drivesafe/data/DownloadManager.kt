package io.github.taalaydev.drivesafe.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.core.remaining
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.io.InternalIoApi
import kotlinx.io.readByteArray
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

sealed class DownloadResult<out T> {
    data class Success<T>(val data: T) : DownloadResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : DownloadResult<Nothing>()
    data class Loading(val progress: DownloadProgress) : DownloadResult<Nothing>()
}

@Serializable
data class DownloadProgress(
    val bytesDownloaded: Long,
    val contentLength: Long?,
    val percent: Float
)

object DownloadManager {
    const val DEFAULT_BUFFER_SIZE = 4096

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend inline fun <reified T> downloadFile(url: String): DownloadResult<T> {
        return try {
            val response = client.get(url)
            val data = response.body<T>()
            DownloadResult.Success(data)
        } catch (e: Exception) {
            DownloadResult.Error("Failed to download file", e)
        }
    }

    fun downloadWithProgress(url: String): Flow<DownloadResult<ByteArray>> = flow {
        try {
            val response = client.get(url)
            val channel = response.bodyAsChannel()
            val contentLength = response.contentLength()
            var totalBytes = 0L

            emit(DownloadResult.Loading(DownloadProgress(0, contentLength, 0f)))
            val data = buildPacket {
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                    writeFully(packet.readByteArray())
                    totalBytes += packet.remaining

                    val progress = DownloadProgress(
                        bytesDownloaded = totalBytes,
                        contentLength = contentLength,
                        percent = if (contentLength != null) {
                            (totalBytes.toFloat() / contentLength.toFloat()) * 100
                        } else 0f
                    )

                    emit(DownloadResult.Loading(progress))
                }
            }.readByteArray()

            emit(DownloadResult.Success(data))
        } catch (e: Exception) {
            emit(DownloadResult.Error("Failed to download file: ${e.message}", e))
        }
    }.catch { e ->
        emit(DownloadResult.Error("Failed to download file: ${e.message}", e))
    }

    fun dispose() {
        client.close()
    }
}