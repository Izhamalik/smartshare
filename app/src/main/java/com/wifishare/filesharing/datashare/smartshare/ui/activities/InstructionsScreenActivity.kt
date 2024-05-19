package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.format.Formatter
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.adapters.MyViewPagerAdapter
import com.wifishare.filesharing.datashare.smartshare.common.Constants
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityInstructionsScreenBinding
import com.wifishare.filesharing.datashare.smartshare.otherutils.Keyword
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentFive
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentFour
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentOne
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.InstructionFragmentThree
import java.util.Random

class InstructionsScreenActivity : AppCompatActivity() {

    private var binding: ActivityInstructionsScreenBinding? = null
    var activity: String = ""
    var text: String = ""
    lateinit var sharedPreferences_not_show_agin: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private var adapter: MyViewPagerAdapter? = null
    private var text1 = ""
    private var text2 = ""
    private var text3 = ""
    private var text4 = ""
    private var text5 = ""
    private var string: SpannableString? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstructionsScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.backpressBtn?.setOnClickListener {
            finish()
        }

        text1 = resources.getString(R.string.point1_receiver)
        text2 = resources.getString(R.string.point2_receiver)

        var intent1: Intent
        activity = intent.getStringExtra("wifiOrHotspot")!!
        text =
            "Heyy, Install this application to receive files instantly: https://play.google.com/store/apps/details?id=${packageName}\n $text1\n$text2\n$text3\n$text4\n$text5"
        if (activity.contains("receive")) {
            text =
                "Heyy, Install this application to send files instantly: https://play.google.com/store/apps/details?id=$packageName\n $text1\n$text2\n$text3\n$text4\n$text5"

        }


        string = SpannableString(resources.getText(R.string.point1_receiver))
        val clickHandler: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(tp: TextPaint) {
                super.updateDrawState(tp)
                tp.isUnderlineText = true
                tp.color = resources.getColor(R.color.mainthemecolor)
            }

            override fun onClick(p0: View) {

                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Smart Transfer: File Sharing"
                )
                sharingIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    text
                )
                startActivity(Intent.createChooser(sharingIntent, "Share app via"))
            }
        }
        string?.setSpan(clickHandler, 9, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding?.indicatortext?.text = string
        binding?.indicatortext?.movementMethod = LinkMovementMethod.getInstance()
        binding?.indicatortext?.highlightColor = Color.TRANSPARENT


        binding?.indicatortext?.movementMethod = LinkMovementMethod.getInstance()
        binding?.indicatortext?.setLinkTextColor(Color.BLUE)


        when (activity) {
            "send_wifi0" -> {

                intent1 = Intent(this@InstructionsScreenActivity, DataTransferActivity::class.java)
                intent1.putExtra("HOST_ADDRESS", Controller.hostAddress.toString())
                intent.putExtra("intentFrom", "activity")

                intent1.putExtra("FROM_WIFI", Keyword.QR_CODE_TYPE_WIFI)
                intent1.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                adapter = MyViewPagerAdapter(this, 5)
                text4 = resources.getText(R.string.point4_receiver).toString()
                text5 = resources.getText(R.string.point5_receiver).toString()
                InstructionFragmentFive.imagetype = "group"
                InstructionFragmentFour.imagetype = "directwifi"
                InstructionFragmentThree.imagetype = ""
            }

            "send_hotspot0" -> {
                intent1 = Intent(this, ScannerActivity::class.java)
                intent1.putExtra("wifiOrHotspot", activity)

                text4 = resources.getText(R.string.point4_receiver_hotspot).toString()
                text5 = resources.getText(R.string.point5_receiver_hotspot).toString()

                InstructionFragmentFive.imagetype = "qr"
                InstructionFragmentFour.imagetype = "hotspot"
                InstructionFragmentThree.imagetype = ""

                adapter = MyViewPagerAdapter(this, 5)
            }

            "receive_wifi0" -> {

                intent1 = Intent(this, ReceivingDataActivity::class.java)
                intent1.putExtra("wifiOrHotspot", activity)
                intent.putExtra("intentFrom", "activity")

                text3 = resources.getText(R.string.point3_send).toString()
                text4 = resources.getText(R.string.point4_receiver).toString()

                InstructionFragmentFive.imagetype = ""
                InstructionFragmentFour.imagetype = "directwifi"
                InstructionFragmentThree.imagetype = "send"
                adapter = MyViewPagerAdapter(this, 4)
            }

            "receive_hotspot0" -> {
                intent1 = Intent(this, HotSpotActivity::class.java)
                intent1.putExtra("wifiOrHotspot", activity)
                text3 = resources.getText(R.string.point3_send).toString()
                text4 = resources.getText(R.string.point4_receiver).toString()
                InstructionFragmentFive.imagetype = ""
                InstructionFragmentFour.imagetype = "directwifi"
                InstructionFragmentThree.imagetype = "send"
                adapter = MyViewPagerAdapter(this, 4)
            }

            "send_wifi1" -> {
                exitDialog("")
                intent1 = Intent(this@InstructionsScreenActivity, DataTransferActivity::class.java)
                intent1.putExtra("HOST_ADDRESS", Controller.hostAddress.toString())
                intent.putExtra("intentFrom", "activity")

                intent1.putExtra("FROM_WIFI", Keyword.QR_CODE_TYPE_WIFI)
                intent1.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                text3 = resources.getText(R.string.point3_receiver_clone).toString()
                text4 = resources.getText(R.string.point4_receiver).toString()
                text5 = resources.getText(R.string.point5_receiver).toString()

                InstructionFragmentFive.imagetype = "group"
                InstructionFragmentFour.imagetype = "directwifi"
                InstructionFragmentThree.imagetype = "receive"
                adapter = MyViewPagerAdapter(this, 5)
            }

            "send_hotspot1" -> {
                intent1 = Intent(this, ScannerActivity::class.java)
                intent1.putExtra("wifiOrHotspot", activity)

                text3 = resources.getText(R.string.point3_receiver_clone).toString()
                text4 = resources.getText(R.string.point4_receiver_hotspot).toString()
                text5 = resources.getText(R.string.point5_receiver_hotspot).toString()

                InstructionFragmentFive.imagetype = "qr"
                InstructionFragmentFour.imagetype = "hotspot"
                InstructionFragmentThree.imagetype = "receive"
                adapter = MyViewPagerAdapter(this, 5)
            }

            "receive_wifi1" -> {
                val min = 2000
                val max = 8000
                val random = Random().nextInt(max - min + 1) + min

                val wm: WifiManager =
                    applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val ip: String = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
                val ipa = ip.split(".").takeLast(1).joinToString(".")
                Log.e("ccascascasd", "" + ipa.toString())
                val ipaa = ip.split("\\.(?=[^.]+$)".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0]
                Log.e("ccascascasd", "" + ipaa.toString())

                Toast.makeText(
                    this@InstructionsScreenActivity, "$ip=====$random" + "======${ipa}",
                    Toast.LENGTH_LONG
                ).show()

                intent1 = Intent(this, ReceivingDataActivity::class.java)
                intent1.putExtra("wifiOrHotspot", activity)
                intent.putExtra("intentFrom", "activity")


                text3 = resources.getText(R.string.point3_send_clone).toString()
                text4 = resources.getText(R.string.point32_send_clone).toString()
                text5 = resources.getText(R.string.point4_receiver_clone).toString()

                InstructionFragmentFive.imagetype = "directwifi"
                InstructionFragmentFour.imagetype = "next"
                InstructionFragmentThree.imagetype = "send"
                adapter = MyViewPagerAdapter(this, 5)
            }

            else -> {
                intent1 = Intent(this, HotSpotActivity::class.java)
                intent1.putExtra("wifiOrHotspot", activity)


                text3 = resources.getText(R.string.point3_send_clone).toString()
                text4 = resources.getText(R.string.point32_send_clone).toString()
                text5 = resources.getText(R.string.point4_receiver_hotspot_clone).toString()

                InstructionFragmentFive.imagetype = "hotspot"
                InstructionFragmentFour.imagetype = "next"
                InstructionFragmentThree.imagetype = "send"
                adapter = MyViewPagerAdapter(this, 5)
            }
        }

        binding?.viewPager2?.adapter = adapter

        binding?.viewPager2?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> binding?.indicator1?.let {
                        resetcolor(
                            it,
                            ""
                        )
                        binding?.indicatortext?.text = string
                        binding?.indicatortext?.movementMethod = LinkMovementMethod.getInstance()
                        binding?.indicatortext?.highlightColor = Color.TRANSPARENT
                    }

                    1 -> binding?.indicator2?.let {
                        resetcolor(
                            it,
                            text2
                        )
                    }

                    2 -> binding?.indicator3?.let {
                        resetcolor(
                            it,
                            text3
                        )
                    }

                    3 -> binding?.indicator4?.let {
                        resetcolor(
                            it,
                            text4
                        )
                    }

                    4 -> binding?.indicator5?.let {
                        resetcolor(
                            it,
                            text5
                        )
                    }
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        binding?.dontShow?.setOnClickListener {

            editor.putBoolean("value_not_show_again_instruction_$activity", true).apply()

            try {
                if (activity.contains("send_hotspot0"))
                    startActivityForResult(intent1, 1)
                else {
                    startActivity(intent1)
                    finish()
                }
            } catch (e: Exception) {

            }


        }

        binding?.doneBtn?.setOnClickListener {

            intent1.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (activity.contains("send_hotspot"))
                startActivityForResult(intent1, 1)
            else {
                startActivity(intent1)
                finish()
            }
        }

        binding?.skipBtn?.setOnClickListener {

            intent1.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (activity.contains("send_hotspot"))
                startActivityForResult(intent1, 1)
            else {
                startActivity(intent1)
                finish()
            }
        }





        binding?.shareBtn?.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Smart Transfer: File Sharing"
            )
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Heyy, Install this application to send files instantly: https://play.google.com/store/apps/details?id=${packageName}\n $text1\n$text2\n$text3\n$text4\n$text5"
            )
            startActivity(Intent.createChooser(sharingIntent, "Share app via"))
        }


    }

    override fun onResume() {
        super.onResume()
        sharedPreferences_not_show_agin=getSharedPreferences("sharedPreferences_not_show_again_instruction", MODE_PRIVATE)
        editor=sharedPreferences_not_show_agin.edit()
    }

    private fun resetcolor(m: MaterialCardView, s: String?) {
        var colorStateList = ColorStateList.valueOf(resources.getColor(R.color.offwhite))
        binding?.indicator1?.backgroundTintList = colorStateList
        binding?.indicator2?.backgroundTintList = colorStateList
        binding?.indicator3?.backgroundTintList = colorStateList
        binding?.indicator4?.backgroundTintList = colorStateList
        binding?.indicator5?.backgroundTintList = colorStateList

        colorStateList = ColorStateList.valueOf(resources.getColor(R.color.mainthemecolor))
        m.backgroundTintList = colorStateList
        if (s != "")
            binding?.indicatortext?.text = s

    }


    private fun exitDialog(code: String) {
        var userRated = 0

        try {
            val dialog = Dialog(this@InstructionsScreenActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_loading_otp) //get layout from ExitDialog folder
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            val editText = dialog.findViewById<EditText>(R.id.editText)
            val okay_btn = dialog.findViewById<Button>(R.id.okay_btn)
            val scanner_btn = dialog.findViewById<Button>(R.id.scanner_btn)

            editText.setText(code)

            scanner_btn.setOnClickListener {
                var intent = Intent(this@InstructionsScreenActivity, ScannerActivity::class.java)
                startActivityForResult(intent, 101)
                dialog.dismiss()
            }

            okay_btn.setOnClickListener {
                if (editText.text.toString() != "") {

                    val phoneNumber = editText.text.toString().trim { it <= ' ' }
                    val strLastFourDi =
                        if (phoneNumber.length >= 4) phoneNumber.substring(phoneNumber.length - 4) else ""

                    val wm: WifiManager =
                        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val ip: String = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
                    val ipaa = ip.split("\\.(?=[^.]+$)".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()[0]

                    Controller.hostAddress = ipaa + "." + phoneNumber.dropLast(4)
                    try {
                        Constants.PORT =
                            strLastFourDi.toInt() // returns abcdefghijklmnopqrstuvwxyzabcdefghi

                    } catch (e: Exception) {

                    }
                    Log.e(
                        "ccccccccccc",
                        "Port ==== ${Constants.PORT}+==========ip${Controller.hostAddress}."
                    )

                    dialog.dismiss()
                } else {
                    Toast.makeText(applicationContext, "Enter Valid OTP", Toast.LENGTH_SHORT).show()
                }

            }

            dialog.show()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {

            val code: String = data!!.getStringExtra("QR_RECEIVED").toString()
            val intent = Intent()
            intent.putExtra("QR_RECEIVED", code)
            setResult(RESULT_OK, intent)
            finish()
        } else if (requestCode == 101 && resultCode == RESULT_OK) {

            val code: String = data!!.getStringExtra("QR_RECEIVED").toString()
            exitDialog(code)
            val phoneNumber = code.toString().trim { it <= ' ' }
            val strLastFourDi =
                if (phoneNumber.length >= 4) phoneNumber.substring(phoneNumber.length - 4) else ""


            val wm: WifiManager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ip: String = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
            val ipaa =
                ip.split("\\.(?=[^.]+$)".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

            Controller.hostAddress = ipaa + "." + phoneNumber.dropLast(4)
            try {
                Constants.PORT =
                    strLastFourDi.toInt() // returns abcdefghijklmnopqrstuvwxyzabcdefghi

            } catch (e: Exception) {

            }
            Log.e(
                "ccccccccccc",
                "Port ==== ${Constants.PORT}+==========ip${Controller.hostAddress}."
            )
            var intent1 = Intent(this@InstructionsScreenActivity, DataTransferActivity::class.java)
            intent1.putExtra("HOST_ADDRESS", Controller.hostAddress.toString())
            intent.putExtra("intentFrom", "activity")

//        intent.putExtra("DEVICE_NAME", mWifiP2pDevice!!.deviceName)
            intent1.putExtra("FROM_WIFI", Keyword.QR_CODE_TYPE_WIFI)
            intent1.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent1)

        }
    }


}