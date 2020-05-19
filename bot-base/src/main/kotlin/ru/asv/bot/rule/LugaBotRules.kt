package ru.asv.bot.rule

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
                        RegexpWord("лугах")
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
                    "Телефон УК +7 (495) 111-22-33"
                }
            }

        }
    }

}