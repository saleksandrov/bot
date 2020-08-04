package ru.asv.bot.text

import reactor.core.publisher.Mono

class SystemCommandProcessor {

    fun start() : Mono<String> {
        return Mono.just("""
            Чат бот создан для жителей ЖК Бунинские Луга и содержит ответы на частые вопросы.
            Например 'Подскажи контакты УК' или 'Как прописаться в Лугах?'.
            
            По всем вопросам просьба писать на asv-app-dev@yandex.ru.
        """.trimIndent())
    }

    fun help() : Mono<String> {
        return Mono.just("""
            Чат бот создан для жителей ЖК Бунинские Луга и содержит ответы на частые вопросы.
            Например 'Подскажи контакты УК' или 'Как прописаться в Лугах?'.
            
            По всем вопросам просьба писать на asv-app-dev@yandex.ru.           
        """.trimIndent())
    }

}

fun isSystemCommand(command: String): Boolean {
    if (command.startsWith('/')) {
        return true
    }
    return false
}

fun execCommand(command: String): Mono<String> {
    val sp = SystemCommandProcessor()
    if (command.equals("/start")) {
        return sp.start()
    } else if (command.equals("/help")) {
        return sp.help()
    } else {
        return Mono.just("Неизвестная команда")
    }
}