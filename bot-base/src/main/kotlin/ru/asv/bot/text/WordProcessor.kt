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
        var answer = ruleEngine.defaultAnswer()
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

                } else {
                    // Size of question words is greater than our answer pattern => is not matched
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
        return answer
    }
}

interface Word {

    fun matches(value: String): Boolean

    fun isOptional(): Boolean {
        return false
    }

}

data class RequiredWord(val word: String) : Word {

    override fun matches(value: String): Boolean {
        return word == value
    }

}

data class OptionalWord(val word: Word) : Word {

    override fun matches(value: String): Boolean {
        return word.matches(value)
    }

    override fun isOptional(): Boolean {
        return true
    }

}

data class RegexpWord(val word: String) : Word {

    private val regex: Regex = word.toRegex()

    override fun matches(value: String): Boolean {
        return value.matches(regex)
    }

}


