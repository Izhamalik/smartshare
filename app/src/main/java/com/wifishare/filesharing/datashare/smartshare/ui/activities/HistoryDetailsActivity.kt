package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.adapters.FilesSelectionPagerAdapter
import com.wifishare.filesharing.datashare.smartshare.adapters.HistoryViewPagerAdapter
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityHistoryDetailsBinding

class HistoryDetailsActivity : AppCompatActivity() {

    private var binding : ActivityHistoryDetailsBinding? = null
    private var historyViewPagerAdapter : HistoryViewPagerAdapter? = null
    
    companion object {
        var selectionPoint = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        
        historyViewPagerAdapter = HistoryViewPagerAdapter(this , 5)
        binding?.viewPager2?.adapter = historyViewPagerAdapter

        organizeViews()
        
        binding?.viewPager2?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 ->  binding?.indicator1?.let { it1 -> resetcolor(it1, binding?.text1 , "Images") }

                    1 ->  binding?.indicator2?.let { it1 -> resetcolor(it1, binding?.text2 , "Videos") }

                    2 -> binding?.indicator3?.let { it1 -> resetcolor(it1, binding?.text3 , "Music") }

                    3 ->  binding?.indicator4?.let { it1 -> resetcolor(it1, binding?.text4 , "Documents") }

                    4 -> binding?.indicator5?.let { it1 -> resetcolor(it1, binding?.text5 , "Apps") }
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



    private fun organizeViews(){
        binding?.viewPager2?.currentItem = selectionPoint
        when (selectionPoint){
            0 -> {
                binding?.indicator1?.let { it1 -> resetcolor(it1, binding?.text1 , "Images") }
            }

            1 -> {
                binding?.indicator2?.let { it1 -> resetcolor(it1, binding?.text2 , "Videos") }
            }

            2 -> {
                binding?.indicator3?.let { it1 -> resetcolor(it1, binding?.text3 , "Music") }
            }

            3 -> {
                binding?.indicator4?.let { it1 -> resetcolor(it1, binding?.text4 , "Documents") }
            }

            4 -> {
                binding?.indicator5?.let { it1 -> resetcolor(it1, binding?.text5 , "Apps") }
            }
        }
    }


    private fun resetcolor(m: MaterialCardView, s: TextView?, t : String) {
        binding?.headerTxt?.text = "$t Selection"

        var colorStateList = ColorStateList.valueOf(resources.getColor(R.color.offwhite))
        binding?.indicator1?.backgroundTintList = colorStateList
        binding?.indicator2?.backgroundTintList = colorStateList
        binding?.indicator3?.backgroundTintList = colorStateList
        binding?.indicator4?.backgroundTintList = colorStateList
        binding?.indicator5?.backgroundTintList = colorStateList

        binding?.text1?.setTextColor(ContextCompat.getColor(this , R.color.light_gray))
        binding?.text2?.setTextColor(ContextCompat.getColor(this , R.color.light_gray))
        binding?.text3?.setTextColor(ContextCompat.getColor(this , R.color.light_gray))
        binding?.text4?.setTextColor(ContextCompat.getColor(this , R.color.light_gray))
        binding?.text5?.setTextColor(ContextCompat.getColor(this , R.color.light_gray))

        colorStateList = ColorStateList.valueOf(resources.getColor(R.color.mainthemecolor))
        m.backgroundTintList = colorStateList
        s?.setTextColor(ContextCompat.getColor(this , R.color.mainthemecolor))

    }
}