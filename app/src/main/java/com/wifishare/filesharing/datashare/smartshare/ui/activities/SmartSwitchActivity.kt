package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivitySmartSwitchBinding
import com.wifishare.filesharing.datashare.smartshare.Controller

class SmartSwitchActivity : AppCompatActivity() {

    private var binding : ActivitySmartSwitchBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmartSwitchBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.receiveBtn?.setOnClickListener {

            Controller.isSender = false
            val intent = Intent(this, SelectionActivity::class.java)
            intent.putExtra("checkFlow","1")
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        binding?.sendBtn?.setOnClickListener {

            Controller.isSender = true
            val intent = Intent(this, CloneSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

    }
}