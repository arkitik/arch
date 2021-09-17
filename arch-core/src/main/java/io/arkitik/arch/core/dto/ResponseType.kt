package io.arkitik.arch.core.dto

import org.springframework.web.reactive.function.client.WebClientResponseException
import kotlin.reflect.KClass

/**
 * Created By [*Ibrahim Al-Tamimi *](https://www.linkedin.com/in/iloom/)
 * Created At 18 1:20 AM, **Sat, September 2021**
 * Project *arch* [https://arkitik.io]
 */
enum class ResponseType(val responseException: KClass<out WebClientResponseException>?) {
    SUCCESS(null),
    BAD_REQUEST(WebClientResponseException.BadRequest::class),
    NOT_FOUND(WebClientResponseException.NotFound::class),
    UN_AUTHORIZED(WebClientResponseException.Unauthorized::class),
    NOT_ACCEPTABLE(WebClientResponseException.NotAcceptable::class)

}