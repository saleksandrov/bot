package ru.asv.bot.rest.component

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

@Component
class LogComponent {

    private val counter = AtomicInteger(0)
    private val lastRequests = ConcurrentLinkedQueue<String>()

    fun getTotalRequests() : Int {
        return counter.get()
    }

    fun getLastRequests(): List<String> {
        return lastRequests.toList()
    }

    fun newRequest(request: String) {
        runBlocking {
            launch(Dispatchers.Default) {
                logRequest(request)
            }
        }
    }

    private suspend fun logRequest(request: String) {
        counter.incrementAndGet()
        if (lastRequests.size > 10) {
            lastRequests.poll()
        }
        lastRequests.offer(request)

    }

}
