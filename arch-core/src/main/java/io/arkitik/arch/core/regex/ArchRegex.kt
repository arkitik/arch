package io.arkitik.arch.core.regex

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
interface ArchRegex {
    val regex: Regex
    val preProcess: Boolean

    fun String.isMatch(): Boolean
    fun String.replace(transformer: (MatchResult) -> String): String
}