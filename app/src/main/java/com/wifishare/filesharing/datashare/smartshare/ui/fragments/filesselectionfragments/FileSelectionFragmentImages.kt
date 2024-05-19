package com.wifishare.filesharing.datashare.smartshare.ui.fragments.filesselectionfragments

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.adapters.FilesSelectionVideosAdapter
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentFileSelectionOneBinding
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.interfaces.FileSelectionItem
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel.Companion.imgFile
import com.wifishare.filesharing.datashare.smartshare.ui.activities.FilesSelectionActivity
import com.wifishare.filesharing.datashare.smartshare.util.executeAsyncTask
import kotlinx.coroutines.GlobalScope

class FileSelectionFragmentImages : Fragment(), FileSelectionItem {

    private var binding : FragmentFileSelectionOneBinding? = null
    private var adapter : FilesSelectionVideosAdapter? = null
    private var selectionclick = true
    private var isselected = false
    private var isallselected = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileSelectionOneBinding.inflate(layoutInflater , container , false)

        binding?.recyclerView?.let{
            it.layoutManager = GridLayoutManager(requireContext() , 3)
            adapter= FilesSelectionVideosAdapter()
            it.adapter = adapter
        }
        imgFile?.let { adapter?.setValues(requireContext() , it, "images" , this) }
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

                BackgroundViewModel?.imgFile?.forEach {
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

    override fun fileselectionitem(listofselection : ArrayList<FileInfo>) {
        if (listofselection.size == imgFile?.size) {
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

        if (FilesSelectionActivity.selectedFilesList.size > 0){
            FilesSelectionActivity.fileselectionbinding?.selectionLayout?.visibility = View.VISIBLE
            FilesSelectionActivity.fileselectionbinding?.numberoffilesTxt?.text = "(${FilesSelectionActivity.selectedFilesList.size})"
        }
        else{
            FilesSelectionActivity.fileselectionbinding?.selectionLayout?.visibility = View.GONE
        }
    }

}