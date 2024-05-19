package com.wifishare.filesharing.datashare.smartshare.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.interfaces.FileSelectionItem
import com.wifishare.filesharing.datashare.smartshare.ui.activities.FilesSelectionActivity

class FilesSelectionVideosAdapter : RecyclerView.Adapter<FilesSelectionVideosAdapter.ViewHolder>() {

    private var itemList: List<FileInfo> = listOf()
    private var context: Context? = null
    private var type = ""
    lateinit var fileSelectionItem: FileSelectionItem
    private var arrayList : ArrayList<FileInfo> = arrayListOf()

    fun setValues(
        context: Context,
        itemlist: List<FileInfo>,
        type: String,
        fileSelectionItem: FileSelectionItem
    ) {
        this.itemList = itemlist
        this.context = context
        this.type = type
        this.fileSelectionItem = fileSelectionItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_selectionvideo, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (type == "videos") {
            holder.playBtn.visibility = View.VISIBLE
        } else {
            holder.playBtn.visibility = View.GONE
        }

        if (FilesSelectionActivity.selectedFilesList.contains(itemList[position])) {
           holder.selectedlayout.visibility = View.VISIBLE
        } else {
            holder.selectedlayout.visibility = View.GONE
        }

        context?.let {
            Glide.with(it)
                .load(itemList[position].filePath)
                .into(holder.imageHolder)
        }

        holder.imageHolder.setOnClickListener {
            if (FilesSelectionActivity.selectedFilesList.contains(itemList[position])) {
                FilesSelectionActivity.selectedFilesList.remove(itemList[position])
                if (Controller.mFileInfoMap.contains(it)){
                    Controller.delFileInfo(itemList[position])
                }
                arrayList.remove(itemList[position])
                holder.selectedlayout.visibility = View.GONE
            } else {
                FilesSelectionActivity.selectedFilesList.add(itemList[position])
                if (!Controller.mFileInfoMap.contains(it)){
                    Controller.addFileInfo(itemList[position])
                }
                arrayList.add(itemList[position])
                holder.selectedlayout.visibility = View.VISIBLE
            }

            fileSelectionItem.fileselectionitem(arrayList)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageHolder = itemView.findViewById<ImageView>(R.id.imageHolder)
        var playBtn = itemView.findViewById<ImageView>(R.id.playBtn)
        var selectedlayout = itemView.findViewById<FrameLayout>(R.id.selectedlayout)

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

