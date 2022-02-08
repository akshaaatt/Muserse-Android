package com.aemerse.muserse.helper

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.aemerse.muserse.model.Album
import com.aemerse.muserse.R

class MyAlbumAdapter(val context: Context, private val albums: ArrayList<Album>, private val itemClicked: (Album) -> Unit) : RecyclerView.Adapter<MyAlbumAdapter.AlbumHolder>() {

    companion object {
        var myAlbumSong = ArrayList<Album>()
        var changTextAlbum = "Title"
        var changTextArtistAlbum = "Artist"
        var changeCoverAlbum: ByteArray? = null
    }

    private val mContext: Context

    init {
        myAlbumSong = albums
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.allbum_item, parent, false)
        return AlbumHolder(view, itemClicked)
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: MyAlbumAdapter.AlbumHolder, position: Int) {
        return holder.bindAlbum(albums[position], position)
    }

    inner class AlbumHolder(itemView: View?, val itemClicked: (Album) -> Unit) : RecyclerView.ViewHolder(itemView!!) {
        private var setAlbumTwo = itemView?.findViewById<ImageView>(R.id.imageViewCoverAllbum2)
        private var albumTitle = itemView?.findViewById<TextView>(R.id.textViewTitleAllbum)
        private var albumArtist = itemView?.findViewById<TextView>(R.id.textViewArtistAllbum)

        fun bindAlbum(album:Album, position: Int) {
            albumTitle?.text = album.title
            albumArtist?.text = album.artist

            //cover
            val image = getAlbumArt(albums[position].albumUrl)
            when {
                image != null -> {
                    setAlbumTwo?.let {
                        Glide.with(mContext).asBitmap()
                            .load(image)
                            .into(it)
                    }
                }
                else -> {
                    setAlbumTwo?.let {
                        Glide.with(mContext)
                            .load(R.drawable.ic_baseline_headset_24)
                            .into(it)
                    }
                }
            }

            itemView.setOnClickListener { itemClicked(album)
                changTextAlbum = albumTitle?.text.toString()
                changTextArtistAlbum = albumArtist?.text.toString()
                changeCoverAlbum = image
            }
        }
    }

    //cover
    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art: ByteArray? = retriever.embeddedPicture
        retriever.release()
        return art
    }
}