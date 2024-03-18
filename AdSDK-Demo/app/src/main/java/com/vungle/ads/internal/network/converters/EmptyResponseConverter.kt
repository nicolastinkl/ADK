package com.vungle.ads.internal.network.converters

import okhttp3.ResponseBody

class EmptyResponseConverter : Converter<ResponseBody?, Void?> {

    override fun convert(responseBody: ResponseBody?): Void? {
        responseBody?.close()
        return null
    }
}
