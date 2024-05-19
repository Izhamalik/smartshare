package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.adapters.FilesSelectionPagerAdapter
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityFilesSelectionBinding
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.filesinfo.TextUtils
import com.wifishare.filesharing.datashare.smartshare.util.clickWithThrottle
import com.wifishare.filesharing.datashare.smartshare.util.toast
import org.w3c.dom.Text

class FilesSelectionActivity : AppCompatActivity() {

    private var filesSelectionPagerAdapter : FilesSelectionPagerAdapter? = null

    companion object {
        var fileselectionbinding : ActivityFilesSelectionBinding? = null
        var activitytype = ""
        var selectionPoint = 0
        var selectedFilesList : ArrayList<FileInfo> = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileselectionbinding = ActivityFilesSelectionBinding.inflate(layoutInflater)
        setContentView(fileselectionbinding?.root)

        fileselectionbinding?.backpressBtn?.clickWithThrottle { onBackPressed() }

        fileselectionbinding?.reviewBtn?.clickWithThrottle {
            if (selectedFilesList.size > 0){
                ReviewSelectionActivity.selectedfileslists = selectedFilesList
                startActivity(Intent(this , ReviewSelectionActivity :: class.java))
            }
            else {
                toast(this , "Please select files")
            }
        }



        filesSelectionPagerAdapter = FilesSelectionPagerAdapter(this , 5)
        fileselectionbinding?.viewPager2?.adapter = filesSelectionPagerAdapter

        organizeViews()

        fileselectionbinding?.imagesBtn?.setOnClickListener {
            fileselectionbinding?.viewPager2?.currentItem = 0
            fileselectionbinding?.indicator1?.let { it1 -> resetcolor(it1, fileselectionbinding?.text1 , "Images") }
        }


        fileselectionbinding?.docsBtn?.setOnClickListener {
            fileselectionbinding?.viewPager2?.currentItem = 3
            fileselectionbinding?.indicator4?.let { it1 -> resetcolor(it1, fileselectionbinding?.text4 , "Documents") }
        }


        fileselectionbinding?.videosBtn?.setOnClickListener {
            fileselectionbinding?.viewPager2?.currentItem = 1
            fileselectionbinding?.indicator2?.let { it1 -> resetcolor(it1, fileselectionbinding?.text2 , "Videos") }
        }


        fileselectionbinding?.musicBtn?.setOnClickListener {
            fileselectionbinding?.viewPager2?.currentItem = 2
            fileselectionbinding?.indicator3?.let { it1 -> resetcolor(it1, fileselectionbinding?.text3 , "Music") }
        }


        fileselectionbinding?.appsBtn?.setOnClickListener {
            fileselectionbinding?.viewPager2?.currentItem = 4
            fileselectionbinding?.indicator5?.let { it1 -> resetcolor(it1, fileselectionbinding?.text5 , "Apps") }
        }


        fileselectionbinding?.viewPager2?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 ->  fileselectionbinding?.indicator1?.let { it1 -> resetcolor(it1, fileselectionbinding?.text1 , "Images") }

                    1 ->  fileselectionbinding?.indicator2?.let { it1 -> resetcolor(it1, fileselectionbinding?.text2 , "Videos") }

                    2 -> fileselectionbinding?.indicator3?.let { it1 -> resetcolor(it1, fileselectionbinding?.text3 , "Music") }

                    3 ->  fileselectionbinding?.indicator4?.let { it1 -> resetcolor(it1, fileselectionbinding?.text4 , "Documents") }

                    4 -> fileselectionbinding?.indicator5?.let { it1 -> resetcolor(it1, fileselectionbinding?.text5 , "Apps") }
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


    }

    override fun onBackPressed() {
        super.onBackPressed()
        selectedFilesList.clear()
    }


    private fun organizeViews(){
        fileselectionbinding?.viewPager2?.currentItem = selectionPoint
        when (selectionPoint){
            0 -> {
                fileselectionbinding?.indicator1?.let { it1 -> resetcolor(it1, fileselectionbinding?.text1 , "Images") }
            }

            1 -> {
                fileselectionbinding?.indicator2?.let { it1 -> resetcolor(it1, fileselectionbinding?.text2 , "Videos") }
            }

            2 -> {
                fileselectionbinding?.indicator3?.let { it1 -> resetcolor(it1, fileselectionbinding?.text3 , "Music") }
            }

            3 -> {
                fileselectionbinding?.indicator4?.let { it1 -> resetcolor(it1, fileselectionbinding?.text4 , "Documents") }
            }

            4 -> {
                fileselectionbinding?.indicator5?.let { it1 -> resetcolor(it1, fileselectionbinding?.text5 , "Apps") }
            }
        }
    }


    private fun resetcolor(m: MaterialCardView, s: TextView? , t : String) {
        fileselectionbinding?.headerTxt?.text = "$t Selection"

        var colorStateList = ColorStateList.valueOf(resources.getColor(R.color.offwhite))
        fileselectionbinding?.indicator1?.backgroundTintList = colorStateList
        fileselectionbinding?.indicator2?.backgroundTintList = colorStateList
        fileselectionbinding?.indicator3?.backgroundTintList = colorStateList
        fileselectionbinding?.indicator4?.backgroundTintList = colorStateList
        fileselectionbinding?.indicator5?.backgroundTintList = colorStateList

        fileselectionbinding?.text1?.setTextColor(ContextCompat.getColor(this , R.color.light_gray))
        fileselectionbinding?.text2?.setTextColor(ContextCompat.getColor(this , R.color.light_gray))
        fileselectionbinding?.text3?.setTextColor(ContextCompat.getColor(this , R.color.light_gray))
        fileselectionbinding?.text4?.setTextColor(ContextCompat.getColor(this , R.color.light_gray))
        fileselectionbinding?.text5?.setTextColor(ContextCompat.getColor(this , R.color.light_gray))

        colorStateList = ColorStateList.valueOf(resources.getColor(R.color.mainthemecolor))
        m.backgroundTintList = colorStateList
        s?.setTextColor(ContextCompat.getColor(this , R.color.mainthemecolor))

    }
}