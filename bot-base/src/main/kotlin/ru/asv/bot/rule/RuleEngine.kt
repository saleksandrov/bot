package ru.asv.bot.rule

import reactor.core.publisher.Mono
import ru.asv.bot.text.OptionalWord
import ru.asv.bot.text.RegexpWord
import ru.asv.bot.text.RequiredWord
import ru.asv.bot.text.Word


open class RuleEngine {

    val answers = mutableMapOf<List<Word>, () -> Mono<String>>()

    private val defaultAnswers = mutableListOf<String>()

    protected fun botRule(init: () -> Unit) {
        init.invoke()
    }

    protected fun answer(init: AnswerContext.() -> Unit) {
        val ac = AnswerContext()
        ac.init()
        answers[ac.patterns] = ac.answerFun
    }

    protected fun defaultAnswers(vararg answers: String) {
        defaultAnswers += answers
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

    fun thenAnswer(answer: String) {
        this.answerFun = { Mono.just(answer) }
    }

    fun whenMatches(vararg patterns: Word) {
        this.patterns = patterns.toList()
    }

    fun word(word: String): Word {
        return RequiredWord(word)
    }

    fun optional(word: String): Word {
        return OptionalWord(RequiredWord(word))
    }

    fun regexp(word: String): Word {
        return RegexpWord(word)
    }

}
