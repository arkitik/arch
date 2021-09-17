package io.arkitik.arch.core

import io.arkitik.arch.core.dto.TestClassData
import io.arkitik.arch.core.parser.JacksonDataParser
import io.arkitik.arch.core.replacer.DefaultRegexReplacer

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
class SampleApp

fun main() {
    val dataParser = JacksonDataParser()
    val regexReplacer = DefaultRegexReplacer(dataParser)
    val testClassData = with(regexReplacer) {
        dataParser.run {
            val testClassData =
                SampleApp::class.java.classLoader.getResourceAsStream("sample.json")!!.parse(TestClassData::class)
            testClassData.write()
        }.replace()
    }
    println(testClassData)
}