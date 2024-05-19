package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityHistoryBinding
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.model.HistoryPageViewModel
import com.wifishare.filesharing.datashare.smartshare.util.clickWithThrottle

class HistoryActivity : AppCompatActivity() {

    private var binding : ActivityHistoryBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.backpressBtn?.setOnClickListener { onBackPressed() }




        binding?.imagesBtn?.clickWithThrottle {
            HistoryDetailsActivity.selectionPoint = 0
            startActivity(Intent(this , HistoryDetailsActivity :: class.java))
        }

        binding?.videoBtn?.clickWithThrottle {
            HistoryDetailsActivity.selectionPoint = 1
            startActivity(Intent(this , HistoryDetailsActivity :: class.java))
        }

        binding?.audiosBtn?.clickWithThrottle {
            HistoryDetailsActivity.selectionPoint = 2
            startActivity(Intent(this , HistoryDetailsActivity :: class.java))
        }

        binding?.docsBtn?.clickWithThrottle {
            HistoryDetailsActivity.selectionPoint = 3
            startActivity(Intent(this , HistoryDetailsActivity :: class.java))
        }

        binding?.appsBtn?.clickWithThrottle {
            HistoryDetailsActivity.selectionPoint = 4
            startActivity(Intent(this , HistoryDetailsActivity :: class.java))
        }

    }
}