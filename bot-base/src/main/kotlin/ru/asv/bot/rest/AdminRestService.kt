package ru.asv.bot.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.asv.bot.rest.component.LogComponent

@RestController
@RequestMapping("/adm/base", produces = [MediaType.APPLICATION_JSON_VALUE])
class AdminRestService @Autowired constructor(
    private val logRequest: LogComponent
) {

    @GetMapping("version")
    fun getVersion(): Mono<String> {
        return Mono.just("1.1.0")
    }

    @GetMapping("logdata")
    fun getLogData(): Mono<String> {
        return Mono.just("""
            Total requests: ${logRequest.getTotalRequests()} 
            Last requests: ${logRequest.getLastRequests().map { "${it} \n" }}
            Unique requests: ${logRequest.uniqueRequests.size}
            Max count by user: ${logRequest.uniqueRequests.maxBy { e -> e.value }}
        """.trimIndent())
    }

}