package com.yazantarifi.android.reporter.screens

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.yazantarifi.android.reporter.R
import com.yazantarifi.android.reporter.ShakeReporter
import com.yazantarifi.android.reporter.ShakeReporterPagerAdapter
import com.yazantarifi.android.reporter.adapter.network.NetworkItem
import kotlinx.android.synthetic.main.screen_shake_reporter.*
import java.io.*
import java.lang.Exception
import java.nio.file.Files.exists


class ShakeReporterScreen: AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1332

        @JvmStatic
        fun startScreen(context: Context) {
            Intent(context, ShakeReporterScreen::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                this.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
                context.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_shake_reporter)
        startScreen()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        startScreen()
    }

    private fun startScreen() {
        setupToolbar()
        setupViewPagerContent()
        setupTabLayoutContent()
    }

    private fun setupToolbar() {
        screenToolbar?.let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupTabLayoutContent() {
        tabLayout?.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    containerPager?.currentItem = it
                }
            }
        })
    }

    private fun setupViewPagerContent() {
        containerPager?.apply {
            this.adapter = ShakeReporterPagerAdapter(this@ShakeReporterScreen)
            this.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout?.setScrollPosition(position, 0f, true)
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.reporter_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                super.onOptionsItemSelected(item)
            }
            else -> exportReportsToExternalStorage()
        }
    }

    private fun exportReportsToExternalStorage(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
           showExportReportConfirmationDialog()
            return true
        }

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        return true
    }

    private fun showExportReportConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.export_title))
            .setMessage(getString(R.string.export_message))
            .setPositiveButton(android.R.string.ok) { p0, p1 ->
                p0?.dismiss()
                exportReportFiles()
            }
            .setNegativeButton(android.R.string.cancel) { p0, p1 ->
                p0?.dismiss()
            }
            .show()
    }

    private fun exportReportFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            externalMediaDirs[0]
        } else {
            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        }?.let {
            if (it.isDirectory && !it.exists()) {
                it.mkdir()
            }

            createNetworkCallsReportFile(it)
            Toast.makeText(
                this@ShakeReporterScreen,
                getString(R.string.report_created),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createNetworkCallsReportFile(rootFile: File) {
        val tokensArray = ArrayList<String>()
        ShakeReporter.networkRequestsFile?.let {
            val br = BufferedReader(FileReader(it))
            br.use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    val targetLine = line ?: ""
                    tokensArray.add(targetLine)
                }
            }

            try {
                val fullPath = rootFile.absolutePath + File.separator + ShakeReporter.NETWORK_REQUESTS_FILE_NAME + ".txt"
                val writer = BufferedWriter(FileWriter(fullPath))
                tokensArray.forEach {
                    writer.append(it + "\n")
                }
                writer.flush()
                writer.close()
            } catch (ex: Exception) {
                ShakeReporter.printLogs(ex.message ?: "", true)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   showExportReportConfirmationDialog()
                }
            }
        }
    }

}
