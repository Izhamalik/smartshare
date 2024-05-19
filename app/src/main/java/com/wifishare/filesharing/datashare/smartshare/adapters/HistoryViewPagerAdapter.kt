package com.wifishare.filesharing.datashare.smartshare.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentFive
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentFour
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentOne
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentThree
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentTwo
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.historyfragments.ApkHistoryFragment
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.historyfragments.AudiosHistoryFragment
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.historyfragments.DocsHistoryFragment
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.historyfragments.ImagesHistoryFragment
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.historyfragments.VideosHistoryFragment

class HistoryViewPagerAdapter(fragmentActivity: FragmentActivity, var returnitem : Int) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return returnitem // Number of fragments
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ImagesHistoryFragment()
            1 -> VideosHistoryFragment()
            2 -> AudiosHistoryFragment()
            3 -> DocsHistoryFragment()
            4 -> ApkHistoryFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}