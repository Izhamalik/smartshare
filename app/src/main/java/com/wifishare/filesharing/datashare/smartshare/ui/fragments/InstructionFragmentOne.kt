package com.wifishare.filesharing.datashare.smartshare.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentInstructionOneBinding

class InstructionFragmentOne : Fragment() {

    private var binding : FragmentInstructionOneBinding? = null

    companion object {
        var imagetype = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstructionOneBinding.inflate(layoutInflater , container , false)

        return binding?.root
    }

}