package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityCloneSelectionBinding
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel.Companion.audioFile
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel.Companion.docFile
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel.Companion.imgFile
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel.Companion.videoFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CloneSelectionActivity : AppCompatActivity() {

    private var binding : ActivityCloneSelectionBinding? = null
    private var isCheckedAll = false
    private var isCheckedImg = false
    private var isCheckedVideo = false
    private var isCheckedDoc = false
    private var isCheckedMusic = false
    var sFileInfoList: List<FileInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloneSelectionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Controller.mFileInfoMap.clear()
        CoroutineScope(Dispatchers.Main).launch {
            binding?.tvImagesSize?.text = "(${Controller.mainImages})"
            binding?.tvVideoSize?.text = "(${Controller.mainVideos})"
            binding?.tvAudioSize?.text = "(${Controller.mainAudios})"
            binding?.tvDocumentsSize?.text = "(${Controller.mainDocuments})"
        }

        binding?.backpressBtn?.setOnClickListener {
            onBackPressed()
        }


        binding?.frameImages?.setOnClickListener {

            if (isCheckedImg) {
                isCheckedImg = false

                binding?.imgChecked?.setImageResource(R.drawable.ic_un_selected_circle)
                CoroutineScope(Dispatchers.Main).launch {
                    removeData(imgFile!!)

                }

            } else {

                isCheckedImg = true
                binding?.imgChecked?.setImageResource(R.drawable.ic_selected_circle)
                CoroutineScope(Dispatchers.Main).launch {
                    addData(imgFile!!)
                    binding?.totalFiles?.text="Total Files : ${Controller.mFileInfoMap.size}"

                }
            }
        }



        binding?.frameVideo?.setOnClickListener {
            if (isCheckedVideo) {
                isCheckedVideo = false

                binding?.videoChecked?.setImageResource(R.drawable.ic_un_selected_circle)
                CoroutineScope(Dispatchers.Main).launch {
                    removeData(videoFile!!)
                    binding?.totalFiles?.text="Total Files : ${Controller.mFileInfoMap.size}"

                }
            } else {
                isCheckedVideo = true

                binding?.videoChecked?.setImageResource(R.drawable.ic_selected_circle)
                CoroutineScope(Dispatchers.Main).launch {
                    addData(videoFile!!)
                    binding?.totalFiles?.text="Total Files : ${Controller.mFileInfoMap.size}"

                }
            }
        }


        binding?.frameDoc?.setOnClickListener {
            if (isCheckedDoc) {
                isCheckedDoc = false

                binding?.docChecked?.setImageResource(R.drawable.ic_un_selected_circle)
                CoroutineScope(Dispatchers.Main).launch {
                    removeData(docFile!!)
                    binding?.totalFiles?.text="Total Files : ${Controller.mFileInfoMap.size}"

                }
            } else {
                isCheckedDoc = true

                binding?.docChecked?.setImageResource(R.drawable.ic_selected_circle)
                CoroutineScope(Dispatchers.Main).launch {
                    addData(docFile!!)
                    binding?.totalFiles?.text="Total Files : ${Controller.mFileInfoMap.size}"

                }
            }
        }

        binding?.frameMusic?.setOnClickListener {
            if (isCheckedMusic) {
                isCheckedMusic = false

                binding?.musicChecked?.setImageResource(R.drawable.ic_un_selected_circle)
                CoroutineScope(Dispatchers.Main).launch {
                    removeData(audioFile!!)
                    binding?.totalFiles?.text="Total Files : ${Controller.mFileInfoMap.size}"

                }
            } else {
                isCheckedMusic = true

                binding?.musicChecked?.setImageResource(R.drawable.ic_selected_circle)
                CoroutineScope(Dispatchers.Main).launch {
                    addData(audioFile!!)
                    binding?.totalFiles?.text="Total Files : ${Controller.mFileInfoMap.size}"

                }
            }
        }


        binding?.btnNext?.setOnClickListener {
            if (Controller.mFileInfoMap.size > 0) {
                Controller.isSender =true

                val intent = Intent(this, SelectionActivity::class.java)
                intent.putExtra("checkFlow","1")

                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            } else {
                Toast.makeText(this, "Select atleast one media type!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun addData(arr: List<FileInfo>) {

        CoroutineScope(Dispatchers.Main).launch {
            arr.forEach {
                Controller.addFileInfo(it)
            }
            binding?.totalFiles?.text="Total Files : ${Controller.mFileInfoMap.size}"

        }
    }

    private suspend fun removeData(arr: List<FileInfo>) {
        CoroutineScope(Dispatchers.Main).launch {
            arr.forEach {
                Controller.delFileInfo(it)
            }
            binding?.totalFiles?.text="Total Files : ${Controller.mFileInfoMap.size}"
            if(Controller.mFileInfoMap.size==0){
                binding?.totalFiles?.text= ""
            }
        }
//        delay(500)
    }
}