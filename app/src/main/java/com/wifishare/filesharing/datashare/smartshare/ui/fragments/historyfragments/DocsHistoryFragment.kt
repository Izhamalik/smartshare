package com.wifishare.filesharing.datashare.smartshare.ui.fragments.historyfragments

import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.adapters.HistoryOtherAdapter
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentDocsHistoryBinding
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.interfaces.FileSelectionItem
import com.wifishare.filesharing.datashare.smartshare.model.HistoryPageViewModel
import java.util.Objects

class DocsHistoryFragment : Fragment(), FileSelectionItem {

    private var binding: FragmentDocsHistoryBinding? = null
    private var adapater: HistoryOtherAdapter? = null
    private lateinit var pageViewModel: HistoryPageViewModel
    private var selectionclick = true
    private var isselected = false
    private var isallselected = true
    private var listofvideos = arrayListOf<FileInfo>()
    private var deletedialog: Dialog? = null
    private var listofdeletefiles = arrayListOf<FileInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDocsHistoryBinding.inflate(layoutInflater, container, false)

        adapater = HistoryOtherAdapter()
        deletedialog = Dialog(requireContext())

        pageViewModel = ViewModelProvider(this)[HistoryPageViewModel::class.java]
        pageViewModel.setIndex(4)

        binding?.recyclerView?.let {
            it.layoutManager = GridLayoutManager(requireContext(), 3)
            it.adapter = adapater
        }

        binding?.deleteBtn?.setOnClickListener {
            if (listofdeletefiles.isNotEmpty()) {
                showDeleteDialog()
            }
        }



        pageViewModel.files.observe(viewLifecycleOwner) {
            listofvideos.clear()
            listofvideos.addAll(it)
            if (it.isNotEmpty()) {
                binding?.nofilesfoundlayout?.visibility = View.GONE
                binding?.recyclerView?.visibility = View.VISIBLE
                adapater?.setValues(requireContext(), it, "docs", this)
                adapater?.notifyDataSetChanged()
            }
            else{
                binding?.nofilesfoundlayout?.visibility = View.VISIBLE
                binding?.recyclerView?.visibility = View.GONE
            }
        }
        pageViewModel.loadFiles(requireActivity())




        binding?.selectBtn?.setOnClickListener {
            if (selectionclick) {
                selectionclick = false
                adapater?.selectall()
            } else {
                selectionclick = true
                adapater?.unselectall()
            }
        }

        return binding?.root
    }


    override fun fileselectionitem(listofselection: ArrayList<FileInfo>) {

        listofdeletefiles.clear()
        listofdeletefiles.addAll(listofselection)

        if (listofselection.size == 0) {
            adapater?.longclickdisable()
        }


        if (listofselection.size == listofvideos.size) {
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
    }


    private fun showDeleteDialog() {
        Objects.requireNonNull<Window>(deletedialog?.window)
            .setBackgroundDrawableResource(android.R.color.transparent)
        deletedialog?.setContentView(R.layout.dialog_delete)
        deletedialog?.setCancelable(false)
        deletedialog?.show()

        var cancelBtn = deletedialog?.findViewById<TextView>(R.id.cancelBtn)
        var deleteBtn = deletedialog?.findViewById<TextView>(R.id.deletBtn)

        cancelBtn?.setOnClickListener {
            deletedialog?.dismiss()
        }

        deleteBtn?.setOnClickListener {
            deletedialog?.dismiss()
            pageViewModel.deleteFiles(requireActivity() , listofdeletefiles)
            binding?.selectionlayout?.visibility = View.GONE
        }
    }


}