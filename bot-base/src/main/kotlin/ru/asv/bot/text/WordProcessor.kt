package ru.asv.bot.text

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import ru.asv.bot.rule.RuleEngine

interface WordProcessor {
    fun determineAnswer(questionWords: List<String>, ruleEngine: RuleEngine): Mono<String>
}

@Component
class NaiveWordProcessor : WordProcessor {

    override fun determineAnswer(questionWords: List<String>, ruleEngine: RuleEngine): Mono<String> {
        var answer: Mono<String>? = null
        ruleEngine.ac.answers().forEach answer@{ patternWordsList, v ->
            var errors = 0
            var startIndex = 0
            patternWordsList.forEach { patternWord ->
                if (questionWords.size >= startIndex + 1) {
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

        if (answer == null) {
            ruleEngine.ac.keyWordAnswers().forEach { patternWordsList, v ->
                var matches = 0
                questionWords.forEach { questionWord ->
                    patternWordsList.forEach {patternWord ->
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
