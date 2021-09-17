package io.arkitik.arch.core.dto

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
data class TestScenario(
    val name: String,
    val cases: List<TestCase>,
)