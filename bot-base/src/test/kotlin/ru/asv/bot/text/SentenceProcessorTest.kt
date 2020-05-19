package ru.asv.bot.text

import org.junit.Test

class SentenceProcessorTest {

    @Test
    fun testProcessor() {
        println(NaiveSentenceProcessor().splitToWords("!Какая погода в лугах?"))
    }

}