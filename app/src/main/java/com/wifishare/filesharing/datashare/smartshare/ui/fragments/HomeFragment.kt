package com.wifishare.filesharing.datashare.smartshare.ui.fragments

import android.Manifest
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
import androidx.navigation.fragment.findNavController
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentHomeBinding
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SmartSwitchActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SplashActivity.Companion.fileLoaded
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel
import com.wifishare.filesharing.datashare.smartshare.ui.activities.FileChoosingActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SelectionActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.ShareFilesActivity
import com.wifishare.filesharing.datashare.smartshare.util.clickWithThrottle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt


class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val CODE_REQ_PERMISSIONS = 665
    private lateinit var backgroundViewModel: BackgroundViewModel

    companion object {
        var totaluse = "0"
        var totalempty = "0"
        var percentage = 0
        var isServiceRunning = false
        var isSocketClosed = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)


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
            if (!isServiceRunning) {
                Log.d("IzharMalik", "$fileLoaded")
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

        binding?.sharefilesBtn?.clickWithThrottle {
            startActivity(Intent(requireActivity(), ShareFilesActivity::class.java))
        }



        binding?.receivefilesBtn?.clickWithThrottle {
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
                Controller.isSender = false

                if (!isServiceRunning) {

                    val intent = Intent(requireActivity(), SelectionActivity::class.java)
                    intent.putExtra("checkFlow", "startActivity")
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please wait, data is being Transferred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }



        binding?.storageBtn?.setOnClickListener {
            findNavController().navigate(R.id.filesFragment)
        }

        binding?.settingsBtn?.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }

        loadData()

        binding?.sendfilesBtn?.setOnClickListener {
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

                Controller.isSender = true
                if (!isServiceRunning) {
                    if (fileLoaded) {

                        val intent = Intent(requireActivity(), FileChoosingActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.putExtra("CurrentItem", 15)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Please wait, data is being calculated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Please wait, data is being Transferred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }



        binding?.receivefilesBtn?.setOnClickListener {
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
                Controller.isSender = false

                if (!isServiceRunning) {

                    val intent = Intent(requireActivity(), SelectionActivity::class.java)
                    intent.putExtra("checkFlow", "startActivity")
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please wait, data is being Transferred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }



        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission())
            loadData()
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

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(
                    String.format(
                        "package:%s",
                        requireActivity().applicationContext.packageName
                    )
                )
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
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                CODE_REQ_PERMISSIONS
            )
        }
    }


    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            val result1 =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun loadData() {

        if (checkPermission()) {
            CoroutineScope(Dispatchers.Main).launch {

                backgroundViewModel =
                    ViewModelProvider(requireActivity())[BackgroundViewModel::class.java]
                backgroundViewModel.imgSize.observe(requireActivity()) {
                    Controller.mainImages = it.toString()
                }

                backgroundViewModel.videoSize.observe(requireActivity()) {
                    Controller.mainVideos = it.toString()
                }

                backgroundViewModel.audioSize.observe(requireActivity()) {
                    Controller.mainAudios = it.toString()
                    fileLoaded = true
                }

                backgroundViewModel.docSize.observe(requireActivity()) {
                    Controller.mainDocuments = it.toString()
                }

                backgroundViewModel.apkSize.observe(requireActivity()) {
                    Controller.mainAPK = it.toString()
//                    setData()
                }

                backgroundViewModel.percentageRec.observe(requireActivity()) {
//            binding.appBarStart.contentStart.circleView.stopSpinning()
                    percentage = it.roundToInt()
                }

                backgroundViewModel.totalUsed.observe(requireActivity()) {
                    totaluse = it
                }

                backgroundViewModel.totalFree.observe(requireActivity()) {
                    totalempty = it
                }

                backgroundViewModel.loadFiles(requireActivity())
            }

        }
    }

}