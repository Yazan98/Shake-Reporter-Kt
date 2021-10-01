package com.yazantarifi.android.reporter.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_network_item.view.*

class NetworkCallsViewHolder constructor(view: View): RecyclerView.ViewHolder(view) {
    val requestUrl: TextView? = view.textView6
    val requestMethod: TextView? = view.textView8
    val requestHeaders: TextView? = view.headers
    val time: TextView? = view.time
    val responseHeaders: TextView? = view.responseHeaders
    val responseBody: TextView? = view.responseBody
    val viewContainer: View? = view.viewContainer
}
