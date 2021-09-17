package io.arkitik.arch.core.function

import java.io.InputStream
import kotlin.reflect.KClass

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
interface DataParser {
    fun <T : Any> String.parse(clazz: KClass<T>): T
    fun <T : Any> InputStream.parse(clazz: KClass<T>): T
    fun <T : Any> T.write(): String
}