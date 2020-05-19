package ru.asv.bot.rule

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import ru.asv.bot.adapter.WeatherAdapter
import ru.asv.bot.text.OptionalWord
import ru.asv.bot.text.RegexpWord
import ru.asv.bot.text.RequiredWord
import ru.asv.bot.text.Word

@Component
class LugaBotRules @Autowired constructor(private val weatherAdapter: WeatherAdapter) : RuleEngine() {

    init {
        botRule {

            defaultAnswers {
                listOf(
                    "В моей базе еще нет ответа на этот вопрос. Но она пополняется.",
                    "Я еще пока не знаю ответа на этот вопрос.",
                    "Попробуйте уточнить вопрос."
                )
            }

            answer {
                whenMatches {
                    listOf<Word>(
                        RequiredWord("погода"),
                        OptionalWord(RequiredWord("бунинских")),
                        RegexpWord("луга.*")
                    )
                }

                thenAnswer {
                    weatherAdapter.getWeather().flatMap {
                        val answer = """
                         В Лугах сейчас: Температура ${it.fact.temp}, ощущается как ${it.fact.feels_like}, скорость ветра ${it.fact.wind_speed}""".trimIndent()
                        Mono.just(answer)
                    }
                }
            }

            answer {
                whenMatches {
                    arrayListOf<Word>(
                        RequiredWord("телефон"),
                        RequiredWord("ук")
                    )
                }

                thenAnswer {
                    Mono.just("Телефон УК +7 (800) 505-89-89")
                }
            }

        }
    }

}