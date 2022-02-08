package com.aemerse.muserse.helper

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.aemerse.muserse.ui.AlbumActivity
import com.aemerse.muserse.ui.MainActivity
import com.aemerse.muserse.model.Song
import com.aemerse.muserse.R
import com.aemerse.muserse.helper.MyAlbumAdapter.Companion.changeCoverAlbum
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changTextArtist
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changTextTitle
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changeCover
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.mediaPlayer
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.songe

class MyMusicAlbumAd (context: Context, myListSong: ArrayList<Song>) : BaseAdapter()  {

    companion object {
        var TextTrack: TextView? = null
        var ItemMusic: LinearLayout? = null
        var myListSongAlbum = ArrayList<Song>()
    }

    private val mContext: Context

    init {
        myListSongAlbum = myListSong
        mContext = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutInflate = LayoutInflater.from(mContext)
        val myView = layoutInflate.inflate(R.layout.music_album_item,parent,false)
        val song = myListSongAlbum[position]
//        val image = myListSongAlbum[position].songUrl?.let { getSongArt(it) }

        TextTrack = myView.findViewById(R.id.textNameTrack)
        ItemMusic = myView.findViewById(R.id.PlayAlbum)
        TextTrack?.text = song.title

        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer()

        ItemMusic?.setOnClickListener {
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(song.songUrl)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

            myView.findViewById<TextView>(R.id.textNameTrack).setTextColor(Color.parseColor("#13f8d1"))
            myView.findViewById<TextView>(R.id.textNameTrack).isSelected = true
            songe = song

            changTextTitle = myView.findViewById<TextView>(R.id.textNameTrack).text.toString()
            changTextArtist = AlbumActivity.binding.textArtistAlbum.text.toString()
            changeCover = changeCoverAlbum

            MainActivity.binding.navarLayout.visibility = View.VISIBLE
            MainActivity.binding.textViewTitleN.text = changTextTitle
            MainActivity.binding.textViewArtistN.text = changTextArtist
            MainActivity.binding.imageViewPlayN.setImageResource(R.drawable.ic_round_pause)
        }
        return myView

    }

    override fun getItem(position: Int): Any {
        return myListSongAlbum[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return myListSongAlbum.size
    }

//    private fun getSongArt(uri: String): ByteArray? {
//        val retrever = MediaMetadataRetriever()
//        retrever.setDataSource(uri)
//        val art: ByteArray? = retrever.embeddedPicture
//        retrever.release()
//        return art
//    }

}
