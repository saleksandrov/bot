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
                }.onErrorResume {
                    Mono.just("""
                        Сожалеем, но бесплатный ежедневный лимит запросов к сервису Яндекс.Погода превышен. 
                        Через несколько часов можно будет снова обращатьcя к сервису.
                    """.trimIndent())
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

            answerWhenMatches(
                or(word("телефон"), regexp("контакт.*")),
                word("пик"),
                answer = """
                    Главный офис
                      123242, Россия, г. Москва, ул. Баррикадная, 19 стр. 1
                      
                    Телефоны:
                      +7 (495) 266-19-71 
                      +7 (495) 308-09-79
                    E-mail info@pik.ru
                     
                """.trimIndent()
            )

            val contacts = """
                    Управляющий Мордовин Кирилл Николаевич.
                    Адрес УК ул. Александры Монаховой д. 94 к. 5. Офис работает по вторникам и четвергам 9:00-18:00.
                    Телефон УК +7 (800) 505-89-89, +7 (495) 123-35-54
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
                optionalRegexp("офис.*"),
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
                Прописаться можно
                1. (После 31 августа) В офисе УК по адресу ул. Александры Монаховой д. 94 к. 5 
                Время работы:
                  ПН, СР с 10:00 - 16:00
                  Обед с 13:00 - 14:00
                При себе иметь выписку из ЕГРН, паспорт   
                
                2. Записаться на портале госуслуг http://gosuslugi.ru или http://mos.ru 
                   в МВД для регистрации по месту жительства, выбрать МФЦ и прийти по записи.
                   МФЦ п. Коммунарка находится по адресу ул. Александры Монаховой д. 23  
                
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
                or(word("адрес"), regexp("контакт.*")),
                regexp("офис.*"),
                regexp("заселен.*"),
                answer = "ул. Александры Монаховой д. 98к1 (вторник, четверг c 9.00 - 18.00) +7 (495) 266-93-01"
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
                             
                    Разные виды работ:
                      Дмитрий +7 910 443-22-80 
                """.trimIndent()
            )

            answerWhenContains(
                or(regexp("мастер.*"), regexp("сантехн.*"), regexp("'электри.*"), regexp("рабоч.*")),
                answer = "Уточните пожалуйста вопрос. Например 'Подскажи контакты мастеров'"
            )

            answerWhenMatches(
                or(regexp("контакт.*"), word("адрес")),
                regexp("опорн.*"),
                regexp("пункт.*"),
                regexp("полиц.*"),
                answer = "ул. Александры Монаховой, дом 105к2 https://yandex.ru/maps/-/CCQtMNdv~A"
            )

            answerWhenMatches(
                or(regexp("контакт.*"), word("телефон"), word("адрес")),
                optionalRegexp("отделени.*"),
                or(regexp("полиц.*"), word("МВД")),
                optionalRegexp("коммунарк.*"),
                answer = """
                    15А, посёлок Коммунарка 
                    Телефоны: 
                      +7 (495) 817-72-02 
                    Режим работы:
                      ПН Выходной
                      ВТ 18:00 – 20:00
                      СР Выходной
                      ЧТ 18:00 – 20:00
                      ПТ Выходной
                      СБ 10:00 – 12:00
                      ВС Выходной
                """.trimIndent()
            )

            answerWhenContains(
                regexp("участков.*"),
                answer = "Московский Дмитрий Анатольевич +7 (999) 010-77-12"
            )

            answerWhenMatches(
                or(regexp("контакт.*"), word("адрес")),
                regexp("почт.*"),
                optionalRegexp("отделен.*"),
                answer = """
                            Отделение почтовой связи Коммунарка 108801
                            ул. Липовый Парк, 8, корп. 2
                            Телефоны: 
                              +7 (800) 200-58-88
                              +7 (800) 100-00-00
                              +7 (929) 555-45-63
                            Режим работы:
                              ПН – ПТ 08:00 - 20:00
                              СБ      09:00 - 18:00
                              ВС      Выходной
                              
                            https://yandex.ru/maps/-/CCQtMRfhTA
                            
                            """.trimIndent()
            )

            answerWhenContains(
                word("мфц"),
                answer = """
                    ул. Александры Монаховой д. 23
                    Телефон:
                       +7 (495) 777-77-77 
                    Режим работы:
                       ПН – ВС  08:00 - 20:00  
                        
                    https://yandex.ru/maps/-/CCQtMRGg-D
                """.trimIndent()
            )

        }

    }
}