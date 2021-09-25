package com.yazantarifi.android.reporter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yazantarifi.android.reporter.screens.CrashesFragment
import com.yazantarifi.android.reporter.screens.NetworkCallsFragment

class ShakeReporterPagerAdapter constructor(screen: FragmentActivity): FragmentStateAdapter(screen) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CrashesFragment()
            1 -> NetworkCallsFragment()
            else -> CrashesFragment()
        }
    }

}
