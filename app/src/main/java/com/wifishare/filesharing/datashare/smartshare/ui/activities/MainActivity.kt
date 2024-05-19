package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityMainBinding
import com.wifishare.filesharing.datashare.smartshare.databinding.RateusDialogBinding
import com.wifishare.filesharing.datashare.smartshare.util.TinyDB
import com.wifishare.filesharing.datashare.smartshare.util.toast


class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null
    private var doubleClick = false
    private val rateusDialogBinding: RateusDialogBinding by lazy {
        RateusDialogBinding.inflate(layoutInflater)
    }
    private var rateusdialog: Dialog? = null
    var rating = 0
    private var rateusdialogcount = 1
    private var tinyDB: TinyDB? = null
    private var reviewInfo: ReviewInfo? = null
    private lateinit var appUpdateManager: AppUpdateManager
    private val REQ_CODE_VERSION_UPDATE = 530


    companion object{
    }

    val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.hide()
        setContentView(binding?.root)
        rateusdialog = Dialog(this)
        tinyDB = TinyDB(this)
        rateusdialogcount = tinyDB?.getInt("rateusdialogcount")!!


        binding?.bottomNavigation?.setupWithNavController(navController)
        val appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.homeFragment,
                    R.id.filesFragment,
                    R.id.settingsFragment
                )
            )
        setupActionBarWithNavController(navController, appBarConfiguration)


        if (Controller.showrateusdialog && rateusdialogcount >= Controller.showrateusdialogcount && tinyDB?.getInt(
                "rate"
            ) != 1
        ) {
            rateusdialogcount = 0
            tinyDB?.putInt("rateusdialogcount", rateusdialogcount)
            showRateusDialog()
        } else {
            rateusdialogcount++
            tinyDB?.putInt("rateusdialogcount", rateusdialogcount)
        }

        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkUpdate()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    if (Controller.forceupdate) {
                        try {
                            appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                this,
                                100
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.FLEXIBLE,
                                this,
                                100
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            e.printStackTrace()
                        }
                    }
                }
            }



        binding?.bottomNavigation?.itemIconTintList = null

    }


    override fun onBackPressed() {
        if (doubleClick) {
            finishAffinity()
        } else {
            toast(this, "Tap again to exit")
            doubleClick = true
            Handler().postDelayed({ doubleClick = false }, 1000)
        }
    }


    private fun checkUpdate() {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this@MainActivity, 100
                        )
                        appUpdateManager.registerListener(listener!!)
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                } else {

                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                }

                Activity.RESULT_CANCELED -> {
                    if (Controller.forceupdate) {
                        finish()
                    }
                }

                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {

                }
            }
        }
    }

    private val listener: InstallStateUpdatedListener? =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {

                popupSnackbarForCompleteUpdate()
            }
        }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(R.id.main_container),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            setActionTextColor(resources.getColor(R.color.white))
            show()
        }
        appUpdateManager.unregisterListener(listener!!)
    }


    @SuppressLint("SetTextI18n")
    private fun showRateusDialog() {
        rateusdialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        rateusdialog?.setContentView(rateusDialogBinding.root)
        rateusdialog?.setCanceledOnTouchOutside(false)
        rateusdialog?.setCancelable(false)

        rateusdialog?.show()

        rateusDialogBinding.submitbutton.setCardBackgroundColor(
            Color.parseColor("#999999")
        )
        rateusDialogBinding.submitText.setTextColor(
            Color.parseColor("#d9d9d9")
        )
        rateusDialogBinding?.submitText?.text = "Rate"
        resetRateUsAnimation(null)


        var submitButtonclickable = false


        rateusDialogBinding.submitbutton?.setOnClickListener {
            if (submitButtonclickable) {
                if (rating >= 0) {
                    if (rating > 3) {
                        tinyDB?.putInt("rate", 1)
                        showInappreviewScreen()
                        rateusdialog?.dismiss()

                    } else {
                        rateusdialog?.dismiss()
                        startActivity(Intent(this, FeedbackActivity::class.java))
                    }
                } else {
                    toast(this, "Please select some rating")
                }
            }
        }


        rateusDialogBinding.onestar.setOnClickListener {
            submitButtonclickable = true
            rating = 1
            setSubmitButtonColor("Send us a feedback")
            resetRateUsAnimation(listOf(rateusDialogBinding.onestar))
        }

        rateusDialogBinding.onetwo.setOnClickListener {
            submitButtonclickable = true
            rating = 2
            setSubmitButtonColor("Send us a feedback")
            resetRateUsAnimation(
                listOf(
                    rateusDialogBinding.onestar,
                    rateusDialogBinding.onetwo
                )
            )
        }

        rateusDialogBinding.onethree.setOnClickListener {
            submitButtonclickable = true
            rating = 3
            setSubmitButtonColor("Send us a feedback")
            resetRateUsAnimation(
                listOf(
                    rateusDialogBinding.onestar,
                    rateusDialogBinding.onetwo,
                    rateusDialogBinding.onethree
                )
            )
        }

        rateusDialogBinding.onefour.setOnClickListener {
            submitButtonclickable = true
            rating = 4
            setSubmitButtonColor("Rate")
            resetRateUsAnimation(
                listOf(
                    rateusDialogBinding.onestar,
                    rateusDialogBinding.onetwo,
                    rateusDialogBinding.onethree,
                    rateusDialogBinding.onefour
                )
            )
        }

        rateusDialogBinding.onefive.setOnClickListener {
            submitButtonclickable = true
            rating = 5
            setSubmitButtonColor("Rate")
            resetRateUsAnimation(
                listOf(
                    rateusDialogBinding.onestar,
                    rateusDialogBinding.onetwo,
                    rateusDialogBinding.onethree,
                    rateusDialogBinding.onefour,
                    rateusDialogBinding.onefive
                )
            )
        }

        rateusDialogBinding.closeBtn.setOnClickListener {
            rateusdialog?.dismiss()
        }

    }


    fun resetRateUsAnimation(
        listofimageview: List<ImageView>?
    ) {
        rateusDialogBinding.onestar.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.ic_normalstar
            )
        )
        rateusDialogBinding.onetwo.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.ic_normalstar
            )
        )
        rateusDialogBinding.onethree.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.ic_normalstar
            )
        )
        rateusDialogBinding.onefour.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.ic_normalstar
            )
        )
        rateusDialogBinding.onefive.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.ic_normalstar
            )
        )

        listofimageview?.forEach {
            it?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.ic_goldstar
                )
            )
        }
    }


    fun setSubmitButtonColor(submitText: String) {
        rateusDialogBinding.submitbutton.setCardBackgroundColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.mainthemecolor
            )
        )
        rateusDialogBinding.submitText.setTextColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.white
            )
        )
        rateusDialogBinding.submitText.text = submitText
    }

    fun showInappreviewScreen() {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                //Received ReviewInfo object
                reviewInfo = request.result
                reviewInfo?.let {
                    val flow = manager.launchReviewFlow(this@MainActivity, it)
                    flow.addOnCompleteListener {
                        tinyDB?.putInt("rate", 1)
                    }
                }
            } else {
                reviewInfo = null
            }
        }
    }
}