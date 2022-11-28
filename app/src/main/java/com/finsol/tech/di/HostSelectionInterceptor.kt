package com.finsol.tech.di

import com.finsol.tech.FinsolApplication
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import okhttp3.Interceptor
import okhttp3.Response

class HostSelectionInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        val preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        var host: String = preferenceHelper.getString(
            FinsolApplication.context,
            AppConstants.KEY_PREF_IP_ADDRESS_VALUE, ""
        )
        if (host.isEmpty()) {
            host = preferenceHelper.getString(
                FinsolApplication.context,
                AppConstants.KEY_PREF_TEMP_IP_ADDRESS_VALUE, ""
            )
        }

        val newUrl = request.url.newBuilder()
            .host(host)
            .build()

        request = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }

}