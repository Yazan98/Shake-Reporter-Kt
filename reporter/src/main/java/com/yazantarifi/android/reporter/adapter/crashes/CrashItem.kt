package com.yazantarifi.android.reporter.adapter.crashes

data class CrashItem(
    var message: String? = "",
    var stackTrace: String? = "",
    var localizedMessage: String? = "",
    var timestampString: String? = "",
    var threadName: String? = ""
)
