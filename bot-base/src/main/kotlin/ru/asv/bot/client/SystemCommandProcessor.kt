package ru.asv.bot.client

import reactor.core.publisher.Mono


class SystemCommandProcessor {

    companion object {
        val aboutAnswer = """
            Чат бот создан для жителей ЖК Бунинские Луга, в его базе есть ответы на частые вопросы.
            Примеры вопросов - 'Подскажи контакты УК', 'Подскажи контакты мастеров', 'Подскажи тарифы', 'Как прописаться?', 
            'Как получить квитанцию на оплату капремонта кладовой?'.
            А еще он знает какая погода в ЖК, спросите к примеру 'Какая погода в Лугах?'.
            
            Чтобы узнать что нового появилось в чатботе введите команду /whatsnew
            Чтобы повторить данное сообщение введите команду /start
            
            База ответов постоянно пополняется.  
            Если заметили ошибку или хотите добавить ответ в базу или исправить неточность напишите на asv-app-dev@yandex.ru.
        """.trimIndent()}

    fun start() : Mono<String> {
        return Mono.just(aboutAnswer)
    }

    fun help() : Mono<String> {
        return Mono.just(aboutAnswer)
    }

    fun new() : Mono<String> {
        return Mono.just("""
            Текущая Версия 1.0 
                Это самая первая версия чатбота 
        """.trimIndent() )
    }

}

