package io.arkitik.arch.core.type

import io.arkitik.arch.core.regex.ArchRegex

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
open class DefaultArchRegex(
    override val regex: Regex,
    override val preProcess: Boolean,
) : ArchRegex {
    override fun String.isMatch() = regex.matches(this)
    override fun String.replace(transformer: (MatchResult) -> String): String {
        require(isMatch()) {
            "Invalid input, $this didn't match ${regex.pattern}."
        }
        return ""
    }
}