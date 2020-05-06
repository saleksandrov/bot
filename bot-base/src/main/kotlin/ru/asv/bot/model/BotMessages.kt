package ru.asv.bot.model

data class BotResponse(
    val method: String,
    val chatId: Int,
    val text: String
)

data class BotRequest(
    val chatId: Int,
    val text: String
)