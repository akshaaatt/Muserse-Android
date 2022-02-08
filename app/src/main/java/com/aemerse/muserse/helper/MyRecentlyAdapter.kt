package com.aemerse.muserse.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.aemerse.muserse.ui.MainActivity
import com.aemerse.muserse.model.Song
import com.aemerse.muserse.R
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changTextArtist
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changTextTitle
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changeCover
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.mediaPlayer
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.songe
import kotlinx.android.synthetic.main.recent_item.view.*
import kotlin.collections.ArrayList

class MyRecentlyAdapter(context: Context, myListSong: ArrayList<Song>) : RecyclerView.Adapter<MyRecentlyAdapter.SongHolder>() {

    companion object {
        var myListSong = ArrayList<Song>()
    }

    private val mContext: Context
    init {
        MyRecentlyAdapter.myListSong = myListSong
        myListSong.reverse()
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.recent_item, parent, false)
        return SongHolder(view)
    }

    override fun getItemCount(): Int {
        return myListSong.size
    }

    override fun onBindViewHolder(holder: MyRecentlyAdapter.SongHolder, position: Int) {
        return holder.bindMusic(myListSong[position], position)
    }

    inner class SongHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {
        private val setCover = itemView?.findViewById<ImageView>(R.id.coverMusic)
        private val songTitle = itemView?.findViewById<TextView>(R.id.textViewTitleRecent)
        private val songArtist = itemView?.findViewById<TextView>(R.id.textViewDescRecent)
        private val itemMusic = itemView?.findViewById<ConstraintLayout>(R.id.PlayMusic)

        @SuppressLint("ResourceType")
        fun bindMusic(song: Song, position: Int) {
            songTitle?.text = song.title
            songArtist?.text = song.desc

            //cover
            val image = getSongArt(myListSong[position].songUrl)
            if(image != null) {
                setCover?.let {
                    Glide.with(mContext).asBitmap()
                        .load(image)
                        .into(it)
                }
            }
            else {
                setCover?.let {
                    Glide.with(mContext)
                        .load(R.drawable.ic_baseline_headset_24)
                        .into(it)
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

                itemView.textViewDescRecent.setTextColor(Color.parseColor("#00d6b3"))
                itemView.textViewTitleRecent.setTextColor(Color.parseColor("#13f8d1"))
                changTextTitle = itemView.textViewTitleRecent.text.toString()
                changTextArtist = itemView.textViewDescRecent.text.toString()
                changeCover = image

                MainActivity.binding.navarLayout.visibility = VISIBLE
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