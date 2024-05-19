package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.adapters.SelectionReviewAdapter
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityReviewSelectionBinding
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.interfaces.FileSelectionItem
import com.wifishare.filesharing.datashare.smartshare.util.clickWithThrottle
import com.wifishare.filesharing.datashare.smartshare.util.executeAsyncTask
import kotlinx.coroutines.GlobalScope

class ReviewSelectionActivity : AppCompatActivity(), FileSelectionItem {

    private var binding: ActivityReviewSelectionBinding? = null
    private var videolist = arrayListOf<FileInfo>()
    private var imageslist = arrayListOf<FileInfo>()
    private var audioslist = arrayListOf<FileInfo>()
    private var docslist = arrayListOf<FileInfo>()
    private var appslist = arrayListOf<FileInfo>()

    private var imagesadapter: SelectionReviewAdapter? = null
    private var videosadapter: SelectionReviewAdapter? = null
    private var audiosadapter: SelectionReviewAdapter? = null
    private var docsadapter: SelectionReviewAdapter? = null
    private var appsadapter: SelectionReviewAdapter? = null

    companion object {
        var selectedfileslists: ArrayList<FileInfo> = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewSelectionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setuprecyclerView()

        binding?.backpressBtn?.clickWithThrottle {
            finish()
        }

        binding?.doneBtn?.clickWithThrottle {
            Controller.isSender = true
            FilesSelectionActivity.selectedFilesList.clear()
            val intent = Intent(this, SelectionActivity::class.java)
            intent.putExtra("checkFlow", "0")
            startActivity(intent)
        }

        GlobalScope.executeAsyncTask(onPreExecute = {


        }, doInBackground = {

            selectedfileslists.forEach {
                when (it.fileType) {
                    1 -> {
                        imageslist.add(it)
                    }

                    2 -> {
                        videolist.add(it)
                    }

                    3 -> {
                        audioslist.add(it)
                    }

                    4 -> {
                        docslist.add(it)
                    }

                    5 -> {
                        appslist.add(it)
                    }
                }
            }

        }, onPostExecute = {
            runOnUiThread {
                if (imageslist.size > 0) {
                    binding?.picturelayout?.visibility = View.VISIBLE
                    imagesadapter?.setValues(
                        this@ReviewSelectionActivity,
                        imageslist,
                        "images",
                        this
                    )
                    imagesadapter?.notifyDataSetChanged()
                } else {
                    binding?.picturelayout?.visibility = View.GONE
                }

                if (videolist.size > 0) {
                    binding?.videolayout?.visibility = View.VISIBLE
                    videosadapter?.setValues(
                        this@ReviewSelectionActivity,
                        videolist,
                        "videos",
                        this
                    )
                    videosadapter?.notifyDataSetChanged()
                } else {
                    binding?.videolayout?.visibility = View.GONE
                }

                if (audioslist.size > 0) {
                    binding?.musiclayout?.visibility = View.VISIBLE
                    audiosadapter?.setValues(
                        this@ReviewSelectionActivity,
                        audioslist,
                        "audios",
                        this
                    )
                    audiosadapter?.notifyDataSetChanged()
                } else {
                    binding?.musiclayout?.visibility = View.GONE
                }

                if (docslist.size > 0) {
                    binding?.docslayout?.visibility = View.VISIBLE
                    docsadapter?.setValues(this@ReviewSelectionActivity, docslist, "docs", this)
                    docsadapter?.notifyDataSetChanged()
                } else {
                    binding?.docslayout?.visibility = View.GONE
                }

                if (appslist.size > 0) {
                    binding?.appslayout?.visibility = View.VISIBLE
                    appsadapter?.setValues(this@ReviewSelectionActivity, appslist, "apps", this)
                    appsadapter?.notifyDataSetChanged()
                } else {
                    binding?.appslayout?.visibility = View.GONE
                }
            }
        })
    }

    fun setuprecyclerView() {

        binding?.picturesrv?.let {
            it.layoutManager = GridLayoutManager(this, 3)
            imagesadapter = SelectionReviewAdapter()
            it.adapter = imagesadapter
        }

        binding?.videorv?.let {
            it.layoutManager = GridLayoutManager(this, 3)
            videosadapter = SelectionReviewAdapter()
            it.adapter = imagesadapter
        }

        binding?.musicrv?.let {
            it.layoutManager = GridLayoutManager(this, 3)
            audiosadapter = SelectionReviewAdapter()
            it.adapter = imagesadapter
        }

        binding?.docsrv?.let {
            it.layoutManager = GridLayoutManager(this, 3)
            docsadapter = SelectionReviewAdapter()
            it.adapter = imagesadapter
        }

        binding?.appsrv?.let {
            it.layoutManager = GridLayoutManager(this, 3)
            appsadapter = SelectionReviewAdapter()
            it.adapter = imagesadapter
        }
    }

    override fun fileselectionitem(listofselection: ArrayList<FileInfo>) {
    }
}