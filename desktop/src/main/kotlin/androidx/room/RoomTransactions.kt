package androidx.room

/**
 * Android Room's withTransaction. On the JVM runtime transactions go through the writer
 * connection; DAO calls made inside [block] join it automatically through the coroutine context.
 */
suspend fun <R> RoomDatabase.withTransaction(block: suspend () -> R): R =
    useWriterConnection { transactor -> transactor.immediateTransaction { block() } }
