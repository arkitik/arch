package io.arkitik.arch.core.replacer

import io.arkitik.arch.core.dto.TestClassData
import io.arkitik.arch.core.dto.TestScenario
import io.arkitik.arch.core.function.DataParser
import io.arkitik.arch.core.regex.RegexReplacer
import io.arkitik.arch.core.toMap
import io.arkitik.arch.core.toObject
import org.apache.commons.lang3.RandomStringUtils
import org.iban4j.CountryCode
import org.iban4j.Iban
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
class DefaultRegexReplacer(
    private val dataParser: DataParser,
) : RegexReplacer {

    override fun String.replace(): TestClassData {
        val updatedClassData = dataParser.run {
            val replaced = NOW_REGEX.replace(this@replace) {
                TIMESTAMP_FORMATTER.format(LocalDateTime.now())
            }.replace(regex = TIME_REGEX) {
                val isPlus = it.groupValues[1] == "+"
                val addedAmount = it.groupValues[2].toInt()
                val addedType = it.groupValues[3]
                TIMESTAMP_FORMATTER.format(
                    DateAmountType.localDateTime(isPlus, addedType, addedAmount)
                )
            }.replace(regex = RANDOM_REGEX) {
                val randomDataType = RandomDataType.valueOf(it.groupValues[1])
                val randomSize = it.groupValues[2].toInt()
                randomDataType.generator(randomSize)
            }
            replaced.parse(TestClassData::class)
        }
        val scenarios: List<TestScenario> = updatedClassData.scenarios
            .map {
                val testCasesMaps = it.cases.map { test ->
                    test.toMap()
                }
                val updatedTestCasesMaps = testCasesMaps.map { map ->
                    replaceRegex(map, testCasesMaps)
                }
                TestScenario(it.name, updatedTestCasesMaps.map { map ->
                    map.toObject()
                })
            }
        return TestClassData(scenarios)
    }

    private fun replaceRegex(element: Map<String, Any>, testCasesMaps: List<Map<String, Any>>): HashMap<String, Any> {
        val newElement = HashMap(element)
        for (i in 0..3) {
            newElement.forEach {
                newElement[it.key] = resolveValue(it, newElement, testCasesMaps)
            }
        }
        return newElement
    }

    @Suppress("UNCHECKED_CAST")
    private fun resolveValue(
        entry: Map.Entry<String, Any>,
        selfElementValue: HashMap<String, Any>,
        testCasesMaps: List<Map<String, Any>>,
    ): Any {
        return valueReplacer(entry.value, selfElementValue, testCasesMaps)
    }

    @Suppress("UNCHECKED_CAST")
    private fun valueReplacer(
        value: Any,
        selfElementValue: HashMap<String, Any>,
        testCasesMaps: List<Map<String, Any>>,
    ): Any {
        return when (value) {
            is Map<*, *> -> {
                val newValue = HashMap<String, Any?>()
                value.forEach {
                    if (it.value != null)
                        newValue[it.key.toString()] = valueReplacer(it.value!!, selfElementValue, testCasesMaps)
                    else
                        newValue[it.key.toString()] = null
                }
                newValue
            }
            is List<*> -> {
                val newValue = ArrayList<Any?>()
                value.forEach {
                    if (it != null)
                        newValue.add(valueReplacer(it, selfElementValue, testCasesMaps))
                    else
                        newValue.add(null)
                }
                newValue
            }
            is Set<*> -> {
                val newValue = HashSet<Any?>()
                value.forEach {
                    if (it != null)
                        newValue.add(valueReplacer(it, selfElementValue, testCasesMaps))
                    else
                        newValue.add(null)
                }
                newValue
            }
            is String -> {
                PATH_REGEX.replace(value) {
                    val map = testCasesMaps[it.groupValues[1].toInt()]
                    val pathValue = it.groupValues[2]
                    pathValueResolver(pathValue, map) as String? ?: it.value
                }.replace(regex = SELF_REF_REGEX) {
                    val pathValue = it.groupValues[1]
                    pathValueResolver(pathValue, selfElementValue) as String? ?: it.value
                }.replace(regex = IBAN_REF_REGEX) {
                    ibanReferenceReplacer(it, selfElementValue)
                }.replace(regex = IBAN_REGEX) {
                    ibanReplacer(it)
                }
            }
            else -> value
        }
    }

    private fun ibanReplacer(it: MatchResult): CharSequence {
        val countryCode = it.groupValues[1]
        val bankCode = it.groupValues[2]
        val accountNumber = it.groupValues[3]
        return Iban.Builder()
            .accountNumber(accountNumber)
            .bankCode(bankCode)
            .countryCode(CountryCode.getByCode(countryCode))
            .build()
            .toString()
    }

    private fun ibanReferenceReplacer(it: MatchResult, selfElementValue: HashMap<String, Any>): CharSequence {
        val countryCode = it.groupValues[1]
        val bankCode = it.groupValues[2]
        val path = it.groupValues[3]
        val pathValue = pathValueResolver(path, selfElementValue) as String?
        return if (pathValue == null) {
            it.value
        } else {
            if (pathValue.matches("([0-9]+)".toRegex()))
                Iban.Builder()
                    .accountNumber(pathValue)
                    .bankCode(bankCode)
                    .countryCode(CountryCode.getByCode(countryCode))
                    .build()
                    .toString()
            else
                it.value
        }
    }

    private fun pathValueResolver(pathValue: String, map: Map<String, Any>, delimiter: String = "."): Any? {
        var value: Any? = map
        pathValue.split(delimiter)
            .forEach {
                value = if (it.toIntOrNull() != null) {
                    (value as List<*>)[it.toInt()]
                } else {
                    (value as Map<*, *>)[it]
                }
            }
        return value
    }

    companion object {
        private val RANDOM_REGEX = "RANDOM\\((NUMBER|CAPS|CHARS|ALL),([0-9]+)\\)".toRegex()
        private val TIME_REGEX = "([-+])([0-9]*)([smh])".toRegex()
        private val NOW_REGEX = "now\\(\\)".toRegex()

        private val PATH_REGEX = "PATH\\(([0-9]*),([[a-z.][A-Z][0-9]]+)\\)".toRegex()
        private val SELF_REF_REGEX = "SELF_REF\\(([[a-z.][A-Z][0-9]]+)\\)".toRegex()

        private val IBAN_REGEX = "IBAN\\(([A-Z]{2}+),([A-Z]{4}+),([0-9]+)\\)".toRegex()
        private val IBAN_REF_REGEX = "IBAN_REF\\(([A-Z]{2}+),([A-Z]{4}+),([[a-z.][A-Z][0-9]]+)\\)".toRegex()

        private val TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
    }
}

private enum class RandomDataType(val generator: Int.() -> String) {
    NUMBER({
        RandomStringUtils.random(
            this,
            false,
            true
        )
    }),
    CHARS({
        RandomStringUtils.random(
            this,
            true,
            false
        )
    }),
    CAPS({
        RandomStringUtils.random(
            this,
            true,
            false
        ).uppercase()
    }),
    ALL({
        RandomStringUtils.random(
            this,
            true,
            true
        )
    });
}

private enum class DateAmountType(val shortcut: String, val unit: ChronoUnit) {
    SECOND("s", ChronoUnit.SECONDS),
    MINUTE("m", ChronoUnit.MINUTES),
    HOUR("h", ChronoUnit.HOURS);

    companion object {
        fun localDateTime(isPlus: Boolean, shortcut: String, amount: Int): LocalDateTime {
            val unit = values().firstOrNull {
                it.shortcut.equals(shortcut, true)
            }?.unit ?: ChronoUnit.SECONDS
            val localDateTime = LocalDateTime.now()
            if (isPlus)
                return localDateTime.plus(amount.toLong(), unit)
            return localDateTime.minus(amount.toLong(), unit)
        }
    }
}