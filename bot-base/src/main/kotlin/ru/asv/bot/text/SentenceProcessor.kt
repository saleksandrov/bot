package ru.asv.bot.text

import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils

interface SentenceProcessor {

    fun splitToWords(sentence: String): List<String>
}

@Component
class NaiveSentenceProcessor: SentenceProcessor {

    private val patternRegexp = Regex("[!@#$%^&*()?\"'><.,{}\\[\\]]")
    private val splitRegex = Regex("\\s+")
    private val stopWords: Set<String> = ResourceUtils
        .getFile("classpath:text/stopwords.txt")
        .readLines(Charsets.UTF_8).toSet()

    init {
        // load stopwords
    }

    override fun splitToWords(sentence: String): List<String> {
        val reducedSentence = sentence.toLowerCase().replace(patternRegexp, "")
        val words = reducedSentence.split(splitRegex)
        return words.filter { !stopWords.contains(it) }.toList()
    }
}

fun main() {


}