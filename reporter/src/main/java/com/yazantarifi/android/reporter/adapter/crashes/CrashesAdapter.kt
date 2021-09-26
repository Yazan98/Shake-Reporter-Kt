package com.yazantarifi.android.reporter.adapter.crashes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yazantarifi.android.reporter.R
import com.yazantarifi.android.reporter.adapter.CrashesViewHolder
import com.yazantarifi.android.reporter.adapter.listeners.CrashClickListener

class CrashesAdapter constructor(
    private val items: ArrayList<CrashItem>,
    private val crashesClickListener: CrashClickListener? = null
): RecyclerView.Adapter<CrashesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrashesViewHolder {
        return CrashesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.crashes_row, null, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CrashesViewHolder, position: Int) {
        val currentItem = items[position]
        holder.message?.text = "${currentItem.message?.trim()}"
        holder.localizedMessage?.text = "${currentItem.localizedMessage?.trim()}"
        holder.stackTrace?.text = "${currentItem.stackTrace?.trim()}"
        holder.info?.text = "${currentItem.threadName?.trim()} / ${currentItem.timestampString?.trim()}"
        holder.item?.setOnClickListener {
            crashesClickListener?.onCrashItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
