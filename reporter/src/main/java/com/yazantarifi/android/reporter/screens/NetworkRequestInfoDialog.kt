package com.yazantarifi.android.reporter.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yazantarifi.android.reporter.R
import com.yazantarifi.android.reporter.adapter.network.NetworkItem
import kotlinx.android.synthetic.main.dialog_network_call.*

class NetworkRequestInfoDialog: BottomSheetDialogFragment() {

    companion object {
        private const val NETWORK_INFO = "args.network.info"
        private const val DIALOG_TAG = "NetworkRequestInfoDialog"

        @JvmStatic
        fun showDialog(context: FragmentActivity, networkItem: NetworkItem) {
            NetworkRequestInfoDialog().apply {
                this.arguments = Bundle().apply {
                    this.putParcelable(NETWORK_INFO, networkItem)
                }

                this.show(context.supportFragmentManager, DIALOG_TAG)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_network_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            it.getParcelable<NetworkItem>(NETWORK_INFO)?.also {
                initNetworkInfo(it)
            }
        }
    }

    private fun initNetworkInfo(networkItem: NetworkItem) {
        requestUrl?.text = networkItem.requestUrl
        requestMethod?.text = networkItem.requestMethod
        requestTime?.text = networkItem.responseTime
        requestHeaders?.text = networkItem.requestHeaders
        responseHeaders?.text = networkItem.responseHeaders
        responseBody?.text = networkItem.responseBody
    }

}
