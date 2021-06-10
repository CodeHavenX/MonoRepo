package com.cramsan.ps2link.appcore.network

import com.cramsan.framework.assertlib.assert
import com.cramsan.framework.logging.logE
import io.ktor.client.statement.HttpResponse

class PS2HttpResponse<Body> private constructor(
    val body: Body?,
    val rawResponse: HttpResponse? = null,
    val throwable: Throwable? = null
) {
    val code = rawResponse?.status?.value ?: 200
    val isSuccessful = throwable == null && code in 200..299

    companion object {
        fun <Body> success(body: Body, rawResponse: HttpResponse? = null): PS2HttpResponse<Body> = PS2HttpResponse(body, rawResponse)

        fun <Body> failure(rawResponse: HttpResponse?, throwable: Throwable?): PS2HttpResponse<Body> {
            assert(rawResponse != null || throwable != null, "PS2HttpResponse", "A rawResponse or throwable is needed.")
            return PS2HttpResponse(null, rawResponse, throwable)
        }

        fun <Body> failure(rawResponse: HttpResponse?, throwableList: List<Throwable>): PS2HttpResponse<Body> {
            assert(throwableList.isNotEmpty(), "PS2HttpResponse", "throwableList cannot be empty.")
            return PS2HttpResponse(
                null, rawResponse,
                Exception(
                    "Multiple exceptions found. ${throwableList.size} exceptions in total. First exception attached.",
                    throwableList.first(),
                )
            )
        }

        fun <Orig, Result> process(response: PS2HttpResponse<Orig>, process: (Orig) -> Result): PS2HttpResponse<Result> {
            if (!response.isSuccessful) {
                return response.toFailure()
            }

            return try {
                response.toSuccess(process(response.requireBody()))
            } catch (throwable: Throwable) {
                logE("PS2HttpResponse", "Exception processing successful request.", throwable)
                response.toFailure(throwable)
            }
        }
    }
}

fun <Orig, Result> PS2HttpResponse<Orig>.toFailure() = toFailure<Orig, Result>(throwable)

fun <Orig, Result> PS2HttpResponse<Orig>.toFailure(throwable: Throwable?) = PS2HttpResponse.failure<Result>(rawResponse, throwable)

fun <Orig, Result> PS2HttpResponse<Orig>.toSuccess(body: Result) = PS2HttpResponse.success(body, rawResponse)

fun <Body> PS2HttpResponse<Body>.requireBody(): Body {
    assert(isSuccessful, "PS2HttpResponse", "Request needs to be successful")
    return requireNotNull(body)
}

fun <Orig, Unit> PS2HttpResponse<Orig>.onSuccess(process: (Orig) -> Unit): PS2HttpResponse<Orig> {
    return try {
        if (isSuccessful) {
            process(requireBody())
        }
        this
    } catch (throwable: Throwable) {
        PS2HttpResponse.failure(null, throwable)
    }
}

fun <Orig, Result> PS2HttpResponse<Orig>.process(process: (Orig) -> Result): PS2HttpResponse<Result> {
    return PS2HttpResponse.process(this, process)
}

fun <Orig, Result> List<PS2HttpResponse<Orig>>.processList(process: (Orig) -> Result): PS2HttpResponse<List<Result>> {
    return try {
        val failures = filter { !it.isSuccessful }.mapNotNull { it.throwable }
        when (failures.size) {
            0 -> PS2HttpResponse.success(map { process(it.requireBody()) })
            1 -> PS2HttpResponse.failure(null, failures.first())
            else -> PS2HttpResponse.failure(null, failures)
        }
    } catch (throwable: Throwable) {
        PS2HttpResponse.failure(null, throwable)
    }
}