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

import java.io.IOException

interface Call<T> {
    /**
     * Synchronously send the request and return its response.
     *
     * @throws IOException if a problem occurred talking to the server.
     * @throws RuntimeException (and subclasses) if an unexpected error occurs creating the request or
     * decoding the response.
     */
    @Throws(IOException::class)
    fun execute(): Response<T>?

    /**
     * Asynchronously send the request and notify `callback` of its response or if an error
     * occurred talking to the server, creating the request, or processing the response.
     */
    fun enqueue(callback: Callback<T>)

    /**
     * Cancel this call. An attempt will be made to cancel in-flight calls, and if the call has not
     * yet been executed it never will be.
     */
    fun cancel()

    /** True if [.cancel] was called.  */
    fun isCanceled(): Boolean

}
