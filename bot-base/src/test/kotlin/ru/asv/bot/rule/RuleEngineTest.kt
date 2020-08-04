package ru.asv.bot.rule

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.asv.bot.adapter.WeatherAdapter
import ru.asv.bot.text.NaiveSentenceProcessor
import ru.asv.bot.text.NaiveWordProcessor
import ru.asv.bot.text.WordProcessor

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
        println("== Success")
        //println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Какая погода в ЖК?"), rules).block())
        //println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Какая погода в бунинских лугах?"), rules).block())
        //println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Какая погода в лугах?"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Какой телефон в УК"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Какой адрес УК?"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Подскажи контакты УК"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Какое время работы офиса УК"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("прописка"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Как прописаться в лугах"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Как прописаться в бунинских лугах"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Как прописаться?"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Как прописаться в ЖК?"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("квитанция на оплату капитального ремонта кладовой"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Как получить квитанцию на оплату кап ремонта кладовой"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Как получить квитанцию на капремонт кладовой"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Как получить квитанцию на оплату капремонта кладовой"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("капремонт кладовой"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("Как получить квитанцию на оплату капремонта по кладовой?"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("капремонт"), rules).block())
        println(wp.determineAnswer(NaiveSentenceProcessor().splitToWords("слово капремонт слово"), rules).block())
        println("")

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

}