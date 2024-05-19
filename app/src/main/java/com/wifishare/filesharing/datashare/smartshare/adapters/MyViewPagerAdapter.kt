package com.wifishare.filesharing.datashare.smartshare.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentFive
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentFour
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentOne
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentThree
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentTwo

class MyViewPagerAdapter(fragmentActivity: FragmentActivity , var returnitem : Int) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return returnitem // Number of fragments
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InstructionFragmentOne()
            1 -> InstructionFragmentTwo()
            2 -> InstructionFragmentThree()
            3 -> InstructionFragmentFour()
            4 -> InstructionFragmentFive()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}