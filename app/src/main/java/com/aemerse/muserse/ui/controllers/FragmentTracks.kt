package com.aemerse.muserse.ui.controllers

import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aemerse.muserse.*
import com.aemerse.muserse.helper.*
import com.aemerse.muserse.model.Song

import kotlin.collections.ArrayList

class FragmentTracks : Fragment() {

    private lateinit var recycleTrack:RecyclerView
    private lateinit var recycleRecent:RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_track, container, false)
        recycleTrack = view.findViewById(R.id.RecyclerViewTrack)
        recycleRecent = view.findViewById(R.id.RecyclerViewRecent)

        recycleTrack.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity,1)
        }

        recycleRecent.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity,1,LinearLayoutManager.HORIZONTAL,false)
        }

        //list music track
        val allSong = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = requireActivity().contentResolver.query(allSong,null,null,null,sortOrder)
        val listOfSongs = ArrayList<Song>()

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    @Suppress("DEPRECATION")
                    val songURL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songAuthor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val cover = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK))

                    try {
                        listOfSongs.add(Song(songName, songAuthor, songURL, cover))
                    }
                    catch (e:Exception){
//                        Toast.makeText(context,e.message.toString(),Toast.LENGTH_LONG).show()
                    }

                }while (cursor.moveToNext())
            }
            cursor.close()

            val songList = view.findViewById<RecyclerView>(R.id.RecyclerViewTrack)
            songList.adapter = MyTrackAdapter(requireActivity().applicationContext, listOfSongs)

        }

        //list music recent
        val recentSong = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val recentOrder = MediaStore.Audio.Media.DATE_ADDED
        val recentCursor = requireActivity().contentResolver.query(recentSong,null,null,null,recentOrder)
        val listOfRecently = ArrayList<Song>()

        if (recentCursor != null) {
            if (recentCursor.moveToFirst()) {
                do {
                    @Suppress("DEPRECATION")
                    val songURL = recentCursor.getString(recentCursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songAuthor = recentCursor.getString(recentCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songName = recentCursor.getString(recentCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val cover = recentCursor.getString(recentCursor.getColumnIndex(MediaStore.Audio.Media.TRACK))

                    try {
                        listOfRecently.add(
                            Song(
                                songName,
                                songAuthor,
                                songURL,
                                cover
                            )
                        )
                    }
                    catch (e:Exception){
//                        Toast.makeText(context,e.message.toString(),Toast.LENGTH_LONG).show()
                    }

                }while (recentCursor.moveToNext())
            }
            recentCursor.close()

            val recentlyList = view.findViewById<RecyclerView>(R.id.RecyclerViewRecent)
            recentlyList.adapter = MyRecentlyAdapter(requireActivity().applicationContext, listOfRecently)

        }
        return view
    }
}
