package com.wifishare.filesharing.datashare.smartshare.ui.fragments.historyfragments

import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.adapters.DataClickItem
import com.wifishare.filesharing.datashare.smartshare.adapters.HistoryVideosAdapter
import com.wifishare.filesharing.datashare.smartshare.databinding.FragmentVideosHistoryBinding
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.interfaces.FileSelectionItem
import com.wifishare.filesharing.datashare.smartshare.model.BackgroundViewModel
import com.wifishare.filesharing.datashare.smartshare.model.HistoryPageViewModel
import com.wifishare.filesharing.datashare.smartshare.ui.activities.FilesSelectionActivity
import java.io.File
import java.util.Objects


class VideosHistoryFragment : Fragment(), FileSelectionItem , DataClickItem {

    private var binding: FragmentVideosHistoryBinding? = null
    private var adapater: HistoryVideosAdapter? = null
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
        binding = FragmentVideosHistoryBinding.inflate(layoutInflater, container, false)

        pageViewModel = ViewModelProvider(this)[HistoryPageViewModel::class.java]
        pageViewModel.setIndex(2)

        adapater = HistoryVideosAdapter()
        deletedialog = Dialog(requireContext())


        binding?.selectBtn?.setOnClickListener {
            if (selectionclick) {
                selectionclick = false
                adapater?.selectall()
            } else {
                selectionclick = true
                adapater?.unselectall()
            }
        }


        binding?.deleteBtn?.setOnClickListener {
            if (listofdeletefiles.isNotEmpty()) {
                showDeleteDialog()
            }
        }

        binding?.recyclerView?.let {
            it.layoutManager = GridLayoutManager(requireContext(), 3)
            it.adapter = adapater
        }


        pageViewModel.files.observe(viewLifecycleOwner) {
            listofvideos.clear()
            listofvideos.addAll(it)
            if (it.isNotEmpty()) {
                binding?.nofilesfoundlayout?.visibility = View.GONE
                binding?.recyclerView?.visibility = View.VISIBLE
                adapater?.setValues(requireContext(), it, "videos", this , this)
                adapater?.notifyDataSetChanged()
            } else {
                binding?.nofilesfoundlayout?.visibility = View.VISIBLE
                binding?.recyclerView?.visibility = View.GONE
            }
        }
        pageViewModel.loadFiles(requireActivity())

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

    override fun dataclickitem(path: String, type: String) {
        openVideo(path)
    }


    private fun openVideo(path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireActivity().packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "video/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            // Handle the case where no app can handle the intent
            // For example, you can show a Toast or a Snackbar
            // Toast.makeText(this, "No application available to view the video", Toast.LENGTH_LONG).show()
        }
    }

}