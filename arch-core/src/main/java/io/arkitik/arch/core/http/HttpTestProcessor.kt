package io.arkitik.arch.core.http

import io.arkitik.arch.core.dto.RequestType
import io.arkitik.arch.core.dto.TestCase
import io.arkitik.arch.core.function.DataParser
import io.arkitik.arch.core.processor.Processor
import io.arkitik.ktx.radix.tool.http.HttpRequestProcessor
import io.arkitik.ktx.radix.tool.http.RequestData
import kotlin.reflect.KClass

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
class HttpTestProcessor(
    rootUrl: String,
    private val dataParser: DataParser,
    private val testCaseSignatureCreator: TestCase.() -> String,
) : Processor {
    private val httpRequestProcessor = HttpRequestProcessor.from(rootUrl)

    override fun <RS : Any> process(
        testCase: TestCase,
        clazz: KClass<RS>,
        successCall: (TestCase, RS?) -> Unit,
        failCall: (TestCase, Throwable) -> Unit,
    ) {
        val requestContent = dataParser.run {
            testCase.request.write()
        }
        val signature = testCaseSignatureCreator(testCase)
        val headers = arrayListOf<Pair<String, Any?>>()
        headers.add(Pair("Content-Type", "application/json"))
        if (testCase.autoHeader) {
            headers.add(Pair("Authorization", "Bearer $signature"))
        } else {
            headers.addAll(testCase.headers.map {
                Pair(it.key, it.value)
            })
        }
        callApi(testCase.requestType,
            RequestData(
                requestContent,
                testCase.mapping,
                headers
            ), clazz, {
                successCall(testCase, it)
            }) {
            failCall(testCase, it)
        }
    }

    private fun <T, RS : Any> callApi(
        requestType: RequestType,
        requestData: RequestData<T>,
        responseClass: KClass<RS>,
        successCall: (RS?) -> Unit,
        failCall: (Throwable) -> Unit,
    ) = when (requestType) {
        RequestType.POST -> {
            httpRequestProcessor
                .postBlocking(
                    requestData,
                    responseClass,
                    successCall,
                    failCall
                )
        }
        RequestType.PATCH -> {
            httpRequestProcessor
                .patchBlocking(
                    requestData,
                    responseClass,
                    successCall,
                    failCall
                )
        }
        RequestType.PUT -> {
            httpRequestProcessor
                .putBlocking(
                    requestData,
                    responseClass,
                    successCall,
                    failCall
                )
        }
    }
}