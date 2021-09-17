package io.arkitik.arch.core.processor

import io.arkitik.arch.core.dto.TestCase
import kotlin.reflect.KClass

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
interface Processor {
    fun <RS : Any> process(
        testCase: TestCase,
        clazz: KClass<RS>,
        successCall: (TestCase, RS?) -> Unit,
        failCall: (TestCase, Throwable) -> Unit,
    )
}