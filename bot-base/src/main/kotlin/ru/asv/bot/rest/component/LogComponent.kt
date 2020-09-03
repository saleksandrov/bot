package ru.asv.bot.rest.component

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import ru.asv.bot.client.BotRequest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

@Component
class LogComponent {

    private val counter = AtomicInteger(0)
    private val lastRequests = ConcurrentLinkedQueue<String>()

    val uniqueRequests = ConcurrentHashMap<Int, Int>()

    fun getTotalRequests() : Int {
        return counter.get()
    }

    fun getLastRequests(): List<String> {
        return lastRequests.toList()
    }

    fun newRequest(request: BotRequest?) {
        GlobalScope.launch(Dispatchers.Default) {
            logRequest(request)
        }
    }

    private suspend fun logRequest(request: BotRequest?) {
        counter.incrementAndGet()
        if (lastRequests.size > 10) {
            lastRequests.poll()
        }
        lastRequests.offer("Thread=${Thread.currentThread().name} JSON=${request}")
        request?.let {
            uniqueRequests.compute(request.chatId) {
                _, v -> v?.inc() ?: 0
            }
        }
    }

}
