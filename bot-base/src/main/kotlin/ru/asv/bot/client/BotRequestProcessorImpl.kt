package ru.asv.bot.client

import com.google.gson.*
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Type

@Component
class BotRequestProcessorImpl : BotRequestProcessor {

    override fun parseRequest(request: String): BotRequest? {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(BotRequest::class.java, jsonDeserializer())
        val customGson = gsonBuilder.create()
        return customGson.fromJson(request, BotRequest::class.java)
    }

    override fun isSystemCommand(command: String): Boolean {
        if (command.startsWith('/')) {
            return true
        }
        return false
    }

    override fun execCommand(command: String): Mono<String> {
        val sp = SystemCommandProcessor()
        return when (command) {
            "/start" -> sp.start()
            "/help" -> sp.help()
            "/whatsnew" -> sp.new()
            else -> Mono.just("Неизвестная команда")
        }
    }

    private fun jsonDeserializer(): JsonDeserializer<BotRequest> {
        return JsonDeserializer<BotRequest> { jsonElement: JsonElement, _: Type, _: JsonDeserializationContext ->
            val jsonObject: JsonObject = jsonElement.asJsonObject

            val messageObject = jsonObject
                .getAsJsonObject("message")
            val chatObject = messageObject
                .getAsJsonObject("chat")

            BotRequest(
                chatObject["id"].asInt,
                if (messageObject.has("text")) {
                    messageObject["text"].asString
                } else {
                    ""
                }
            )

        }
    }

}