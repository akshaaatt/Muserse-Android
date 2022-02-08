package com.aemerse.muserse.ui

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aemerse.muserse.R
import com.aemerse.muserse.databinding.ActivitySearchBinding
import com.aemerse.muserse.helper.MySearchAdapter
import com.aemerse.muserse.model.Song
import java.util.*

class SearchActivity : AppCompatActivity() {

    private var listOfSearches = ArrayList<Song>()
    private lateinit var recyclerSearch: RecyclerView
    private var adapterSearch = MySearchAdapter(this, listOfSearches)
    private lateinit var mLayoutManager:RecyclerView.LayoutManager
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backSearch.setOnClickListener {
            onBackPressed()
            animationBack()
        }

        val editTextSearch = findViewById<EditText>(R.id.textBoxSearch)
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
                adapterSearch.notifyDataSetChanged()

                when {
                    s.count() >= 1 -> {
                        binding.layoutSongs.visibility = VISIBLE
                        binding.layoutSearch.visibility = GONE
                    }
                    s.count() < 1 -> {
                        binding.layoutSongs.visibility = GONE
                        binding.layoutSearch.visibility = VISIBLE
                    }
                }
            }
        })

        buildRecyclerView()

        recyclerSearch = findViewById(R.id.recyclerSearch)
        recyclerSearch.adapter = adapterSearch

        val allSearch = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = this.contentResolver.query(allSearch, null, null, null, sortOrder)

        if (cursor != null) {
            when {
                cursor.moveToFirst() -> {
                    do {
                        val songURL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                        val songDesc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                        val songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                        val cover = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK))

                        try {
                            listOfSearches.add(Song(songName, songDesc, songURL, cover))
                        } catch (e:Exception){}
                    }
                    while (cursor.moveToNext())
                }
            }
            cursor.close()

            recyclerSearch.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(this@SearchActivity, 1)
            }
        }
    }

    //SEARCH
    private fun filter(text: String) {
        val filteredList: ArrayList<Song> = ArrayList()
        for (item in listOfSearches) {
            when {
                item.title
                    .lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault())) || item.desc.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault())) -> filteredList.add(item)
            }
        }
        adapterSearch.filterList(filteredList)
    }

    private fun animationBack() {
        val animScale = AnimationUtils.loadAnimation(this, R.anim.anim_backward)
        binding.backSearch.startAnimation(animScale)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun buildRecyclerView() {
        recyclerSearch = findViewById(R.id.recyclerSearch)
        recyclerSearch.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        adapterSearch = MySearchAdapter(this,listOfSearches)
        recyclerSearch.layoutManager = mLayoutManager
        recyclerSearch.adapter = adapterSearch
    }
}
