package com.wifishare.filesharing.datashare.smartshare.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentFive
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentFour
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentOne
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentThree
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentTwo
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.filesselectionfragments.FileSelectionFragmentApk
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.filesselectionfragments.FileSelectionFragmentDocs
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.filesselectionfragments.FileSelectionFragmentImages
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.filesselectionfragments.FileSelectionFragmentMusic
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.filesselectionfragments.FileSelectionFragmentVideos

class FilesSelectionPagerAdapter(fragmentActivity: FragmentActivity, var returnitem : Int) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return returnitem // Number of fragments
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FileSelectionFragmentImages()
            1 -> FileSelectionFragmentVideos()
            2 -> FileSelectionFragmentMusic()
            3 -> FileSelectionFragmentDocs()
            4 -> FileSelectionFragmentApk()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}