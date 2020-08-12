package ru.asv.bot.rule

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import ru.asv.bot.adapter.WeatherAdapter
import ru.asv.bot.text.SystemCommandProcessor

@Component
class LugaBotRules @Autowired constructor(private val weatherAdapter: WeatherAdapter) : RuleEngine() {

    init {
        botRule {

            defaultAnswers(
                "В моей базе еще нет ответа на этот вопрос. Но она пополняется.",
                "Я еще пока не знаю ответа на этот вопрос.",
                "Попробуйте уточнить вопрос."
            )

            //fist question
            answerWhenMatches(
                word("умеешь"),
                answer = SystemCommandProcessor.aboutAnswer
            )

            // weather
            answerWhenMatches(
                word("погода"),
                optional("бунинских"),
                optionalRegexp("луга.*"),
                optional("жк")
            ) {
                weatherAdapter.getData().flatMap {
                    val answer = """
                        В Лугах сейчас: Температура ${it.fact.temp}, ощущается как ${it.fact.feels_like}, скорость ветра ${it.fact.wind_speed}
                        
                        Данные предоставлены сервисом Яндекс.Погода (https://yandex.ru/pogoda/moscow)
                        """.trimIndent()
                    Mono.just(answer)
                }
            }

            answerWhenMatches(
                word("погода"),
                answer = "Уточните пожалуйста вопрос. Например 'Какая погода в Лугах?'"
            )

            answerWhenMatches(
                word("температура"),
                answer = "Уточните пожалуйста вопрос. Например 'Какая погода в Лугах?'"
            )
            //end weather

            // Contacts
            answerWhenMatches(
                regexp("контакт.*"),
                answer = "Уточните пожалуйста вопрос. Например 'Какие контакты УК?', 'Какой телефон у УК?'"
            )

            answerWhenMatches(
                word("ук"),
                answer = "Уточните пожалуйста вопрос. Например 'Какай адрес УК?', 'Подскажи контакты УК?'"
            )

            val contacts = """
                    Управляющий Мордовин Кирилл Николаевич.
                    Адрес УК ул. Александры Монаховой д. 94 к. 5. Офис работает по вторникам и четвергам 9:00-18:00.
                    Телефон УК +7 (800) 505-89-89.
                """.trimIndent()

            answerWhenContains(
                regexp("управляющи.*"),
                answer = contacts
            )

            answerWhenMatches(
                word("телефон"),
                word("ук"),
                answer = "Телефон УК +7 (800) 505-89-89"
            )

            answerWhenMatches(
                word("время"),
                regexp("работ.*"),
                optionalRegexp("офис.*"),
                word("ук"),
                answer = contacts
            )

            answerWhenMatches(
                word("адрес"),
                word("ук"),
                answer = contacts
            )

            answerWhenMatches(
                regexp("контакт.*"),
                word("ук"),
                answer = contacts
            )

            answerWhenMatches(
                regexp("контакт.*"),
                regexp("управляющ.*"),
                answer = contacts
            )

            answerWhenMatches(
                regexp("контакт.*"),
                regexp("служб.*"),
                word("эксплуатации"),
                optionalRegexp("дом"),
                answer = contacts
            )

            answerWhenMatches(
                optionalRegexp("контакт.*"),
                regexp("управляющ.*"),
                word("эксплуатации"),
                optionalRegexp("дом"),
                answer = contacts
            )
            //end contacts

            val registerAnswer = """
                Прописаться можно в офисе заселения по адресу ул. Александры Монаховой д. 98к1 
                (вторник, четверг c 9.00 - 18.00) +7 (495) 266-93-01
                или в МФЦ по адресу ул. Александры Монаховой д. 23  
            """.trimIndent()

            answerWhenMatches(
                regexp("прописать.*"),
                optional("бунинских"),
                optionalRegexp("луга.*"),
                answer = registerAnswer
            )

            answerWhenMatches(
                regexp("прописать.*"),
                word("жк"),
                answer = registerAnswer
            )

            answerWhenMatches(
                word("прописка"),
                optionalRegexp("луга.*"),
                answer = registerAnswer
            )

            answerWhenMatches(
                optionalRegexp("квитанц.*"),
                optionalRegexp("оплат.*"),
                regexp("кап.*"),
                optionalRegexp("ремонт.*"),
                regexp("кладов.*"),
                answer = """
                    Для получения квитанции на оплату кап. ремонта кладовой необходимо лично обратиться в Расчетный центр 
                    по адресу ул. Профсоюзная д. 130 к4 в будни (при себе иметь выписку ЕГРН и паспорт)
                """.trimIndent()
            )

            answerWhenContains(
                regexp("капремонт.*"),
                answer = "Уточните пожалуйста вопрос, например 'Как получить квитанцию на оплату капремонта по кладовой?'"
            )

            answerWhenSomeMatches(
                answers(
                    variant(word("шуметь")),
                    variant(
                        regexp("проводи.*"),
                        regexp("ремонт.*"),
                        optionalRegexp("работ.*")
                    )
                ),

                answer = """
                    Можно шуметь
                    ПН-СБ 9:00-19:00 
                    Перерыв 13:00-15:00
                    
                    Нельзя
                    ВС, праздничные дни
                """.trimIndent()
            )

            answerWhenContains(
                regexp("тариф.*"),
                answer = """
                    Холодное водоснабжение - 42.30
                    Горячее водоснабжение - 42.30
                    Водоотведение - 35.82
                    Отопление - 2358.73
                    Электроснабжение - Т1=4.75, Т2=2.06, Т3=3.96
                    Содержание помещения - 32.40
                """.trimIndent()
            )

            answerWhenMatches(
                regexp("контакт.*"),
                or(regexp("мастер.*"), regexp("сантехн.*"), regexp("'электри.*"), regexp("рабоч.*")),
                answer = """
                            Контакты мастеров
                            Сантехники 
                             Андрей +7 967 201-51-63 
                             Михаил +7 905 518-58-28
                            
                            Специалисты по окнам 
                             Александр +7 910 470-68-25
                         """.trimIndent()
            )

            answerWhenContains(
                or(regexp("мастер.*"), regexp("сантехн.*"), regexp("'электри.*"), regexp("рабоч.*")),
                answer = "Уточните пожалуйста вопрос. Например 'Подскажи контакты мастеров'"
            )

        }

    }
}