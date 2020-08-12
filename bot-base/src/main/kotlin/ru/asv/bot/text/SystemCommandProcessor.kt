package ru.asv.bot.text

import reactor.core.publisher.Mono


class SystemCommandProcessor {

    companion object {
        val aboutAnswer = """
            Чат бот создан для жителей ЖК Бунинские Луга, в его базе есть ответы на частые вопросы.
            Примеры вопросов - 'Подскажи контакты УК', 'Подскажи контакты мастеров', 'Подскажи тарифы', 'Как прописаться в Лугах?', 'Кто управляющий?'.
            А еще он знает какая погода в ЖК, спросите к примеру 'Какая погода в Лугах?' или просто напишите 'погода'.
            
            База ответов постоянно пополняется.  
            Если заметили ошибку или хотите добавить ответ в базу или исправить неточность напишите на asv-app-dev@yandex.ru.
        """.trimIndent()}

    fun start() : Mono<String> {
        return Mono.just(aboutAnswer)
    }

    fun help() : Mono<String> {
        return Mono.just(aboutAnswer)
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
    return when (command) {
        "/start" -> sp.start()
        "/help" -> sp.help()
        else -> Mono.just("Неизвестная команда")
    }
}