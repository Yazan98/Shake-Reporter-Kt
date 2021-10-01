package com.yazantarifi.android.reporter

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.BufferedWriter
import java.io.FileWriter
import java.lang.Exception

open class ReporterInterceptor: Interceptor {

    companion object {
        const val URL_INDEX = 0
        const val HEADERS_INDEX = 1
        const val CACHE_CONTROL = 2
        const val METHOD_INDEX = 3
        const val REQUEST_TIME = 4
        const val RESPONSE_CODE = 5
        const val RESPONSE_HEADERS = 6
        const val RESPONSE_BODY = 7
        const val SECTION_KEY = "----------------------"
        const val END_FILE_SECTION = "End File Section ----------------"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        writeLogs(request, response)
        return response
    }

    protected fun writeLogs(request: Request?, response: Response?) {
        try {
            ShakeReporter.networkRequestsFile?.let {
                val requestTotalTime = (response?.receivedResponseAtMillis ?: 0) - (response?.sentRequestAtMillis ?: 0)
                val writer = BufferedWriter(FileWriter(it.absolutePath, true))
                writer.append("${request?.url?.toString()}\n")
                writer.append(SECTION_KEY + "\n")
                writer.append("${request?.headers?.toString()}\n")
                writer.append(SECTION_KEY + "\n")
                writer.append("${request?.cacheControl?.toString()}\n")
                writer.append(SECTION_KEY + "\n")
                writer.append("${request?.method}\n")
                writer.append(SECTION_KEY + "\n")
                writer.append("$requestTotalTime ms\n")
                writer.append(SECTION_KEY + "\n")
                writer.append("${response?.code} \n")
                writer.append(SECTION_KEY + "\n")
                writer.append("${response?.headers?.toString()} \n")
                writer.append(SECTION_KEY + "\n")
                writer.append("${response?.body?.string()} \n")
                writer.append(SECTION_KEY + "\n")
                writer.append(END_FILE_SECTION + "\n")
                writer.flush()
                writer.close()
                ShakeReporter.printLogs("Reporter Saved Network Log ...", false)
            }
        } catch (ex: Exception) {
            ShakeReporter.printLogs(ex?.message ?: "", true)
        }
    }

}
