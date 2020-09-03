package ru.asv.bot.rest

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.asv.bot.client.BotRequest
import ru.asv.bot.client.BotRequestProcessor
import ru.asv.bot.client.BotResponse
import ru.asv.bot.rest.component.LogComponent
import ru.asv.bot.rule.RuleEngine
import ru.asv.bot.text.SentenceProcessor
import ru.asv.bot.text.WordProcessor

@Suppress("UNCHECKED_CAST")
@RestController
@RequestMapping("/api/v1/bot", produces = [MediaType.APPLICATION_JSON_VALUE])
class MessageRestService @Autowired constructor(
    private val sp: SentenceProcessor,
    private val botRules: RuleEngine,
    private val wp: WordProcessor,
    private val requestProcessor: BotRequestProcessor,
    private val logRequest: LogComponent
    ) {

    private val log = LoggerFactory.getLogger(MessageRestService::class.java)
    private val rqLog = LoggerFactory.getLogger("requests")

    @PostMapping("/sendMessage")
    fun processMessage(@RequestBody request: String) : Mono<ResponseEntity<Any>> {
        rqLog.info("Received request $request")

        var botRequest: BotRequest? = null
        try {
            botRequest = requestProcessor.parseRequest(request)
            logRequest.newRequest(botRequest)
            rqLog.info("Extracted request data: ${botRequest!!}")

            return if (requestProcessor.isSystemCommand(botRequest.text)) {
                requestProcessor.execCommand(botRequest.text).flatMap {
                    createSuccessResponse(botRequest.chatId, it)
                }
            } else {
                wp.determineAnswer(sp.splitToWords(botRequest.text), botRules)
                    .flatMap {
                        createSuccessResponse(botRequest.chatId, it)
                    }
            }
        } catch (ex: Exception) {
            log.error("Error during input request handling", ex)
            return if (botRequest != null) {
                createSuccessResponse(botRequest.chatId, "Не смог прочитать вопрос")
            } else {
                createErrorResponse("Error")
            }
        }

    }

    private fun createSuccessResponse(chatId: Int, message: String) : Mono<ResponseEntity<Any>> {
        val botResponse = BotResponse("sendMessage", chatId, message)
        return Mono.just(ResponseEntity.ok(botResponse) as ResponseEntity<Any>)
    }

    private fun createErrorResponse(message: String) =
        Mono.just(ResponseEntity.badRequest().body(message) as ResponseEntity<Any>)

}