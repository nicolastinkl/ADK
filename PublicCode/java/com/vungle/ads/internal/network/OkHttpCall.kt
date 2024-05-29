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

import android.util.Log
import com.vungle.ads.internal.network.Response.Companion.error
import com.vungle.ads.internal.network.Response.Companion.success
import com.vungle.ads.internal.network.converters.Converter
import okhttp3.MediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.asResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import java.io.IOException
import java.util.*

internal class OkHttpCall<T>(
    private val rawCall: okhttp3.Call,
    private val responseConverter: Converter<ResponseBody?, T?>
) : Call<T> {
    @Volatile
    private var canceled = false
    override fun enqueue(callback: Callback<T>) {
        Objects.requireNonNull(callback, "callback == null")
        var call: okhttp3.Call
        synchronized(this) { call = rawCall }
        if (canceled) {
            call.cancel()
        }
        call.enqueue(
            object : okhttp3.Callback {
                override fun onResponse(
                    call: okhttp3.Call,
                    response: okhttp3.Response
                ) {
                    val resp: Response<T>? = try {
                        parseResponse(response)
                    } catch (e: Throwable) {
                        throwIfFatal(e)
                        callFailure(e)
                        return
                    }
                    try {
                        callback.onResponse(this@OkHttpCall, resp)
                    } catch (t: Throwable) {
                        throwIfFatal(t)
                        t.printStackTrace()
                    }
                }

                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    callFailure(e)
                }

                private fun callFailure(e: Throwable) {
                    try {
                        callback.onFailure(this@OkHttpCall, e)
                    } catch (t: Throwable) {
                        throwIfFatal(t)
                        t.printStackTrace()
                    }
                }
            })
    }

    @Throws(IOException::class)
    override fun execute(): Response<T>? {
        var call: okhttp3.Call
        synchronized(this) { call = rawCall }
        if (canceled) {
            call.cancel()
        }
        return parseResponse(call.execute())
    }

    @Throws(IOException::class)
    fun parseResponse(rawResp: okhttp3.Response): Response<T>? {
        val rawBody = rawResp.body ?: return null

        // Remove the body's source (the only stateful object) so we can pass the response along.
        val rawResponse = rawResp
            .newBuilder()
            .body(NoContentResponseBody(rawBody.contentType(), rawBody.contentLength()))
            .build()

        val code = rawResponse.code
        if (code < 200 || code >= 300) {
            return rawBody.use {
                // Buffer the entire body to avoid future I/O.
                val bufferedBody = buffer(it)
                error(bufferedBody, rawResponse)
            }
        }
        if (code == 204 || code == 205) {
            rawBody.close()
            return success(null, rawResponse)
        }



        val catchingBody = ExceptionCatchingResponseBody(rawBody)
        return try {
            val body = responseConverter.convert(catchingBody)
            //Log.d("body",catchingBody.toString()+"");
            success(body, rawResponse)

        } catch (e: RuntimeException) {
            // If the underlying source threw an exception, propagate that rather than indicating it was
            // a runtime exception.
            catchingBody.throwIfCaught()
            throw e
        }
    }

    @Throws(IOException::class)
    private fun buffer(body: ResponseBody): ResponseBody {
        val buffer = Buffer()
        body.source().readAll(buffer)
        return buffer.asResponseBody(body.contentType(), body.contentLength())
    }

    override fun cancel() {
        canceled = true
        var call: okhttp3.Call
        synchronized(this) { call = rawCall }
        call.cancel()
    }

    override fun isCanceled(): Boolean {
        if (canceled) {
            return true
        }
        synchronized(this) { return rawCall.isCanceled() }
    }

    internal class NoContentResponseBody(
        private val contentType: MediaType?,
        private val contentLength: Long
    ) : ResponseBody() {
        override fun contentType(): MediaType? {
            return contentType
        }

        override fun contentLength(): Long {
            return contentLength
        }

        override fun source(): BufferedSource {
            throw IllegalStateException("Cannot read raw response body of a converted body.")
        }
    }

    internal class ExceptionCatchingResponseBody(private val delegate: ResponseBody) :
        ResponseBody() {
        private val delegateSource: BufferedSource
        var thrownException: IOException? = null
        override fun contentType(): MediaType? {
            return delegate.contentType()
        }

        override fun contentLength(): Long {
            return delegate.contentLength()
        }

        override fun source(): BufferedSource {
            return delegateSource
        }

        override fun close() {
            delegate.close()
        }

        @Throws(IOException::class)
        fun throwIfCaught() {
            thrownException?.let {
                throw it
            }
        }

        init {
            delegateSource = object : ForwardingSource(delegate.source()) {
                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    return try {
                        super.read(sink, byteCount)
                    } catch (e: IOException) {
                        thrownException = e
                        throw e
                    }
                }
            }.buffer()
        }
    }

    companion object {
        private fun throwIfFatal(t: Throwable) {
            when (t) {
                is VirtualMachineError -> {
                    throw t
                }
                is ThreadDeath -> {
                    throw t
                }
                is LinkageError -> {
                    throw t
                }
            }
        }
    }
}
