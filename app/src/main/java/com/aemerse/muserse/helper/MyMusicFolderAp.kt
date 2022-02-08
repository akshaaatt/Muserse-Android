package com.aemerse.muserse.helper

import android.content.Context
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aemerse.muserse.R
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changTextArtist
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changTextTitle
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changeCover
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.mediaPlayer
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.songe
import com.aemerse.muserse.model.Song
import com.aemerse.muserse.ui.MainActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.track_item.view.*

class MyMusicFolderAp(val context: Context, myListSong: ArrayList<Song>) : RecyclerView.Adapter<MyMusicFolderAp.SongHolder>() {

    companion object {
        var myListSongFolder = ArrayList<Song>()
    }

    private val mContext: Context
    init {
        myListSongFolder = myListSong
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.track_item, parent, false)
        return SongHolder(view)
    }

    override fun getItemCount(): Int {
        return myListSongFolder.size
    }

    override fun onBindViewHolder(holder: MyMusicFolderAp.SongHolder, position: Int) {
        return holder.bindMusic(myListSongFolder[position], position)
    }

    inner class SongHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        private val layoutCover = itemView?.findViewById<ConstraintLayout>(R.id.layoutCover)
        private var setCover = itemView?.findViewById<ImageView>(R.id.coverMusic)
        private var songTitle = itemView?.findViewById<TextView>(R.id.textViewTitle)
        private var songArtist = itemView?.findViewById<TextView>(R.id.textViewDesc)
        private var itemMusic = itemView?.findViewById<ConstraintLayout>(R.id.PlayMusic)

        fun bindMusic(song: Song, position: Int) {
            songTitle?.text = song.title
            songArtist?.text = song.desc

            //Cover
            val image = getSongArt(myListSongFolder[position].songUrl)
            when {
                image != null -> {
                    setCover?.let {
                        Glide.with(mContext).asBitmap()
                            .load(image)
                            .into(it)
                    }
                }
                else -> {
                    setCover?.let {
                        Glide.with(mContext)
                            .load(R.drawable.ic_baseline_headset_24)
                            .into(it)
                    }
                }
            }

            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer()

            itemView.setOnClickListener {
                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource(song.songUrl)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
                animationItem()
                animationCover()

                itemView.textViewDesc.setTextColor(Color.parseColor("#00d6b3"))
                itemView.textViewTitle.setTextColor(Color.parseColor("#13f8d1"))
                itemView.textViewDesc.isSelected = true
                itemView.textViewTitle.isSelected = true

                changTextTitle = itemView.textViewTitle.text.toString()
                changTextArtist = itemView.textViewDesc.text.toString()
                changeCover = image

                MainActivity.binding.navarLayout.visibility = View.VISIBLE
                MainActivity.binding.textViewTitleN.text = changTextTitle
                MainActivity.binding.textViewArtistN.text = changTextArtist
                MainActivity.binding.imageViewPlayN.setImageResource(R.drawable.ic_round_pause)

                when {
                    image != null -> {
                        MainActivity.binding.coverNavar.let { it1 -> Glide.with(mContext).load(image).into(it1) }
                    }
                    else -> {
                        MainActivity.binding.coverNavar.let { it1 -> Glide.with(mContext).load(R.drawable.ic_baseline_headset_24).into(it1) }
                    }
                }

                songe = song
            }
        }

        private fun animationItem() {
            val animScale = AnimationUtils.loadAnimation(mContext, R.anim.anim_pause)
            itemMusic?.startAnimation(animScale)
        }

        private fun animationCover() {
            val animScale = AnimationUtils.loadAnimation(mContext, R.anim.anim_pause)
            layoutCover?.startAnimation(animScale)
        }
    }

    //cover
    private fun getSongArt(uri: String): ByteArray? {
        val retrever = MediaMetadataRetriever()
        retrever.setDataSource(uri)
        val art: ByteArray? = retrever.embeddedPicture
        retrever.release()
        return art
    }
}