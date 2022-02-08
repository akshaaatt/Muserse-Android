package com.aemerse.muserse.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aemerse.muserse.R
import com.aemerse.muserse.databinding.ActivityMainBinding
import com.aemerse.muserse.helper.MyPagerAdapter
import com.aemerse.muserse.helper.MyTrackAdapter
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changTextArtist
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changTextTitle
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changeCover
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.currentSongIndex
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.mediaPlayer
import com.aemerse.muserse.model.Song
import com.aemerse.muserse.service.DataService
import com.aemerse.muserse.service.MediaPlayerService
import com.aemerse.muserse.service.MediaPlayerService.LocalBinder
import com.bumptech.glide.Glide
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView

class MainActivity : AppCompatActivity() {

    var adapter: MyTrackAdapter? = null
    private var player: MediaPlayerService? = null
    var serviceBound = false

    companion object {
        const val PERMISSION_REQUEST_CODE = 12

        var songinfo:Song? = null
        var ongoingCall = false
        var phoneStateListener: PhoneStateListener? = null
        var telephonyManager: TelephonyManager? = null
        lateinit var binding: ActivityMainBinding
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callStateListener()

        binding.textViewTitleN.text = changTextTitle
        binding.textViewArtistN.text = changTextArtist
        binding.textViewTitleN.isSelected =true
        binding.textViewArtistN.isSelected =true

        when {
            changeCover != null -> {
                binding.coverNavar.let { it1 -> Glide.with(this).load(changeCover).into(it1) }
            }
            else -> {
                binding.coverNavar.let { it1 ->
                    Glide.with(this).load(R.drawable.ic_baseline_headset_24).into(it1)
                }
            }
        }

        when (mediaPlayer) {
            null -> binding.imageViewPlayN.setImageResource(R.drawable.ic_round_play)
            else -> binding.imageViewPlayN.setImageResource(R.drawable.ic_round_pause)
        }

        //strip Control music play button
        binding.imageViewPlayN.setOnClickListener {
            animationZoom(binding.imageViewPlayN)
            when {
                mediaPlayer!!.isPlaying -> {
                    binding.imageViewPlayN.setImageResource(R.drawable.ic_round_play)
                    mediaPlayer!!.pause()
                }
                mediaPlayer!=null -> {
                    binding.imageViewPlayN.setImageResource(R.drawable.ic_round_pause)
                    mediaPlayer!!.start()
                }
            }
        }

        //strip Control music preview btn
        binding.imageViewPreviewN.setOnClickListener {
            animationBlur(binding.imageViewPreviewN)
            when {
                currentSongIndex>0 -> {
                    DataService.playSong(currentSongIndex - 1)
                    currentSongIndex -= 1
                }
                else -> {
                    DataService.playSong(MyTrackAdapter.myListTrack.size - 1)
                    currentSongIndex = MyTrackAdapter.myListTrack.size -1
                }
            }
        }

        //strip Control music next button
        binding.imageViewNextN.setOnClickListener {
            animationBlur(binding.imageViewNextN)
        }

        binding.navarLayout.setOnClickListener {
            when {
                mediaPlayer != null -> {
                    val intent = Intent(this, PlayActivity::class.java)
                    intent.putExtra("position", PlayActivity.position)
                    startActivity(intent)
                }
                else -> Toast.makeText(this,"click on a Song please",Toast.LENGTH_SHORT).show()
            }
        }

        //services
        songinfo?.songUrl?.let { playAudio(it) }

        // Request allow
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    != PackageManager.PERMISSION_GRANTED -> ActivityCompat.requestPermissions(this,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
            else -> loadData()
        }

    }

    private fun loadData() {
        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = fragmentAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.getTabAt(3)!!.setIcon(R.drawable.favourite_selected)
    }

    //  Requested allow 2
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                loadData()
                TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.appBarSearch),
                        "Search bar", "Find the music you want")
                        .tintTarget(true)
                        .outerCircleColor(R.color.colorRed)
                )
            }
        }
    }

    //SEARCH
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
             R.id.appBarSearch -> {
                 val intent = Intent(this, SearchActivity::class.java)
                 startActivity(intent)
             }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?):Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //pause music when calling
    private fun callStateListener() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_RINGING ->
                        if (mediaPlayer != null) {
                            mediaPlayer!!.pause()
                            ongoingCall = true
                        }
                    TelephonyManager.CALL_STATE_IDLE ->
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false
                                mediaPlayer!!.start()
                            }
                        }
                }

            }
        }

        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }


    //services
    private var serviceConnection: ServiceConnection? = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocalBinder
            player = binder.service
            serviceBound = true
            Toast.makeText(this@MainActivity, "Service Bound", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }

    private fun playAudio(media: String) {
        //Check in service is active
        when {
            !serviceBound -> {
                val playerIntent = Intent(this, MediaPlayerService::class.java)
                playerIntent.putExtra("media", media)
                startService(playerIntent)
                bindService(playerIntent, serviceConnection!!, BIND_AUTO_CREATE)
            }
            else -> {
                //Service is active
                //Send media with BroadcastReceiver
            }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putBoolean("ServiceState", serviceBound)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("ServiceState")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection!!)
            //service is active
            player!!.stopSelf()
        }
    }

    //animation
    private fun animationZoom(imageView: ImageView) {
        val animScale = AnimationUtils.loadAnimation(this, R.anim.anim_pause)
        imageView.startAnimation(animScale)
    }

    private fun animationBlur(imageView: ImageView) {
        val animScale = AnimationUtils.loadAnimation(this, R.anim.anim_backward)
        imageView.startAnimation(animScale)
    }
}



