package ru.asv.bot.rest

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/adm/base", produces = [MediaType.APPLICATION_JSON_VALUE])
class AdminRestService {

    @GetMapping("version")
    fun getVersion(): Mono<String> {
        return Mono.just("1.0.0")
    }

}