package com.aemerse.muserse.service

import com.aemerse.muserse.helper.MyTrackAdapter.Companion.mediaPlayer
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.songe

class DataService {
    companion object {
        fun playSong(songIndex: Int = 0) {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(songe?.songUrl)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        }
    }
}
