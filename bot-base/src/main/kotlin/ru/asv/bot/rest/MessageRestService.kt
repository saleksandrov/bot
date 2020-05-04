package ru.asv.bot.rest

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/bot")
class MessageRestService {

    private val logger = LoggerFactory.getLogger(MessageRestService::class.java)

    @PostMapping("/sendMessage")
    fun processMessage(@RequestBody request: String) : Mono<ResponseEntity<String>> {
        logger.info("Received request ${request}")

        return Mono.just(ResponseEntity.ok("OK"))
    }

}