package com.yazantarifi.android.reporter.adapter.network

data class NetworkItem(
    var requestUrl: String? = "",
    var requestHeaders: String? = "",
    var responseHeaders: String? = "",
    var responseCode: String? = "",
    var responseBody: String? = "",
    var requestCacheControl: String? = "",
    var requestMethod: String? = "",
    var responseTime: String? = ""
) {

    fun resetValues() {
        requestUrl = ""
        requestHeaders = ""
        responseHeaders = ""
        responseCode = ""
        responseBody = ""
        requestCacheControl = ""
        requestMethod = ""
        responseTime = ""
    }

}
