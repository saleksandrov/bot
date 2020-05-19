package ru.asv.bot.rule

import org.junit.Test
import ru.asv.bot.text.NaiveWordProcessor

class RuleEngineTest {
    
    private val wp = NaiveWordProcessor()

    @Test
    fun main() {

        //val lugaBot = LugaBotRules(WeatherAdapter())

        // success
        //println(wp.determineAnswer(listOf("погода", "лугах"), lugaBot))
        //println(wp.determineAnswer(listOf("погода", "бунинских", "лугах"), lugaBot))
        //println(wp.determineAnswer(listOf("телефон", "ук"), lugaBot))

        // error
        //println(wp.determineAnswer(listOf("погода12", "бунинских", "лугах"), lugaBot))
        //println(wp.determineAnswer(listOf("погода"), lugaBot))
        //println(wp.determineAnswer(listOf("лугах"), lugaBot))
        //println(wp.determineAnswer(listOf("бунинских", "лугах"), lugaBot))
        //println(wp.determineAnswer(listOf("телефон", "ук", "123"), lugaBot))
        //println(wp.determineAnswer(listOf("телефон"), lugaBot))
        //println(wp.determineAnswer(listOf("телефон", "телефон", "телефон"), lugaBot))
        //println(wp.determineAnswer(listOf("телефон", "телефон", "телефон", "dsdsdd3423424", "dsasdasd"), lugaBot))

    }

}