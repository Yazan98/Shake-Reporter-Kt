package com.yazantarifi.android.reporter.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yazantarifi.android.reporter.R
import com.yazantarifi.android.reporter.ShakeReporter
import com.yazantarifi.android.reporter.adapter.crashes.CrashItem
import com.yazantarifi.android.reporter.adapter.crashes.CrashesAdapter
import com.yazantarifi.android.reporter.adapter.listeners.CrashClickListener
import kotlinx.android.synthetic.main.fragment_crashes.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class CrashesFragment : Fragment(R.layout.fragment_crashes) {

    private var stepNumber = 0
    companion object {
        private const val THREAD_NAME_POSITION = 0
        private const val TIMESTAMP_POSITION = 1
        private const val MESSAGE_POSITION = 2
        private const val LOCALIZED_MESSAGE_POSITION = 3
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCrashesRecyclerView()
    }

    private fun setupCrashesRecyclerView() {
        try {
            crashesRecyclerView?.apply {
                val crashes = getItems()
                if (crashes.isNullOrEmpty()) {
                    noResults?.visibility = View.VISIBLE
                    this.visibility = View.GONE
                    return
                } else {
                    noResults?.visibility = View.GONE
                    this.visibility = View.VISIBLE
                }

                this.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                this.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                this.adapter = CrashesAdapter(crashes, object : CrashClickListener {
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
        val clip = ClipData.newPlainText(
            "Crash Report", """
            Crash Message : ${item.message}
            Crash StackTrace : ${item.stackTrace}
        """.trimIndent()
        )
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
        br.use { br ->
            var line: String?
            while (br.readLine().also { line = it } != null) {
                val targetLine = line ?: ""
                if (!targetLine.contains(ShakeReporter.PATH_SPLITTER)) {
                    when (stepNumber) {
                        THREAD_NAME_POSITION -> threadName = targetLine.trim()
                        TIMESTAMP_POSITION -> timestamp = targetLine.trim()
                        MESSAGE_POSITION -> message = targetLine.trim()
                        LOCALIZED_MESSAGE_POSITION -> localizedMessage = targetLine.trim()
                    }
                }

                if (targetLine.contains(ShakeReporter.PATH_SPLITTER)) {
                    stepNumber += 1
                }

                if (!targetLine.contains(ShakeReporter.PATH_SPLITTER) && stepNumber > 3) {
                    stackTrace += targetLine.trim() + "\n"
                }
            }
        }

        return CrashItem(message, stackTrace, localizedMessage, timestamp, threadName)
    }

}