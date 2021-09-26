package com.yazantarifi.android.reporter.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.crashes_row.view.*

class CrashesViewHolder constructor(view: View): RecyclerView.ViewHolder(view) {
    val message: TextView? = view.crashMessageField
    val localizedMessage: TextView? = view.localizedMessageField
    val info: TextView? = view.threadNameField
    val stackTrace: TextView? = view.stackTrace
    val item: View? = view.itemView
}
