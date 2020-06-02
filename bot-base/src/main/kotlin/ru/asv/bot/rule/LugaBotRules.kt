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

            answer {
                whenMatches(
                    word("погода"),
                    optional("бунинских"),
                    regexp("луга.*")
                )

                thenAnswer {
                    weatherAdapter.getData().flatMap {
                        val answer = """
                         В Лугах сейчас: Температура ${it.fact.temp}, ощущается как ${it.fact.feels_like}, скорость ветра ${it.fact.wind_speed}""".trimIndent()
                        Mono.just(answer)
                    }
                }
            }

            answer {
                whenMatches(
                    word("телефон"),
                    word("ук")
                )

                thenAnswer(
                    "Телефон УК +7 (800) 505-89-89"
                )
            }

        }
    }

}