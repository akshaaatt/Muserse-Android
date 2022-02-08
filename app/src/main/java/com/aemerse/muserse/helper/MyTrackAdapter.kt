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
import com.aemerse.muserse.model.Song
import com.aemerse.muserse.ui.MainActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.track_item.view.*

class MyTrackAdapter(context: Context, myListSong: ArrayList<Song>) : RecyclerView.Adapter<MyTrackAdapter.SongHolder>() {

    companion object {
        var myListTrack = ArrayList<Song>()
        var adapter: MyTrackAdapter? = null
        var mediaPlayer: MediaPlayer? = null
        var songe:Song? = null
        var changTextTitle = "Title"
        var changTextArtist = "Artist"
        var changeCover:ByteArray? = null
        var currentSongIndex = 0
    }

    private val mContext: Context
    init {
        myListTrack = myListSong
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.track_item, parent, false)
        return SongHolder(view)
    }

    override fun getItemCount(): Int {
        return myListTrack.size
    }

    override fun onBindViewHolder(holder: MyTrackAdapter.SongHolder, position: Int) {
        return holder.bindMusic(myListTrack[position])
    }

    inner class SongHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {

        private val layoutCover = itemView?.findViewById<ConstraintLayout>(R.id.layoutCover)
        private val setCover = itemView?.findViewById<ImageView>(R.id.coverMusic)
        private val songTitle = itemView?.findViewById<TextView>(R.id.textViewTitle)
        private val songArtist = itemView?.findViewById<TextView>(R.id.textViewDesc)
        private val itemMusic = itemView?.findViewById<ConstraintLayout>(R.id.PlayMusic)

        fun bindMusic(song:Song) {
            songTitle?.text = song.title
            songArtist?.text = song.desc

            //cover
            val image = getSongArt(myListTrack[position].songUrl)
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
                animationCover()

                itemView.textViewDesc.setTextColor(Color.parseColor("#00d6b3"))
                itemView.textViewTitle.setTextColor(Color.parseColor("#13f8d1"))
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
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art: ByteArray? = retriever.embeddedPicture
        retriever.release()
        return art
    }
}