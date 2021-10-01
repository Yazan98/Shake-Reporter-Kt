package com.yazantarifi.android.reporter.adapter.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NetworkItem(
    var requestUrl: String? = "",
    var requestHeaders: String? = "",
    var responseHeaders: String? = "",
    var responseCode: String? = "",
    var responseBody: String? = "",
    var requestMethod: String? = "",
    var responseTime: String? = ""
): Parcelable

