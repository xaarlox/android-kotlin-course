package com.xaarlox.task12_microgrid_error_handling.data.logging

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/** A single log entry: what went wrong, when, and on which attempt. Contains no personal data */
data class ErrorLogEntry(
    val time: Long,
    val operation: String,
    val code: String,
    val httpStatus: Int?,
    val message: String,
    val attempt: Int,
    val willRetry: Boolean
)

/**
 * Error logging to a file (JSON Lines format: one entry per line) and Logcat.
 *  - Mutex: writes from different coroutines do not interleave;
 *  - rotation: files exceeding maxFileBytes become errors.log.1, preventing unlimited growth;
 *  - a log writing failure never crashes the application
 */
class ErrorLogger(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val maxFileBytes: Long = 256 * 1024L
) {
    private val gson = Gson()
    private val mutex = Mutex()
    private val dir = File(context.filesDir, "logs")
    private val current = File(dir, "errors.log")
    private val previous = File(dir, "errors.log.1")

    /** Change counter: subscribed to by the UI to re-read the log */
    private val _version = MutableStateFlow(0)
    val version: StateFlow<Int> = _version.asStateFlow()

    suspend fun log(entry: ErrorLogEntry) {
        withContext(ioDispatcher) {
            mutex.withLock {
                try {
                    dir.mkdirs()
                    if (current.length() > maxFileBytes) {
                        previous.delete()
                        current.renameTo(previous)
                    }
                    current.appendText(gson.toJson(entry) + "\n")
                } catch (e: IOException) {
                    Log.e(TAG, "Не вдалося записати журнал помилок", e)
                }
            }
        }
        Log.w(TAG, "${entry.operation} → ${entry.code} (спроба ${entry.attempt}): ${entry.message}")
        _version.update { it + 1 }
    }

    /** From newest to oldest */
    suspend fun readAll(): List<ErrorLogEntry> = withContext(ioDispatcher) {
        mutex.withLock {
            listOf(previous, current)
                .filter { it.exists() }
                .flatMap { file -> file.readLines().mapNotNull(::parse) }
                .reversed()
        }
    }

    suspend fun clear() {
        withContext(ioDispatcher) {
            mutex.withLock {
                previous.delete()
                current.delete()
            }
        }
        _version.update { it + 1 }
    }

    private fun parse(line: String): ErrorLogEntry? = try {
        gson.fromJson(line, ErrorLogEntry::class.java)
    } catch (e: JsonParseException) {
        null
    }

    private companion object {
        const val TAG = "ErrorLogger"
    }
}