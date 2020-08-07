package ru.asv.bot.rest

import com.google.gson.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.asv.bot.model.BotRequest
import ru.asv.bot.model.BotResponse
import ru.asv.bot.rest.component.LogComponent
import ru.asv.bot.rule.RuleEngine
import ru.asv.bot.text.SentenceProcessor
import ru.asv.bot.text.WordProcessor
import ru.asv.bot.text.execCommand
import ru.asv.bot.text.isSystemCommand
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
@RestController
@RequestMapping("/api/v1/bot", produces = [MediaType.APPLICATION_JSON_VALUE])
class MessageRestService @Autowired constructor(
    private val sp: SentenceProcessor,
    private val botRules: RuleEngine,
    private val wp: WordProcessor,
    private val logRequest: LogComponent
    ) {

    private val log = LoggerFactory.getLogger(MessageRestService::class.java)
    private val rqLog = LoggerFactory.getLogger("requests")

    @PostMapping("/sendMessage")
    fun processMessage(@RequestBody request: String) : Mono<ResponseEntity<Any>> {
        rqLog.info("Received request ${request}")

        var botRequest: BotRequest? = null
        try {
            logRequest.newRequest(request)
            botRequest = parseRequest(request)
            rqLog.info("Extracted request data: ${botRequest!!}")

            return if (isSystemCommand(botRequest.text)) {
                execCommand(botRequest.text).flatMap {
                    val botResponse = BotResponse("sendMessage", botRequest.chatId, it)
                    Mono.just(ResponseEntity.ok(botResponse) as ResponseEntity<Any>)
                }
            } else {
                wp.determineAnswer(sp.splitToWords(botRequest.text), botRules)
                    .flatMap {
                        val botResponse = BotResponse("sendMessage", botRequest.chatId, it)
                        Mono.just(ResponseEntity.ok(botResponse) as ResponseEntity<Any>)
                    }
            }
        } catch (ex: Exception) {
            log.error("Error during input request handling", ex)
            if (botRequest != null) {
                val botResponse = BotResponse("sendMessage", botRequest.chatId, "Не смог прочитать вопрос")
                return Mono.just(ResponseEntity.ok(botResponse) as ResponseEntity<Any>)
            } else {
                return Mono.just(ResponseEntity.badRequest().body("Error") as ResponseEntity<Any>)
            }
        }

    }

    private fun parseRequest(request: String): BotRequest? {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(BotRequest::class.java, jsonDeserializer())
        val customGson = gsonBuilder.create()
        val botRequest = customGson.fromJson(request, BotRequest::class.java)
        return botRequest
    }

    private fun jsonDeserializer(): JsonDeserializer<BotRequest> {
        return JsonDeserializer<BotRequest> { jsonElement: JsonElement, _: Type, _: JsonDeserializationContext ->
            val jsonObject: JsonObject = jsonElement.getAsJsonObject()

            val messageObject = jsonObject
                .getAsJsonObject("message")
            val chatObject = messageObject
                .getAsJsonObject("chat")

            BotRequest(
                chatObject["id"].asInt,
                messageObject["text"].asString
            )

        }
    }

}