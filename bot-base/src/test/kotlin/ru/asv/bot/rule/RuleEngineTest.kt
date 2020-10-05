package ru.asv.bot.rule

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.asv.bot.adapter.WeatherAdapter
import ru.asv.bot.text.NaiveSentenceProcessor
import ru.asv.bot.text.NaiveWordProcessor
import ru.asv.bot.text.WordProcessor
import kotlin.test.assertTrue

@SpringBootTest(classes = [LugaBotRules::class, WeatherAdapter::class, NaiveWordProcessor::class])
//@Disabled
class RuleEngineTest {

    @Autowired
    lateinit var rules: RuleEngine

    @Autowired
    lateinit var wp: WordProcessor

    @Test
    fun testSimpleQueries() {
        // success
        //assertTrue { answer("Какая погода в ЖК?").contains("") }
        //assertTrue { answer("Какая погода в бунинских лугах?").contains("") }
        //assertTrue { answer("Какая погода в лугах?").contains("") }

        assertTrue { answer("телефон охраны").startsWith("ЧОП Неомакс")}
        assertTrue { answer("телефоны экстренных служб").startsWith("Телефон отделения полиции")}
        assertTrue { answer("телефон школы 338").startsWith("Директор")}

        assertTrue { answer("адрес опорного пункта полиции").contains("ул. Александры Монаховой")}
        assertTrue { answer("контакт участкового").startsWith("+7 (999)")}

        assertTrue { answer("контакты почтового отделения").contains("Отделение почтовой связи")}
        assertTrue { answer("контакты отделения почты").contains("Отделение почтовой связи")}
        assertTrue { answer("контакты почты").contains("Отделение почтовой связи") }
        assertTrue { answer("адрес почты").contains("Отделение почтовой связи") }

        assertTrue { answer("контакты отделения полиции").contains("15А, посёлок Коммунарка") }
        assertTrue { answer("телефон полиции").contains("15А, посёлок Коммунарка") }

        assertTrue { answer("контакты поликлиники к которой прикреплены").startsWith("ГБУЗ «Троицкая Городская Больница ДЗМ»") }
        assertTrue { answer("контакты районной больницы").startsWith("ГБУЗ «Троицкая Городская Больница ДЗМ»") }

        assertTrue { answer("мастер слово слово").contains("Уточните пожалуйста вопрос") }
        assertTrue { answer("сантехник слово слово").contains("Уточните пожалуйста вопрос") }
        assertTrue { answer("Подскажи контакты мастеров").contains("Контакты мастеров") }
        assertTrue { answer("Подскажи контакты сантехника").contains("Контакты мастеров") }
        assertTrue { answer("когда можно шуметь").contains("Можно шуметь") }
        assertTrue { answer("когда можно проводить ремонтные работы").contains("Можно шуметь") }
        assertTrue { answer("когда можно проводить ремонт").contains("Можно шуметь") }

        assertTrue { answer("управляющий").startsWith("Управляющий") }
        assertTrue { answer("контакты управляющего").startsWith("Управляющий") }
        assertTrue { answer("Какой телефон в УК").startsWith("Телефон УК") }
        assertTrue { answer("Какой адрес УК?").startsWith("Управляющий") }
        assertTrue { answer("Подскажи контакты УК").startsWith("Управляющий") }
        assertTrue { answer("Какое время работы офиса УК").startsWith("Управляющий") }

        assertTrue { answer("прописка").contains("Прописаться можно") }
        assertTrue { answer("Как прописаться в лугах").contains("Прописаться можно") }
        assertTrue { answer("Как прописаться в бунинских лугах").contains("Прописаться можно") }
        assertTrue { answer("Как прописаться?").contains("Прописаться можно") }
        assertTrue { answer("Как прописаться в ЖК?").contains("Прописаться можно") }
        assertTrue { answer("Подскажи контакты паспортного стола").contains("Прописаться можно") }

        assertTrue { answer("время работы мвд").startsWith( "Прописаться можно") }
        assertTrue { answer("Подскажи режим работы МВД").startsWith( "Прописаться можно") }

        assertTrue { answer("мвд").startsWith("Уточните") }

        assertTrue { answer("квитанция на оплату капитального ремонта кладовой").contains("Для получения квитанции") }
        assertTrue { answer("Как получить квитанцию на оплату кап ремонта кладовой").contains("Для получения квитанции") }
        assertTrue { answer("Как получить квитанцию на капремонт кладовой").contains("Для получения квитанции") }
        assertTrue { answer("Как получить квитанцию на оплату капремонта кладовой").contains("") }
        assertTrue { answer("капремонт кладовой").contains("Для получения квитанции") }
        assertTrue { answer("Как получить квитанцию на оплату капремонта по кладовой?").contains("Для получения квитанции") }
        assertTrue { answer("капремонт").contains("Уточните пожалуйста вопрос,") }
        assertTrue { answer("слово капремонт слово").contains("Уточните пожалуйста вопрос,") }

        // error
        println("=== Error")
        println(wp.determineAnswer(listOf("погода12", "бунинских", "лугах"), rules).block())
        println(wp.determineAnswer(listOf("погода"), rules).block())
        println(wp.determineAnswer(listOf("лугах"), rules).block())
        println(wp.determineAnswer(listOf("бунинских", "лугах"), rules).block())
        println(wp.determineAnswer(listOf("телефон", "ук", "123"), rules).block())
        println(wp.determineAnswer(listOf("телефон"), rules).block())
        println(wp.determineAnswer(listOf("телефон", "телефон", "телефон"), rules).block())
        println(wp.determineAnswer(listOf("телефон", "телефон", "телефон", "dsdsdd3423424", "dsasdasd"), rules).block())


    }

    fun answer(q: String): String {
        val answer  = wp.determineAnswer(NaiveSentenceProcessor().splitToWords(q), rules).block()!!
        //println(answer)
        return answer
    }

}