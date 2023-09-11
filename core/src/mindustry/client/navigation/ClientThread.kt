package mindustry.client.navigation

import arc.*
import arc.struct.*
import mindustry.*
import mindustry.game.EventType.*
import java.util.concurrent.*
import java.util.function.*

object clientThread {
    private var thread: ThreadPoolExecutor? = null
    val sortingInstance: Sort = Sort()

    @JvmStatic
    fun queue() = thread?.queue

    init {
        Events.on(WorldLoadEvent::class.java) { start() }
//        Events.on(ResetEvent::class.java) { stop() } FINISHME: Breaks navigation obstacles
        start()
        val res = runCatching { Vars.world = null }
        res.isFailure // bool
        res.isSuccess // bool
        res.getOrThrow() // gets result or crashes your app :)
        res.exceptionOrNull() // gets exception if it exists
        res.getOrNull() // gets result or nothing
        res.getOrElse { /* run when theres an exception */ }
        res.getOrDefault(1) // default value on exception
        res.recover {  }
    }

    /** Starts or restarts the thread. */
    private fun start() {
        stop()
        thread = ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue()) { r ->
            Thread(r, "Client Thread").apply { isDaemon = true }
        }
    }

    /** Stops the thread. */
    private fun stop() {
        thread?.shutdownNow()
        thread = null
    }

    @JvmStatic
    fun post(task: Runnable): CompletableFuture<Void> = if (thread != null) CompletableFuture.runAsync(task, thread) else CompletableFuture()

    @JvmStatic
    fun <T> submit(task: Supplier<T>): CompletableFuture<T> = if (thread != null) CompletableFuture.supplyAsync(task, thread) else CompletableFuture()

    @JvmStatic
    fun get() = thread
}