package ru.asv.bot.rule

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import ru.asv.bot.adapter.WeatherAdapter
import ru.asv.bot.client.SystemCommandProcessor

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
                    Mono.just(
                        """
                        Сожалеем, но бесплатный ежедневный лимит запросов к сервису Яндекс.Погода превышен. 
                        Через несколько часов можно будет снова обращатьcя к сервису.
                    """.trimIndent()
                    )
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
                    Управляющий 
                      Манухин Дмитрий Алексеевич

                    Время работы офиса:
                      ВТ 9:00-18:00
                      ЧТ 9:00-18:00
                      
                    Адрес УК:
                      ул. Александры Монаховой д. 94 к. 5. 
                      
                    Телефоны УК:
                      +7 (800) 234-22-22
                      +7 (800) 505-89-89
                      +7 (495) 123-35-54
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

            val medAddress = """
                   ГБУЗ «Троицкая Городская Больница ДЗМ» 
                   Колл-центр ТГБ ДЗМ  
                     +7 (499) 638-34-32
                   Травмпункт:
                     +7 (495) 851-02-76
                   
                   Поликлиника г. Троицк
                   г. Москва, г. Троицк, ул. Юбилейная, д. 5
                   Режим работы:
                     ПН – ПТ 8.00 - 20.00
                     (процедурный кабинет с 7.30 - 20.00)

                     
                   Филиал №2 (Коммунарка)
                   108814, г. Москва, п. Сосенское, п. Коммунарка, ул. Фитаревская, д. 11
                   Телефоны:
                     +7 (495) 817-85-03
                     +7 (495) 817-83-06
                    
                   Филиал №4 (Детская)
                   108814, г. Москва, пос. Сосенское, п. Коммунарка, ул. Александры Монаховой, д. 96, к. 1  
                   Режим работы:
                     ПН – ПТ 8.00 - 20.00
                     СБ 8.00 - 15.00  
                   Телефон:
                     +7 (495) 668-87-51
                     
                   https://tgb.mos.ru/                 
            """.trimIndent()

            answerWhenContains(
                regexp("поликлиник.*"),
                answer = medAddress
            )

            answerWhenContains(
                regexp("больниц.*"),
                answer = medAddress
            )

            answerWhenMatches(
                or(regexp("телефон.*"), regexp("контакт")),
                regexp("экстренн.*"),
                regexp("служб.*"),
                answer = """
                    Телефон отделения полиции +7 (495) 817-72-02
                    
                    Единая служба спасения 112
                    Пожарная 101 (01)
                    Полиция 102 (02)    
                    Скорая 103 (03)
                    
                    МОЭСК (диспетчерская) +7 (495) 733-44-44
                    МЧС "ЛИДЕР" (дежурный) +7 (495) 424-00-33
                """.trimIndent()
            )

            answerWhenMatches(
                or(regexp("телефон.*"), regexp("контакт"), word("адрес")),
                word("школы"),
                word("338"),
                answer = """
                    Директор:
                      Андрианова Мария Петровна
                      AndrianovaMP@edu.mos.ru

                    Телефоны:
                      +7 (495) 025-65-78
                      +7 (915) 077-28-52

                    Секретарь:
                      Рихерт Екатерина Геннадьевна

                    Телефон:
                      +7 (495) 025-65-78

                    Адрес электронной почты: 338@edu.mos.ru
                """.trimIndent()
            )

            answerWhenMatches(
                or(regexp("телефон.*"), regexp("контакт")),
                word("охраны"),
                optionalRegexp("парковк.*"),
                answer = """
                    ЧОП Неомакс +7 (916) 790-37-25 
                    Охрана Парковки +7 (977) 606-39-73
                """.trimIndent()
            )

            //end contacts

            val registerAnswer = """
                Прописаться можно
                1. (Паспортный стол) В офисе УК по адресу ул. Александры Монаховой д. 94 к. 5 
                   Необходимо предварительно записаться по телефону +7 (495) 122-23-76
                   Время работы паспортного стола:
                     ПН 10:00 - 16:00
                     СР 10:00 - 16:00
                     ПТ 10:00 - 16:00
                     Перерыв с 13:00 - 14:00
                   При себе иметь выписку из ЕГРН, паспорт   
                
                2. (МВД) Записаться на портале госуслуг http://gosuslugi.ru или http://mos.ru 
                   в МВД для регистрации по месту жительства, выбрать МФЦ и прийти по записи.
                   
                   МФЦ п. Коммунарка находится по адресу ул. Александры Монаховой д. 23  
                   
                   Режим работы МВД в МФЦ:
                     ПН 09.00 - 18.00
                     ВТ 11:00 - 20.00
                     СР 09.00 - 13:00
                     ЧТ 11.00 - 20.00
                     ПТ 09.00 - 16.45
                     1-я и 3-я субботы с 9.00 - 13.00 
                     после этих суббот понедельник выходной
                     Обед с 14:00 - 14:45
                   
                   Прием ведется на втором этаже
                
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
                or(word("время"), word("режим")),
                word("работы"),
                word("мвд"),
                answer = registerAnswer
            )

            answerWhenMatches(
                or(word("адрес"), regexp("контакт.*")),
                regexp("паспортн.*"),
                regexp("стол.*"),
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
                or(regexp("мастер.*"), regexp("сантехн.*"), regexp("электри.*"), regexp("рабоч.*")),
                answer = """
                    Контакты мастеров
                    Сантехники
                      Андрей +7 967 201-51-63 
                      Михаил (рекомендуют в чате дома 94 к1/к2) +7 905 518-58-28
                      
                    Электрики 
                      Алексей +7 965 438-98-04
                            
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
                or(regexp("полиц.*"), word("мвд")),
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
                answer = "+7 (999) 010-77-46"
            )

            answerWhenContains(
                word("мвд"),
                answer = "Уточните пожалуйста вопрос. Например: 'Подскажи контакты отделения полиции?', 'Подскажи режим работы МВД', 'Как прописаться?' "
            )

            val pochtaAddress = """
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

            answerWhenMatches(
                or(regexp("контакт.*"), word("адрес")),
                regexp("почт.*"),
                optionalRegexp("отделен.*"),
                answer = pochtaAddress
            )

            answerWhenMatches(
                or(regexp("контакт.*"), word("адрес")),
                regexp("отделен.*"),
                word("почты"),
                answer = pochtaAddress
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