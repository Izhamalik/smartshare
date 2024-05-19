package com.wifishare.filesharing.datashare.smartshare.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import com.google.android.material.card.MaterialCardView
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentSettingsBinding
import com.wifishare.filesharing.datashare.smartshare.ui.activities.FeedbackActivity
import com.wifishare.filesharing.datashare.smartshare.util.clickWithThrottle

class SettingsFragment : Fragment() {

    private var binding: FragmentSettingsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        binding?.feedbackBtn?.clickWithThrottle {
            startActivity(Intent(requireActivity(), FeedbackActivity::class.java))
        }

        binding?.customerBtn?.clickWithThrottle {
            startActivity(Intent(requireActivity(), FeedbackActivity::class.java))
        }

        binding?.shareappBtn?.clickWithThrottle() {
            try {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Install Battery Charging Animation now!: https://play.google.com/store/apps/details?id=${requireActivity().packageName}"
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding?.privacypolicyBtn?.clickWithThrottle {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW)
                    browserIntent.data =
                        Uri.parse("https://sites.google.com/view/technowise-innovations/home")
                    startActivity(browserIntent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

        binding?.moreappsBtn?.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/dev?id=7248614859773344824")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/dev?id=7248614859773344824")
                    )
                )
            }
        }


        return binding?.root
    }

}