package ru.asv.bot.rule

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.asv.bot.adapter.WeatherAdapter
import ru.asv.bot.text.NaiveWordProcessor
import ru.asv.bot.text.WordProcessor

@SpringBootTest(classes = [LugaBotRules::class, WeatherAdapter::class, NaiveWordProcessor::class])
@Disabled
class RuleEngineTest {

    @Autowired
    lateinit var rules: RuleEngine

    @Autowired
    lateinit var wp: WordProcessor

    @Test
    fun testSimpleQueries() {
        // success
        println(wp.determineAnswer(listOf("погода", "лугах"), rules).block())
        println(wp.determineAnswer(listOf("погода", "бунинских", "лугах"), rules).block())
        println(wp.determineAnswer(listOf("телефон", "ук"), rules).block())

        // error
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