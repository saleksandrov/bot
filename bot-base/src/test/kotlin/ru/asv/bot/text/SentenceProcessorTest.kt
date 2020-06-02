package ru.asv.bot.text

import org.junit.jupiter.api.Test

class SentenceProcessorTest {

    @Test
    fun testProcessor() {
        println(NaiveSentenceProcessor().splitToWords("!Какая погода в лугах?"))
        println(NaiveSentenceProcessor().splitToWords("Подскажи контакты УК"))
    }

}