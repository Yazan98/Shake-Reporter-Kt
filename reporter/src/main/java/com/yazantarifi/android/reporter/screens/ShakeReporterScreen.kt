package com.yazantarifi.android.reporter.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.yazantarifi.android.reporter.R
import com.yazantarifi.android.reporter.ShakeReporterPagerAdapter
import kotlinx.android.synthetic.main.screen_shake_reporter.*

class ShakeReporterScreen: AppCompatActivity() {

    companion object {
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
        setupViewPagerContent()
        setupTabLayoutContent()
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

}
