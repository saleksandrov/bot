package ru.asv.bot.rule

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import ru.asv.bot.adapter.WeatherAdapter

@Component
class LugaBotRules @Autowired constructor(private val weatherAdapter: WeatherAdapter) : RuleEngine() {

    init {
        botRule {

            defaultAnswers(
                "В моей базе еще нет ответа на этот вопрос. Но она пополняется.",
                "Я еще пока не знаю ответа на этот вопрос.",
                "Попробуйте уточнить вопрос."
            )


            answerWhenMatches(
                word("погода"),
                optional("бунинских"),
                regexp("луга.*")
            ) {
                weatherAdapter.getData().flatMap {
                    val answer = """
                         В Лугах сейчас: Температура ${it.fact.temp}, ощущается как ${it.fact.feels_like}, скорость ветра ${it.fact.wind_speed}""".trimIndent()
                    Mono.just(answer)
                }
            }

            answerWhenMatches(
                word("телефон"),
                word("ук"),
                answer = "Телефон УК +7 (800) 505-89-89"
            )

            answerWhenMatches(
                word("погода"),
                answer = "Уточните пожалуйста вопрос. Например 'Какая погода в Лугах?'"
            )

            answerWhenMatches(
                word("температура"),
                answer = "Уточните пожалуйста вопрос. Например 'Какая погода в Лугах?'"
            )

            answerWhenMatches(
                word("ук"),
                answer = "Уточните пожалуйста вопрос. Например 'Какай адрес УК?'"
            )

            answerWhenMatches(
                word("адрес"),
                word("ук"),
                answer = "Адрес УК ул. Александры Монаховой д. 94 к. 5"
            )

            answerWhenMatches(
                word("время"),
                regexp("работ.*"),
                optionalRegexp("офис.*"),
                word("ук"),
                answer = "Ежедневно 9:00-21:00"
            )

            answerWhenMatches(
                regexp("контакт.*"),
                word("ук"),
                answer = "Адрес УК ул. Александры Монаховой д. 94 к. 5, Офис работает ежедневно 9:00-21:00"
            )

            answerWhenMatches(
                regexp("контакт.*"),
                regexp("служб.*"),
                word("эксплуатации"),
                regexp("дом"),
                answer = "Адрес УК ул. Александры Монаховой д. 94 к. 5, Офис работает ежедневно 9:00-21:00"
            )


        }

    }
}