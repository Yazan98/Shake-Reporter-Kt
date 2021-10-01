package com.yazantarifi.android.reporter.adapter.network

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yazantarifi.android.reporter.R
import com.yazantarifi.android.reporter.adapter.NetworkCallsViewHolder
import com.yazantarifi.android.reporter.adapter.listeners.NetworkItemClickListener

class NetworkItemsAdapter constructor(
    private val items: ArrayList<NetworkItem>,
    private val listener: NetworkItemClickListener? = null
): RecyclerView.Adapter<NetworkCallsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkCallsViewHolder {
        return NetworkCallsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_network_item, parent, false))
    }

    override fun onBindViewHolder(holder: NetworkCallsViewHolder, position: Int) {
        val currentItem = items[position]
        holder.requestUrl?.text = currentItem.requestUrl
        holder.requestHeaders?.text = currentItem.requestHeaders
        holder.requestMethod?.text = currentItem.requestMethod
        holder.time?.text = currentItem.responseTime
        holder.responseHeaders?.text = currentItem.responseHeaders
        holder.responseBody?.text = currentItem.responseBody
        holder.viewContainer?.setOnClickListener {
            listener?.onNetworkItemClicked(currentItem)
        }

        if (currentItem.requestHeaders.isNullOrEmpty()) {
            holder.responseHeadersPlaceHolders?.visibility = View.GONE
            holder.requestHeaders?.visibility = View.GONE
        }

        if (currentItem.responseHeaders.isNullOrEmpty()) {
            holder.responseHeadersPlaceHolder?.visibility = View.GONE
            holder.responseHeaders?.visibility = View.GONE
        }

        if (currentItem.responseBody.isNullOrEmpty()) {
            holder.responseBody?.visibility = View.GONE
            holder.responseBodyHolder?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
