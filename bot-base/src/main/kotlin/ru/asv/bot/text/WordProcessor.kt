package ru.asv.bot.text

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import ru.asv.bot.rule.RuleEngine
import java.util.concurrent.TimeUnit

interface WordProcessor {
    fun determineAnswer(questionWords: List<String>, ruleEngine: RuleEngine): Mono<String>
}

@Component
class NaiveWordProcessor : WordProcessor {

    private val log = LoggerFactory.getLogger(WordProcessor::class.java)

    override fun determineAnswer(questionWords: List<String>, ruleEngine: RuleEngine): Mono<String> {
        var answer: Mono<String>? = null
        var totalMatches = 0
        val startTime = System.nanoTime()
        ruleEngine.ac.answers().forEach answer@{ patternWordsList, v ->
            var errors = 0
            var startIndex = 0
            patternWordsList.forEach { patternWord ->
                if (questionWords.size >= startIndex + 1) {
                    totalMatches++
                    val matches = patternWord.matches(questionWords[startIndex])
                    if (!matches) {
                        if (!patternWord.isOptional()) {
                            errors++
                            startIndex++
                        }
                    } else {
                        startIndex++
                    }
                } else if (patternWord.isOptional()) {
                    // do nothing
                } else {
                    // Size of question words is lower than our answer pattern => is not matched
                    return@answer
                }
            }
            if (startIndex <= questionWords.size - 1) {
                return@answer
            }
            if (errors == 0) {
                answer = v()
                return@answer
            }
        }

        // try to search  by keywords if no matches
        if (answer == null) {
            ruleEngine.ac.keyWordAnswers().forEach { patternWordsList, v ->
                var matches = 0
                questionWords.forEach { questionWord ->
                    patternWordsList.forEach {patternWord ->
                        totalMatches++
                        if (patternWord.matches(questionWord)) {
                            matches++
                        }
                    }
                    // for small optimization
                    if (matches == patternWordsList.size) {
                        answer = v()
                    }
                }
                if (matches == patternWordsList.size) {
                    answer = v()
                }
            }
        }

        if (log.isInfoEnabled) {
            val workTime = System.nanoTime() - startTime
            val found = if (answer != null) "YES" else "NO"
            log.info(
                """Word algorithm metrics 
                    totalMatches=${totalMatches} 
                    workTime=${TimeUnit.MILLISECONDS.convert(workTime, TimeUnit.NANOSECONDS)}
                    workTimeNano=${workTime}
                    foundAnswer=${found}
                 """.trimIndent()
            )
        }
        return answer ?: ruleEngine.defaultAnswer()
    }
}

interface Word {

    fun matches(value: String): Boolean

    fun isOptional(): Boolean {
        return false
    }

}

data class RequiredWord(private val word: String) : Word {

    override fun matches(value: String): Boolean {
        return word == value
    }

}

data class OptionalWord(private val word: Word) : Word {

    override fun matches(value: String): Boolean {
        return word.matches(value)
    }

    override fun isOptional(): Boolean {
        return true
    }

}

data class RegexpWord(private val word: String) : Word {

    private val regex: Regex = word.toRegex()

    override fun matches(value: String): Boolean {
        return value.matches(regex)
    }

}

data class MultiWord(private val words: List<Word>) : Word {

    override fun matches(value: String): Boolean {
        words.forEach {
            if (it.matches(value)) {
                return true
            }
        }
        return false
    }

}
