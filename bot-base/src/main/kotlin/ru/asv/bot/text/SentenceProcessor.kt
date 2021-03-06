package ru.asv.bot.text

import org.springframework.stereotype.Component

interface SentenceProcessor {

    fun splitToWords(sentence: String): List<String>
}

@Component
class NaiveSentenceProcessor: SentenceProcessor {

    private val patternRegexp = Regex("[!@#$%^&*()?\"'><.,{}\\[\\]]")
    private val splitRegex = Regex("\\s+")
    private val stopWords: Set<String> = Stopwords().stopwords.split(Regex("\\n")).toSet()

    override fun splitToWords(sentence: String): List<String> {
        val reducedSentence = sentence.toLowerCase().replace(patternRegexp, "")
        val words = reducedSentence.split(splitRegex)
        return words.filter { !stopWords.contains(it) }.toList()
    }
}
