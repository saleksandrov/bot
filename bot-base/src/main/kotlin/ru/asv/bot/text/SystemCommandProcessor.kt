package ru.asv.bot.text

import reactor.core.publisher.Mono

class SystemCommandProcessor {

    fun start() : Mono<String> {
        return Mono.just("""
            Я создан для жителей ЖК Бунинские Луга, в моей базе есть ответы на частые вопросы.
            Вы можете спросить меня 'Подскажи контакты УК' или 'Как прописаться в Лугах?'.
            А еще я знаю какая погода в ЖК, спросите меня к примеру 'Какая погода в Лугах?' или просто напишите 'погода'.
            
            База ответов постоянно пополняется.  
            Если заметили ошибку или хотите добавить ответ в базу напишите на asv-app-dev@yandex.ru.
        """.trimIndent())
    }

    fun help() : Mono<String> {
        return Mono.just("""
            Я создан для жителей ЖК Бунинские Луга, в моей базе есть ответы на частые вопросы.
            Вы можете спросить меня 'Подскажи контакты УК' или 'Как прописаться в Лугах?'.
            А еще я знаю какая погода в ЖК, спросите меня к примеру 'Какая погода в Лугах?' или просто напишите 'погода'.
            
            База ответов постоянно пополняется.  
            Если заметили ошибку или хотите добавить ответ в базу напишите на asv-app-dev@yandex.ru.           
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