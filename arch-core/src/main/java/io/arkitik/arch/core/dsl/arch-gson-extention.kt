package io.arkitik.arch.core.dsl

import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
inline infix fun <reified T> Gson.parse(json: String): T {
    return fromJson(json, T::class.java)
}


inline infix fun <reified T> Gson.parse(json: JsonObject): T {
    return fromJson(json, T::class.java)
}