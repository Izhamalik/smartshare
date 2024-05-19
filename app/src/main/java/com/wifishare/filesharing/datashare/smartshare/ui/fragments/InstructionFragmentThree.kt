package com.wifishare.filesharing.datashare.smartshare.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentInstructionThreeBinding

class InstructionFragmentThree : Fragment() {

    private var binding : FragmentInstructionThreeBinding? = null

    companion object {
        var imagetype = ""
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstructionThreeBinding.inflate(layoutInflater , container , false)

        when(imagetype){
            "group" -> {
                binding?.imageholder?.setImageDrawable(ContextCompat.getDrawable(requireContext() , R.drawable.group_ill))
            }
            "directwifi" -> {
                binding?.imageholder?.setImageDrawable(ContextCompat.getDrawable(requireContext() , R.drawable.wifidirect_ill))
            }
            "receive" -> {
                binding?.imageholder?.setImageDrawable(ContextCompat.getDrawable(requireContext() , R.drawable.receive_ill))
            }

            "qr" -> {
                binding?.imageholder?.setImageDrawable(ContextCompat.getDrawable(requireContext() , R.drawable.qr_ill))
            }

            "hotspot" -> {
                binding?.imageholder?.setImageDrawable(ContextCompat.getDrawable(requireContext() , R.drawable.hotspot_ill))
            }

            "send" -> {
                binding?.imageholder?.setImageDrawable(ContextCompat.getDrawable(requireContext() , R.drawable.send_ill))
            }

            "next" -> {
                binding?.imageholder?.setImageDrawable(ContextCompat.getDrawable(requireContext() , R.drawable.next_ill))
            }
            else -> {
                binding?.imageholder?.setImageDrawable(ContextCompat.getDrawable(requireContext() , R.drawable.receive_ill))
            }
        }

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
    }

}