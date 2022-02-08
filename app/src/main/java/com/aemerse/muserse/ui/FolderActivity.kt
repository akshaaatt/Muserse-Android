package com.aemerse.muserse.ui

import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aemerse.muserse.R
import com.aemerse.muserse.databinding.ActivityFolderBinding
import com.aemerse.muserse.helper.MyFolderAdapter.Companion.changAddressFolder
import com.aemerse.muserse.helper.MyFolderAdapter.Companion.changNameFolder
import com.aemerse.muserse.helper.MyMusicFolderAp
import com.aemerse.muserse.model.Song

class FolderActivity : AppCompatActivity() {

    private lateinit var recyclerFolder: RecyclerView
    private lateinit var binding: ActivityFolderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textFolderName.text = changNameFolder
        binding.textAddressFolder.text = changAddressFolder

        recyclerFolder = findViewById(R.id.listMusicFolder)

        recyclerFolder.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@FolderActivity,1)
        }

        val allMusicFolder = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = this.contentResolver.query(allMusicFolder,null,null,null,sortOrder)
        val listOfMusicFolder= ArrayList<Song>()

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val songFolderURL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songFolderDesc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songFolderName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val cover = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK))

                    try {
                        listOfMusicFolder.add(Song(songFolderName, songFolderDesc, songFolderURL, cover))
                    }
                    catch (e:Exception) {}
                }
                while (cursor.moveToNext())
            }
            cursor.close()

            val songFolderList: RecyclerView = findViewById(R.id.listMusicFolder)
            songFolderList.adapter = MyMusicFolderAp(this.applicationContext, listOfMusicFolder)
        }
    }
}