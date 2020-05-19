package ru.asv.bot.rule

import org.springframework.stereotype.Component
import ru.asv.bot.text.OptionalWord
import ru.asv.bot.text.RegexpWord
import ru.asv.bot.text.RequiredWord
import ru.asv.bot.text.Word

@Component
class LugaBotRules : RuleEngine() {

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
                    "хорошая погода"
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
                    "Телефон УК +7 (800) 505-89-89"
                }
            }

        }
    }

}