package ru.asv.bot.client

import com.fasterxml.jackson.annotation.JsonProperty

data class BotResponse(
    val method: String,
    @JsonProperty("chat_id") val chatId: Int,
    val text: String
)

data class BotRequest(
    val chatId: Int,
    val text: String
)