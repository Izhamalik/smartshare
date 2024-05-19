package com.wifishare.filesharing.datashare.smartshare.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentFilesBinding
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment.Companion.percentage
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment.Companion.totalempty
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment.Companion.totaluse
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel
import com.wifishare.filesharing.datashare.smartshare.ui.activities.HistoryActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.HistoryDetailsActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SmartSwitchActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SplashActivity.Companion.fileLoaded
import com.wifishare.filesharing.datashare.smartshare.util.clickWithThrottle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt

class FilesFragment : Fragment() {

    private var binding : FragmentFilesBinding? = null

    private lateinit var backgroundViewModel: BackgroundViewModel

    private val CODE_REQ_PERMISSIONS = 665
    private var isAllDone = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilesBinding.inflate(layoutInflater , container , false)



        binding?.imagesBtn?.clickWithThrottle {
            HistoryDetailsActivity.selectionPoint = 0
            startActivity(Intent(requireActivity() , HistoryDetailsActivity :: class.java))
        }

        binding?.videosBtn?.clickWithThrottle {
            HistoryDetailsActivity.selectionPoint = 1
            startActivity(Intent(requireActivity() , HistoryDetailsActivity :: class.java))
        }

        binding?.audiosBtn?.clickWithThrottle {
            HistoryDetailsActivity.selectionPoint = 2
            startActivity(Intent(requireActivity() , HistoryDetailsActivity :: class.java))
        }

        binding?.docsBtn?.clickWithThrottle {
            HistoryDetailsActivity.selectionPoint = 3
            startActivity(Intent(requireActivity() , HistoryDetailsActivity :: class.java))
        }

        binding?.appsBtn?.clickWithThrottle {
            HistoryDetailsActivity.selectionPoint = 4
            startActivity(Intent(requireActivity() , HistoryDetailsActivity :: class.java))
        }

        binding?.phonecloneBtn?.setOnClickListener {
            if (!checkPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestPermission()
                } else {

                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            Manifest.permission.CHANGE_NETWORK_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE
                        ), CODE_REQ_PERMISSIONS
                    )
                }
            }
            if (!HomeFragment.isServiceRunning) {
                Log.d("IzharMalik" , "$fileLoaded")
                if (fileLoaded) {
                    val intent = Intent(requireActivity(), SmartSwitchActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please wait, data is being calculated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please wait, data is being Transferred",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        loadData()

        binding?.cardHistory?.setOnClickListener {
                if (!checkPermission()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        requestPermission()
                    }
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            Manifest.permission.CHANGE_NETWORK_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE
                        ), CODE_REQ_PERMISSIONS
                    )
                } else {
                    val intent = Intent(requireActivity(), HistoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }
        }

        return binding?.root
    }



    private fun loadData() {

        if (checkPermission()) {
            CoroutineScope(Dispatchers.Main).launch {

                backgroundViewModel =
                    ViewModelProvider(requireActivity())[BackgroundViewModel::class.java]
                backgroundViewModel.imgSize.observe(viewLifecycleOwner) {
                    Controller.mainImages = it.toString()
                }

                backgroundViewModel.videoSize.observe(viewLifecycleOwner) {
                    Controller.mainVideos = it.toString()
                }

                backgroundViewModel.audioSize.observe(viewLifecycleOwner) {
                    Controller.mainAudios = it.toString()
                    fileLoaded = true
                }

                backgroundViewModel.docSize.observe(viewLifecycleOwner) {
                    Controller.mainDocuments = it.toString()
                }

                backgroundViewModel.apkSize.observe(viewLifecycleOwner) {
                    Controller.mainAPK = it.toString()
                    setData()
                }

                backgroundViewModel.percentageRec.observe(viewLifecycleOwner) {
                    percentage = it.roundToInt()
                }

                backgroundViewModel.totalUsed.observe(viewLifecycleOwner) {
                    totaluse = it
                }

                backgroundViewModel.totalFree.observe(viewLifecycleOwner) {
                    totalempty = it
                }


            }

        }
    }


    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", requireActivity().applicationContext.packageName))
//                startActivityForResult(intent, CODE_REQ_PERMISSIONS)
                resultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                //   startActivityForResult(intent, CODE_REQ_PERMISSIONS)
                resultLauncher.launch(intent)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                CODE_REQ_PERMISSIONS
            )
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadData()
                val path = File(resources.getString(R.string.download_path))
                if (!path.exists()) {
                    path.mkdirs()
                }
            }
        }


    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_REQ_PERMISSIONS) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        requireContext(),
                        resources.getText(R.string.allow_all_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    isAllDone = false

                    return
                } else {
                    loadData()
                }
            }
            isAllDone = true
            val path = File(resources.getString(R.string.download_path))
            if (!path.exists()) {
                path.mkdirs()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    fun setData() {
        binding?.apply {
                tvImagesSize.text = "(${Controller.mainImages})"
                tvVideoSize.text = "(${Controller.mainVideos})"
               tvAppsSize.text = "(${Controller.mainAPK})"
               tvAudioSize.text = "(${Controller.mainAudios})"
                tvDocumentsSize.text = "(${Controller.mainDocuments})"

            circleView?.progress = percentage
            percentAge?.text = "$percentage%"
            txtUsed?.text = totaluse
            txtFree?.text = totalempty
        }
    }
}