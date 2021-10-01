package com.yazantarifi.android.reporter.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yazantarifi.android.reporter.R
import com.yazantarifi.android.reporter.ReporterInterceptor
import com.yazantarifi.android.reporter.ShakeReporter
import com.yazantarifi.android.reporter.adapter.listeners.NetworkItemClickListener
import com.yazantarifi.android.reporter.adapter.network.NetworkItem
import com.yazantarifi.android.reporter.adapter.network.NetworkItemsAdapter
import kotlinx.android.synthetic.main.fragment_network_calls.*
import java.io.BufferedReader
import java.io.FileReader

class NetworkCallsFragment: Fragment(R.layout.fragment_network_calls) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNetworkCallsRecyclerView()
    }

    private fun setupNetworkCallsRecyclerView() {
        networkCallsRecyclerView?.apply {
            val networkItems = getItems()
            if (networkItems.isNullOrEmpty()) {
                noResults?.visibility = View.VISIBLE
                this.visibility = View.GONE
                return
            } else {
                noResults?.visibility = View.GONE
                this.visibility = View.VISIBLE
            }

            this.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            this.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            this.adapter = NetworkItemsAdapter(networkItems, object: NetworkItemClickListener {
                override fun onNetworkItemClicked(item: NetworkItem) {
                    onNetworkItemClickedListener(item)
                }
            })
        }
    }

    private fun onNetworkItemClickedListener(item: NetworkItem) {

    }

    private fun getItems(): ArrayList<NetworkItem> {
        val responseItems = ArrayList<NetworkItem>()
        ShakeReporter.networkRequestsFile?.let {
            val br = BufferedReader(FileReader(it))
            br.use { br ->
                var line: String?
                var stepIndex = 0
                var currentItem = NetworkItem()
                while (br.readLine().also { line = it } != null) {
                    val targetLine = line ?: ""
                    if (targetLine.contains(ReporterInterceptor.SECTION_KEY)) {
                        stepIndex += 1
                    }

                    if (!targetLine.contains(ReporterInterceptor.SECTION_KEY)) {
                        when (stepIndex) {
                            ReporterInterceptor.URL_INDEX -> currentItem.requestUrl = targetLine
                            ReporterInterceptor.CACHE_CONTROL -> currentItem.requestCacheControl = targetLine
                            ReporterInterceptor.HEADERS_INDEX -> currentItem.requestHeaders = targetLine
                            ReporterInterceptor.METHOD_INDEX -> currentItem.requestMethod = targetLine
                            ReporterInterceptor.REQUEST_TIME -> currentItem.responseTime = targetLine
                            ReporterInterceptor.RESPONSE_CODE -> currentItem.responseCode = targetLine
                            ReporterInterceptor.RESPONSE_HEADERS -> currentItem.responseHeaders += targetLine + "\n"
                            ReporterInterceptor.RESPONSE_BODY -> currentItem.responseBody += targetLine
                        }
                    }

                    if (targetLine.contains(ReporterInterceptor.END_FILE_SECTION)) {
                        stepIndex = 0
                        responseItems.add(currentItem)
                        currentItem = NetworkItem()
                    }
                }
            }
        }
        return responseItems
    }

}
