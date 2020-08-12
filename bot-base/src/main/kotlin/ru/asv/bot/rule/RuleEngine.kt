package ru.asv.bot.rule

import reactor.core.publisher.Mono
import ru.asv.bot.text.*


open class RuleEngine {

    private val defaultAnswers = mutableListOf<String>()

    lateinit var ac: AnswerContext

    protected fun botRule(init: AnswerContext.() -> Unit) {
        val ac = AnswerContext()
        ac.init()
        this.ac = ac
    }

    protected fun defaultAnswers(vararg answers: String) {
        defaultAnswers += answers
    }

    fun defaultAnswer(): Mono<String> {
        return Mono.just(defaultAnswers[(0..defaultAnswers.lastIndex).random()])
    }

}

class AnswerContext {

    private val answers = mutableMapOf<List<Word>, () -> Mono<String>>()
    private val keyWordAnswers = mutableMapOf<List<Word>, () -> Mono<String>>()

    private lateinit var patterns: List<Word>
    private lateinit var answerFun: () -> Mono<String>

    fun answers(): Map<List<Word>, () -> Mono<String>> {
        return this.answers.toMap()
    }

    fun keyWordAnswers(): Map<List<Word>, () -> Mono<String>> {
        return this.keyWordAnswers.toMap()
    }

    private fun thenAnswer(answerFun: () -> Mono<String>) {
        this.answerFun = answerFun
    }

    private fun thenAnswer(answer: String) {
        this.answerFun = { Mono.just(answer) }
    }

    fun answerWhenMatches(vararg patterns: Word, answer: () -> Mono<String>) {
        // rewrites value for each invocation (not thread safe)
        this.patterns = patterns.toList()
        thenAnswer(answer)
        answers[this.patterns] = this.answerFun
    }

    fun answerWhenMatches(vararg patterns: Word, answer: String) {
        this.patterns = patterns.toList()
        thenAnswer(answer)
        answers[this.patterns] = this.answerFun
    }

    fun answerWhenSomeMatches(patterns: List<Array<out Word>>, answer: String) {
        patterns.forEach {
            thenAnswer(answer)
            answers[it.toList()] = this.answerFun
        }
    }

    fun variant(vararg patterns: Word): Array<out Word> {
        return patterns
    }

    fun answers(vararg variants: Array<out Word>): List<Array<out Word>> {
        return variants.toList()
    }

    fun answerWhenContains(vararg patterns: Word, answer: String) {
        thenAnswer(answer)
        keyWordAnswers[patterns.toList()] = this.answerFun
    }

    fun word(word: String): Word {
        return RequiredWord(word)
    }

    fun optional(word: String): Word {
        return OptionalWord(RequiredWord(word))
    }

    fun optionalRegexp(word: String): Word {
        return OptionalWord(RegexpWord(word))
    }

    fun regexp(word: String): Word {
        return RegexpWord(word)
    }

    fun or(vararg words: Word): Word {
        words.forEach {
            if (it.isOptional()) {
                throw RuntimeException("Is not allowed to add optional word to OR predicate")
            }
        }
        return MultiWord(words.toList())
    }

}
