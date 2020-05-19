package ru.asv.bot.rule

import ru.asv.bot.text.Word


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
