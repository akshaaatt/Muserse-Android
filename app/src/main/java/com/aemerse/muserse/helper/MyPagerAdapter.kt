package com.aemerse.muserse.helper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.aemerse.muserse.ui.controllers.FragmentFavourites
import com.aemerse.muserse.ui.controllers.FragmentTracks
import com.aemerse.muserse.ui.controllers.FragmentFolders
import com.aemerse.muserse.ui.controllers.FragmentAlbums

class MyPagerAdapter (fm : FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0-> FragmentTracks()
            1-> FragmentAlbums()
            2-> FragmentFolders()
            else-> return FragmentFavourites()
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position){
            0-> "Tracks"
            1-> "Albums"
            2-> "Folders"
            else-> return ""
        }
    }
}