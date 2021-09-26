package com.yazantarifi.android.reporter.screens

import android.R.attr
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yazantarifi.android.reporter.R
import com.yazantarifi.android.reporter.ShakeReporter
import com.yazantarifi.android.reporter.adapter.crashes.CrashItem
import com.yazantarifi.android.reporter.adapter.crashes.CrashesAdapter
import com.yazantarifi.android.reporter.adapter.listeners.CrashClickListener
import kotlinx.android.synthetic.main.fragment_crashes.*
import java.io.File
import java.io.BufferedReader
import java.io.FileReader
import java.lang.Exception
import android.R.attr.label

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

import androidx.core.content.ContextCompat.getSystemService




class CrashesFragment: Fragment(R.layout.fragment_crashes) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCrashesRecyclerView()
    }

    private fun setupCrashesRecyclerView() {
        try {
            crashesRecyclerView?.apply {
                this.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                this.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                this.adapter = CrashesAdapter(getItems(), object: CrashClickListener {
                    override fun onCrashItemClick(item: CrashItem) {
                        onCrashItemClicked(item)
                    }
                })
            }
        } catch (ex: Exception) {
            ShakeReporter.printLogs(ex.message ?: "", true)
        }
    }

    private fun onCrashItemClicked(item: CrashItem) {
        val clipboard: ClipboardManager? = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("Crash Report", """
            Crash Message : ${item.message}
            Crash StackTrace : ${item.stackTrace}
        """.trimIndent())
        clipboard?.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "Crash Info in Clipboard Now", Toast.LENGTH_SHORT).show()
    }

    private fun getItems(): ArrayList<CrashItem> {
        val items = ArrayList<CrashItem>()
        val filesDirectory = requireContext().filesDir
        val currentFiles = filesDirectory.listFiles()
        if (currentFiles.isNullOrEmpty()) {
            return items
        }

        val currentCrashesFiles = currentFiles[0].listFiles()
        for (item in currentCrashesFiles) {
            try {
                if (item.absolutePath.contains(ShakeReporter.CRASH_FILE_PATH)) {
                    items.add(getCrashItemFromFile(item))
                }
            } catch (ex: Exception) {
                ShakeReporter.printLogs(ex.message ?: "", true)
            }
        }
        return items
    }

    private fun getCrashItemFromFile(file: File): CrashItem {
        val br = BufferedReader(FileReader(file))
        var timestamp = ""
        var threadName = ""
        var stackTrace = ""
        var message = ""
        var localizedMessage = ""
        var stepNumber = 0
        br.use { br ->
            var line: String?
            while (br.readLine().also { line = it } != null) {
                val targetLine = line ?: ""
                if (!targetLine.contains("-----")) {
                    when (stepNumber) {
                        0 -> threadName = targetLine.trim()
                        1 -> timestamp = targetLine.trim()
                        2 -> message = targetLine.trim()
                        3 -> localizedMessage = targetLine.trim()
                    }
                }

                if (targetLine.contains("-----")) {
                    stepNumber += 1
                }

                if (!targetLine.contains("-----") && stepNumber > 3) {
                    stackTrace += targetLine.trim() + "\n"
                }
            }
        }

        return CrashItem(message, stackTrace, localizedMessage, timestamp, threadName)
    }

}