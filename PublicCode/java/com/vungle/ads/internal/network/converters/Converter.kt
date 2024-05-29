package com.vungle.ads.internal.network.converters

import java.io.IOException
import kotlin.Throws

fun interface Converter<In, Out> {
    @Throws(IOException::class)
    fun convert(responseBody: In): Out?
}
