package com.aemerse.muserse.ui

import android.os.Bundle
import android.provider.MediaStore
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.aemerse.muserse.R
import com.aemerse.muserse.databinding.ActivityAlbumBinding
import com.aemerse.muserse.helper.MyAlbumAdapter.Companion.changTextAlbum
import com.aemerse.muserse.helper.MyAlbumAdapter.Companion.changTextArtistAlbum
import com.aemerse.muserse.helper.MyAlbumAdapter.Companion.changeCoverAlbum
import com.aemerse.muserse.helper.MyMusicAlbumAd
import com.aemerse.muserse.model.Song
import com.bumptech.glide.Glide

class AlbumActivity : AppCompatActivity() {

    companion object {
        lateinit var binding: ActivityAlbumBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textAlbumName.text = changTextAlbum
        binding.textArtistAlbum.text = changTextArtistAlbum

        setCoverAlbumActivity()

        val allSongAlbum = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = this.contentResolver.query(allSongAlbum,null,selection,null,sortOrder)
        val listOfMusicAlbum = ArrayList<Song>()

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val songAlbumURL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songAlbumDesc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songAlbumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val cover = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                    try {
                        listOfMusicAlbum.add(Song(songAlbumName, songAlbumDesc, songAlbumURL, cover))
                    }
                    catch (e:Exception){ }

                }while (cursor.moveToNext())
            }
            cursor.close()

            val songAlbumList = findViewById<ListView>(R.id.listViewAlbum)
            songAlbumList.adapter = MyMusicAlbumAd(this.applicationContext, listOfMusicAlbum)
        }
    }

    private fun setCoverAlbumActivity() {
        when {
            changeCoverAlbum != null -> {
                Glide.with(this).asBitmap()
                    .load(changeCoverAlbum)
                    .into(binding.imageViewCoverAlbum)
            }
            else -> {
                Glide.with(this)
                    .load(R.drawable.ic_baseline_headset_24)
                    .into(binding.imageViewCoverAlbum)
            }
        }
    }
}