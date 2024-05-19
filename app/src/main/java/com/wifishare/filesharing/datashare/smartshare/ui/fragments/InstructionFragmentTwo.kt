package com.wifishare.filesharing.datashare.smartshare.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentInstructionOneBinding
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentInstructionTwoBinding


class InstructionFragmentTwo : Fragment() {

    private var binding : FragmentInstructionTwoBinding? = null
    companion object {
        var imagetype = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {     binding = FragmentInstructionTwoBinding.inflate(layoutInflater , container , false)
        return binding?.root
    }
}