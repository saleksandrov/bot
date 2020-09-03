package ru.asv.bot.client

import reactor.core.publisher.Mono

interface BotRequestProcessor {

    fun parseRequest(request: String): BotRequest?

    fun isSystemCommand(command: String): Boolean

    fun execCommand(command: String): Mono<String>

}