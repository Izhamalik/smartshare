package com.wifishare.filesharing.datashare.smartshare.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.interfaces.FileSelectionItem
import com.wifishare.filesharing.datashare.smartshare.ui.activities.FilesSelectionActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SelectionActivity

class FilesSelectionOthersAdapter : RecyclerView.Adapter<FilesSelectionOthersAdapter.ViewHolder>() {

    private var itemList : List<FileInfo> = listOf()
    private var context : Context? = null
    private var type = ""
    lateinit var fileSelectionItem: FileSelectionItem
    private var arrayList : ArrayList<FileInfo> = arrayListOf()

    fun setValues(context: Context , itemlist : List<FileInfo> , type : String , fileSelectionItem: FileSelectionItem) {
        this.itemList = itemlist
        this.context = context
        this.type = type
        this.fileSelectionItem = fileSelectionItem
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_selectionothers, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (type == "music"){
            holder.imageHolder.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_musicad) })
        }
        else{
            holder.imageHolder.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_pdf) })
        }

        if (FilesSelectionActivity.selectedFilesList.contains(itemList[position])){
            holder.checkImage.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_selected) })
        }
        else {

            holder.checkImage.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_unselected) })
        }

        holder.fileName.text = itemList[position].name
        holder.fileSize.text = bytesToHumanReadable(itemList[position].size)

        holder?.itemView?.setOnClickListener {
            if (FilesSelectionActivity.selectedFilesList.contains(itemList[position])){
                FilesSelectionActivity.selectedFilesList.remove(itemList[position])
                arrayList.remove(itemList[position])
                Controller.delFileInfo(itemList[position])
            }
            else {
                FilesSelectionActivity.selectedFilesList.add(itemList[position])
                Controller.addFileInfo(itemList[position])
            }

            if (FilesSelectionActivity.selectedFilesList.contains(itemList[position])){
                holder.checkImage.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_selected) })
                arrayList.add(itemList[position])
            }
            else {

                holder.checkImage.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_unselected) })
            }
            fileSelectionItem.fileselectionitem(arrayList)

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageHolder = itemView.findViewById<ImageView>(R.id.fileimageHolder)
        var checkImage = itemView.findViewById<ImageView>(R.id.checkImage)
        var fileName = itemView.findViewById<TextView>(R.id.fileName)
        var fileSize = itemView.findViewById<TextView>(R.id.fileSize)

    }


    fun bytesToHumanReadable(bytes: Long): String {
        val kilobytes = bytes / 1024.0
        val megabytes = kilobytes / 1024.0

        return when {
            megabytes >= 1 -> String.format("%.2f MB", megabytes)
            kilobytes >= 1 -> String.format("%.2f KB", kilobytes)
            else -> String.format("%d Bytes", bytes)
        }
    }


    fun selectall(){
        arrayList.clear()
        arrayList.addAll(itemList)
        itemList.forEach {
            if (!FilesSelectionActivity.selectedFilesList.contains(it)) {
                FilesSelectionActivity.selectedFilesList.add(it)
                if (!Controller.mFileInfoMap.contains(it)){
                    Controller.addFileInfo(it)
                }
            }
        }
        fileSelectionItem.fileselectionitem(arrayList)
        notifyDataSetChanged()
    }

    fun unselectall(){
        arrayList.clear()
        itemList.forEach {
            if (FilesSelectionActivity.selectedFilesList.contains(it)) {
                FilesSelectionActivity.selectedFilesList.remove(it)
                if (Controller.mFileInfoMap.contains(it)){
                    Controller.delFileInfo(it)
                }
            }
        }
        fileSelectionItem.fileselectionitem(arrayList)
        notifyDataSetChanged()
    }

}

