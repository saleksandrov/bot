package ru.asv.bot.rule

import org.junit.Test

class RuleTest {

    @Test
    fun main() {

        val lugaBot = LugaBotRules()

        // success
        println(WordProcessor().determineAnswer(listOf("погода", "лугах"), lugaBot))
        println(WordProcessor().determineAnswer(listOf("погода", "бунинских", "лугах"), lugaBot))
        println(WordProcessor().determineAnswer(listOf("телефон", "ук"), lugaBot))

        // error
        println(WordProcessor().determineAnswer(listOf("погода12", "бунинских", "лугах"), lugaBot))
        println(WordProcessor().determineAnswer(listOf("погода"), lugaBot))
        println(WordProcessor().determineAnswer(listOf("лугах"), lugaBot))
        println(WordProcessor().determineAnswer(listOf("бунинских", "лугах"), lugaBot))
        println(WordProcessor().determineAnswer(listOf("телефон", "ук", "123"), lugaBot))
        println(WordProcessor().determineAnswer(listOf("телефон"), lugaBot))
        println(WordProcessor().determineAnswer(listOf("телефон", "телефон", "телефон"), lugaBot))
        println(WordProcessor().determineAnswer(listOf("телефон", "телефон", "телефон", "dsdsdd3423424", "dsasdasd"), lugaBot))

    }

}