package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityFeedbackBinding
import com.wifishare.filesharing.datashare.smartshare.util.TinyDB

class FeedbackActivity : AppCompatActivity() {

    private var binding : ActivityFeedbackBinding? = null
    var submitbtnenable = false
    private var tinyDB : TinyDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        tinyDB = TinyDB(this)

        binding?.etView?.requestFocus()

        binding?.backpressBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.etView?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s?.length ?: 0) >= 20){
                    submitbtnenable = true
                    binding?.sendBtn?.setCardBackgroundColor(ContextCompat.getColor(this@FeedbackActivity , R.color.mainthemecolor))
                    binding?.sentBtnText?.setTextColor(ContextCompat.getColor(this@FeedbackActivity , R.color.white))
                }
                else {
                    submitbtnenable = false
                    binding?.sendBtn?.setCardBackgroundColor(Color.parseColor("#54AFAFAF"))
                    binding?.sentBtnText?.setTextColor(Color.parseColor("#54636363"))
                }
                // This method is called when the text is being changed.
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding?.sendBtn?.setOnClickListener {
            if (submitbtnenable){
                tinyDB?.putInt("rate", 1)
                sendMail()
            }
        }

    }


    private fun sendMail() {
        val editText = binding?.etView
        val textEnteredByUser = editText?.text.toString()
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("technowiseinnovations@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Review by User for Smart Share")
        intent.putExtra(Intent.EXTRA_TEXT, textEnteredByUser)
        startActivity(Intent.createChooser(intent, "Email via..."))
    }
}