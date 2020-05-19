package ru.asv.bot.rule

import reactor.core.publisher.Mono
import ru.asv.bot.text.Word


open class RuleEngine {

    val answers = mutableMapOf<List<Word>, () -> Mono<String>>()

    private val defaultAnswers = mutableListOf<String>()

    protected fun botRule(init: () -> Unit) {
        init()
    }

    protected fun answer(init: AnswerContext.() -> Unit) {
        val ac = AnswerContext()
        ac.init()
        answers[ac.patterns] = ac.answerFun
    }

    protected fun defaultAnswers(init: () -> List<String>) {
        defaultAnswers += init()
    }

    fun defaultAnswer(): Mono<String> {
        return Mono.just(defaultAnswers[(0..defaultAnswers.lastIndex).random()])
    }

}

class AnswerContext {

    lateinit var patterns: List<Word>
    lateinit var answerFun: () -> Mono<String>

    fun thenAnswer(answerFun: () -> Mono<String>) {
        this.answerFun = answerFun
    }

    fun whenMatches(patterns: () -> List<Word>) {
        this.patterns = patterns()
    }

}
