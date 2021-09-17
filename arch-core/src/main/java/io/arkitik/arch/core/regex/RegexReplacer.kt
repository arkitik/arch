package io.arkitik.arch.core.regex

import io.arkitik.arch.core.dto.TestClassData

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
interface RegexReplacer {
    fun String.replace(): TestClassData
}