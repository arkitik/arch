package io.arkitik.arch.core.dsl

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.arkitik.arch.core.ArchTestingTool
import io.arkitik.arch.core.dto.TestCase
import io.arkitik.arch.core.dto.TestClassData
import org.junit.jupiter.api.DynamicTest
import kotlin.reflect.KClass

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
val gson = Gson()

fun <RS : Any> ArchTestingTool.run(
    jsonObject: JsonObject,
    clazz: KClass<RS>,
    successCall: (TestCase, RS?) -> Unit,
    failCall: (TestCase, Throwable) -> Unit,
): List<DynamicTest> {
    return run(
        jsonObject.toTestData(),
        clazz,
        successCall,
        failCall
    )
}

fun JsonObject.toTestData(): TestClassData = gson parse this