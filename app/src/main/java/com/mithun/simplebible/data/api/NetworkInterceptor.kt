package com.mithun.simplebible.data.api

import com.mithun.simplebible.utilities.API_KEY
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            chain.request()
                .newBuilder()
                .addHeader("api-key", API_KEY)
                .addHeader("accept", "application/json")
                .build()
        )
    }
}
