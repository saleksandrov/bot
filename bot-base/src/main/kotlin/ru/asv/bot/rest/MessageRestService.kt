package ru.asv.bot.rest

import com.google.gson.*
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.asv.bot.model.BotRequest
import ru.asv.bot.model.BotResponse
import java.lang.reflect.Type

@RestController
@RequestMapping("/api/v1/bot")
class MessageRestService {

    private val logger = LoggerFactory.getLogger(MessageRestService::class.java)
    private val rqLogger = LoggerFactory.getLogger("requests")

    @PostMapping("/sendMessage")
    fun processMessage(@RequestBody request: String) : Mono<ResponseEntity<Any>> {
        rqLogger.info("Received request ${request}")

        return try {
            val botRequest = parseRequest(request)
            rqLogger.info("Extracted request data: ${botRequest!!}")
            val botResponse = BotResponse("sendMessage", botRequest.chatId, botRequest.text)
            Mono.just(ResponseEntity.ok(botResponse as Any))
        } catch (ex: Exception) {
            logger.error("Error during input request handling", ex)
            Mono.just(ResponseEntity.badRequest().body("Error" as Any))
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
                .getAsJsonObject("body")
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