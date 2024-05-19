package com.wifishare.filesharing.datashare.smartshare.ui.fragments.filesselectionfragments

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.adapters.FileSelectionApkAdapter
import com.wifishare.filesharing.datashare.smartshare.adapters.FilesSelectionOthersAdapter
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentFileSelectionApkBinding
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.interfaces.FileSelectionItem
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel
import com.wifishare.filesharing.datashare.smartshare.ui.activities.FilesSelectionActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SelectionActivity
import com.wifishare.filesharing.datashare.smartshare.util.clickWithThrottle
import com.wifishare.filesharing.datashare.smartshare.util.executeAsyncTask
import kotlinx.coroutines.GlobalScope

class FileSelectionFragmentApk : Fragment(), FileSelectionItem {

    private var binding: FragmentFileSelectionApkBinding? = null
    private var adapter: FileSelectionApkAdapter? = null
    private var selectionclick = true
    private var isselected = false
    private var isallselected = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileSelectionApkBinding.inflate(layoutInflater, container, false)

        binding?.recyclerView?.let {
            it.layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = FileSelectionApkAdapter()
            it.adapter = adapter
        }
        BackgroundViewModel.apkFile?.let { adapter?.setValues(requireContext(), it, "docs", this) }
        adapter?.notifyDataSetChanged()

        binding?.selectBtn?.setOnClickListener {
            if (selectionclick) {
                selectionclick = false
                adapter?.selectall()
            }
            else{
                selectionclick = true
                adapter?.unselectall()
            }
        }

        GlobalScope.executeAsyncTask(
            onPreExecute = {

        }, doInBackground = {

            BackgroundViewModel?.apkFile?.forEach {
                if (FilesSelectionActivity.selectedFilesList.contains(it)){
                    isselected = true
                }
                else{
                    isallselected = false
                }
            }
        }, onPostExecute = {
            requireActivity().runOnUiThread {
                if (isselected)
                    binding?.selectionlayout?.visibility  = View.VISIBLE

                if (isallselected){
                    selectionclick = false
                    val newColor = ContextCompat.getColor(requireContext(), R.color.mainthemecolor)
                    binding?.selectImg?.setColorFilter(newColor, PorterDuff.Mode.SRC_IN)
                    binding?.selectTxt?.text = "All Selected"
                }
            }
        })


        return binding?.root
    }

    override fun fileselectionitem(listofselection: ArrayList<FileInfo>) {
        if (listofselection.size == BackgroundViewModel.apkFile?.size) {
            selectionclick = false
            val newColor = ContextCompat.getColor(requireContext(), R.color.mainthemecolor)
            binding?.selectImg?.setColorFilter(newColor, PorterDuff.Mode.SRC_IN)
            binding?.selectTxt?.text = "All Selected"

        } else {
            selectionclick = true
            val newColor = ContextCompat.getColor(requireContext(), R.color.dark_grey)
            binding?.selectImg?.setColorFilter(newColor, PorterDuff.Mode.SRC_IN)
            binding?.selectTxt?.text = "Select All"
        }

        if (listofselection.size > 0)
            binding?.selectionlayout?.visibility = View.VISIBLE
        else
            binding?.selectionlayout?.visibility = View.GONE

        if (FilesSelectionActivity.selectedFilesList.size > 0) {
            FilesSelectionActivity.fileselectionbinding?.selectionLayout?.visibility = View.VISIBLE
            FilesSelectionActivity.fileselectionbinding?.numberoffilesTxt?.text =
                "(${FilesSelectionActivity.selectedFilesList.size})"
        } else {
            FilesSelectionActivity.fileselectionbinding?.selectionLayout?.visibility = View.GONE
        }
    }

}