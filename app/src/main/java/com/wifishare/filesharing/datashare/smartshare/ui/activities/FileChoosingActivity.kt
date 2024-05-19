package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityFileChoosingBinding
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel.Companion.apkFile
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel.Companion.audioFile
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel.Companion.docFile
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel.Companion.imgFile
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel.Companion.videoFile
import com.wifishare.filesharing.datashare.smartshare.util.clickWithThrottle

class FileChoosingActivity : AppCompatActivity() {

    private var binding : ActivityFileChoosingBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileChoosingBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Controller.mFileInfoMap.clear()
        FilesSelectionActivity.selectedFilesList.clear()


        binding?.imagesBtn?.clickWithThrottle {
            FilesSelectionActivity.activitytype = "Images"
            FilesSelectionActivity.selectionPoint = 0
            startActivity(Intent(this , FilesSelectionActivity :: class.java))
        }

        binding?.videosBtn?.clickWithThrottle {
            FilesSelectionActivity.activitytype = "Videos"
            FilesSelectionActivity.selectionPoint = 1
            startActivity(Intent(this , FilesSelectionActivity :: class.java))
        }

        binding?.musicBtn?.clickWithThrottle {
            FilesSelectionActivity.activitytype = "Audios"
            FilesSelectionActivity.selectionPoint = 2
            startActivity(Intent(this , FilesSelectionActivity :: class.java))
        }

        binding?.docsBtn?.clickWithThrottle {
            FilesSelectionActivity.activitytype = "Documents"
            FilesSelectionActivity.selectionPoint = 3
            startActivity(Intent(this , FilesSelectionActivity :: class.java))
        }

        binding?.appsBtn?.clickWithThrottle {
            FilesSelectionActivity.activitytype = "Apk"
            FilesSelectionActivity.selectionPoint = 4
            startActivity(Intent(this , FilesSelectionActivity :: class.java))
        }


    }

}