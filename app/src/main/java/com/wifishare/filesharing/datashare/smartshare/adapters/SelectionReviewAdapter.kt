package com.wifishare.filesharing.datashare.smartshare.adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.interfaces.FileSelectionItem
import com.wifishare.filesharing.datashare.smartshare.ui.activities.FilesSelectionActivity

class SelectionReviewAdapter : RecyclerView.Adapter<SelectionReviewAdapter.ViewHolder>() {

    private var itemList: ArrayList<FileInfo> = arrayListOf()
    private var context: Context? = null
    lateinit var fileSelectionItem: FileSelectionItem
    private var type = ""

    fun setValues(
        context: Context,
        itemlist: List<FileInfo>,
        type: String,
        fileSelectionItem: FileSelectionItem
    ) {
        this.itemList.clear()
        this.itemList.addAll( itemlist)
        this.context = context
        this.type = type
        this.fileSelectionItem = fileSelectionItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.rv_selectionreview, parent, false
        )
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d("IzharMalik" , "$type")
        if (type == "images"){
            context?.let {
                Glide.with(it)
                    .load(itemList[position].filePath)
                    .into(holder.imageHolder)
            }
        }

        else if (type == "videos"){
            holder.playBtn.visibility = View.VISIBLE
            context?.let {
                Glide.with(it)
                    .load(itemList[position].filePath)
                    .into(holder.imageHolder)
            }
        }

        else if (type == "audios"){
            holder.imageHolder.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_musicad) })
        }

        else if (type == "docs") {
            holder.imageHolder.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_pdf) })
        }
        else {
            context?.let {
                Glide.with(it)
                    .load(itemList[position].appIcon)
                    .into(holder.imageHolder)
            }
        }

        holder.closeBtn.setOnClickListener {
            if (FilesSelectionActivity.selectedFilesList.contains(itemList[position]))
            {
                FilesSelectionActivity.selectedFilesList.remove(itemList[position])
                itemList.remove(itemList[position])
                notifyDataSetChanged()
            }
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageHolder = itemView.findViewById<ImageView>(R.id.imageHolderRew)
        var playBtn = itemView.findViewById<ImageView>(R.id.playBtn)
        var closeBtn = itemView.findViewById<ConstraintLayout>(R.id.closeBtn)

    }


}

