package com.aemerse.muserse.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aemerse.muserse.model.Song
import com.aemerse.muserse.R

class MyFolderAdapter(val context: Context, private val folders: ArrayList<Song>, private val itemClicked: (Song) -> Unit) : RecyclerView.Adapter<MyFolderAdapter.FolderHolder>() {

    companion object {
        var myFolderSong = ArrayList<Song>()
        var changNameFolder = "Title"
        var changAddressFolder = "Artist"
    }

    private val mContext: Context
    init {
        myFolderSong = folders
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.folder_item, parent, false)
        return FolderHolder(view, itemClicked)
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    override fun onBindViewHolder(holder: MyFolderAdapter.FolderHolder, position: Int) {
        return holder.bindFolder(folders[position])
    }

    inner class FolderHolder(itemView: View?, val itemClicked: (Song) -> Unit) : RecyclerView.ViewHolder(itemView!!) {
        private var folderTitle = itemView?.findViewById<TextView>(R.id.folderName)
        private var folderAddress = itemView?.findViewById<TextView>(R.id.folderAddres)

        fun bindFolder(song:Song) {
            folderTitle?.text = song.title
            folderAddress?.text = song.desc

            itemView.setOnClickListener {
                itemClicked(song)
                changNameFolder = folderTitle?.text.toString()
                changAddressFolder = folderAddress?.text.toString()
            }
        }
    }
}