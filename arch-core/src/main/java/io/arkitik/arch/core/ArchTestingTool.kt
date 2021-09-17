package io.arkitik.arch.core

import io.arkitik.arch.core.dto.TestCase
import io.arkitik.arch.core.dto.TestClassData
import io.arkitik.arch.core.function.DataParser
import io.arkitik.arch.core.http.HttpTestProcessor
import io.arkitik.arch.core.parser.JacksonDataParser
import io.arkitik.arch.core.processor.Processor
import io.arkitik.arch.core.regex.RegexReplacer
import io.arkitik.arch.core.replacer.DefaultRegexReplacer
import org.junit.jupiter.api.DynamicTest
import java.io.InputStream
import java.nio.file.InvalidPathException
import kotlin.reflect.KClass

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
class ArchTestingTool(
    private val parser: DataParser,
    private val regexReplacer: RegexReplacer,
    private val processor: Processor,
) {
    fun <RS : Any> run(
        resourceFilePath: String,
        classLoader: ClassLoader = javaClass.classLoader,
        clazz: KClass<RS>,
        successCall: (TestCase, RS?) -> Unit,
        failCall: (TestCase, Throwable) -> Unit,
    ): List<DynamicTest> {
        val jsonInputStream = classLoader.getResourceAsStream(resourceFilePath)
        return if (jsonInputStream != null)
            run(jsonInputStream, clazz, successCall, failCall)
        else {
            throw InvalidPathException(resourceFilePath, "Could not open resources, file not found")
        }
    }

    fun <RS : Any> run(
        jsonInputStream: InputStream,
        clazz: KClass<RS>,
        successCall: (TestCase, RS?) -> Unit,
        failCall: (TestCase, Throwable) -> Unit,
    ): List<DynamicTest> {
        val testClassData = parser.run {
            jsonInputStream.parse(TestClassData::class)
        }
        return run(testClassData, clazz, successCall, failCall)
    }

    fun <RS : Any> run(
        testClassData: TestClassData,
        clazz: KClass<RS>,
        successCall: (TestCase, RS?) -> Unit,
        failCall: (TestCase, Throwable) -> Unit,
    ): List<DynamicTest> {
        return regexReplacer.run {
            parser.run {
                testClassData.write().replace()
                    .scenarios
                    .map { scenario ->
                        DynamicTest.dynamicTest(scenario.name) {
                            scenario.cases.forEach {
                                processor.process(it, clazz, successCall, failCall)
                            }
                        }
                    }
            }
        }
    }

    companion object {
        fun create(
            parser: DataParser,
            rootUrl: String,
            testCaseSignatureCreator: TestCase.() -> String,
        ) = create(
            parser,
            HttpTestProcessor(
                rootUrl,
                parser,
                testCaseSignatureCreator
            )
        )

        fun create(
            processor: Processor,
        ) = create(
            processor,
            JacksonDataParser()
        )

        fun create(
            processor: Processor,
            parser: JacksonDataParser,
        ) = create(
            parser,
            processor
        )

        fun create(
            parser: DataParser,
            processor: Processor,
        ) = create(
            parser,
            DefaultRegexReplacer(parser),
            processor
        )

        fun create(
            parser: DataParser,
            regexReplacer: RegexReplacer,
            processor: Processor,
        ) = ArchTestingTool(
            parser,
            regexReplacer,
            processor
        )
    }
}