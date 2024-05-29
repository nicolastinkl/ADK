/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vungle.ads.internal.network

import okhttp3.Headers
import okhttp3.ResponseBody

class Response<T> private constructor(
    private val rawResponse: okhttp3.Response,
    private val body: T?,
    private val errorBody: ResponseBody?
) {
    /** The raw response from the HTTP client.  */
    fun raw(): okhttp3.Response {
        return rawResponse
    }

    /** HTTP status code.  */
    fun code(): Int {
        return rawResponse.code
    }

    /** HTTP status message or null if unknown.  */
    fun message(): String {
        return rawResponse.message
    }

    /** HTTP headers.  */
    fun headers(): Headers {
        return rawResponse.headers
    }

    /** Returns true if [.code] is in the range [200..300).  */
    val isSuccessful: Boolean
        get() = rawResponse.isSuccessful

    /** The deserialized response body of a [successful][.isSuccessful] response.  */
    fun body(): T? {
        return body
    }

    /** The raw response body of an [unsuccessful][.isSuccessful] response.  */
    fun errorBody(): ResponseBody? {
        return errorBody
    }

    override fun toString(): String {
        return rawResponse.toString()
    }

    companion object {

        /**
         * Create a successful response from `rawResponse` with `body` as the deserialized
         * body.
         */
        fun <T> success(body: T?, rawResponse: okhttp3.Response): Response<T> {
            require(rawResponse.isSuccessful) { "rawResponse must be successful response" }
            return Response(rawResponse, body, null)
        }

        /** Create an error response from `rawResponse` with `body` as the error body.  */
        fun <T> error(body: ResponseBody?, rawResponse: okhttp3.Response): Response<T> {
            require(!rawResponse.isSuccessful) { "rawResponse should not be successful response" }
            return Response(rawResponse, null, body)
        }
    }
}
