package androidx.room.util

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import kotlinx.coroutines.runBlocking

/**
 * Room's JVM runtime only ships the suspending entry point; Room's generated code for plain
 * (non-suspend) DAO methods calls this blocking one on Android. Same contract as Android:
 * call from a background thread.
 */
fun <R> performBlocking(
    db: RoomDatabase,
    isReadOnly: Boolean,
    inTransaction: Boolean,
    block: (SQLiteConnection) -> R,
): R = runBlocking { performSuspending(db, isReadOnly, inTransaction, block) }
