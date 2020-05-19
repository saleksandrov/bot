package ru.asv.bot.rule


open class RuleEngine {

    val answers = mutableMapOf<List<Word>, () -> String>()

    private val defaultAnswers = mutableListOf<String>()

    fun botRule(init: () -> Unit) {
        init()
    }

    fun answer(init: AnswerContext.() -> Unit) {
        val ac = AnswerContext()
        ac.init()
        answers[ac.patterns] = ac.answerFun
    }

    fun defaultAnswers(init: () -> List<String>) {
        defaultAnswers += init()
    }

    fun defaultAnswer(): String {
        return defaultAnswers[(0..defaultAnswers.lastIndex).random()]
    }

}

class AnswerContext {

    lateinit var patterns: List<Word>
    lateinit var answerFun: () -> String

    fun thenAnswer(answerFun: () -> String) {
        this.answerFun = answerFun
    }

    fun whenMatches(patterns: () -> List<Word>) {
        this.patterns = patterns()
    }

}

class WordProcessor {

    fun determineAnswer(questionWords: List<String>, ruleEngine: RuleEngine): String {
        var answer = ruleEngine.defaultAnswer()
        ruleEngine.answers.forEach answer@{ patternWordsList, v ->
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

    override fun matches(value: String): Boolean {
        return value.matches(word.toRegex())
    }

}


