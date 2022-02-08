package com.aemerse.muserse.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aemerse.muserse.R
import com.aemerse.muserse.database.DBManager
import com.aemerse.muserse.databinding.ActivityPlayBinding
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changTextArtist
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changTextTitle
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.changeCover
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.currentSongIndex
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.mediaPlayer
import com.aemerse.muserse.helper.MyTrackAdapter.Companion.myListTrack
import com.aemerse.muserse.service.DataService.Companion.playSong
import com.bumptech.glide.Glide
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import kotlin.random.Random

var totalTime: Int = 0

class PlayActivity : AppCompatActivity() {

    companion object{
        var seekbar:SeekBar? = null
        var Pause:ImageView? = null
        var position:Int = -1
    }
    private lateinit var binding: ActivityPlayBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCoverPlayActivity()

        seekbar = findViewById(R.id.seekBar)
        Pause = findViewById(R.id.imagePause)
        totalTime = mediaPlayer!!.duration

        // Set text Artist & Title
        binding.textViewTitle2.text = changTextTitle
        binding.textViewArtist2.text = changTextArtist

        binding.imageCreate.setOnClickListener {
            TapTargetView.showFor(
                this, TapTarget.forView(
                    findViewById(R.id.imageCreate),
                    "Not Active", "it can be used in later Updates"
                )
                    .tintTarget(true)
                    .outerCircleColor(R.color.colorTheme)
            )
        }

        var isLike = true
        binding.imageHeart.setOnClickListener {
            when {
                isLike -> {
                    binding.imageHeart.setImageResource(R.drawable.heart1)
                    isLike = false
                    animationZoom(binding.imageHeart)

                    //database
                    val dbManager = DBManager(this)
                    val values = ContentValues()
                    values.put("Title", binding.textViewTitle2.text.toString())
                    values.put("Artist", binding.textViewArtist2.text.toString())

                    values.put(
                        "Cover", Glide.with(this)
                            .asBitmap()
                            .load(R.id.coverPlayActivity)
                            .toString()
                    )

                    val ID = dbManager.insert(values)
                    when {
                        ID > 0 -> Toast.makeText(this, " Add song to Favourite list ", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this, " ERROR!! NOT Add song to Favourite list ", Toast.LENGTH_SHORT).show()
                    }

                }
                !isLike -> {
                    binding.imageHeart.setImageResource(R.drawable.heart)
                    isLike = true
                    animationZoom(binding.imageHeart)
                }
            }
        }

        //seekBar
        binding.seekBar.max = mediaPlayer!!.duration
        binding.seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser)
                        mediaPlayer!!.seekTo(progress)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) { }
                override fun onStopTrackingTouch(seekBar: SeekBar?) { }
            }
        )

        Thread {
            while (mediaPlayer != null) {
                try {
                    val msg = Message()
                    msg.what = mediaPlayer!!.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                }
                catch (e: InterruptedException) { }
            }
        }.start()

        if (mediaPlayer!!.isPlaying)
            binding.imagePause.setImageResource(R.drawable.pause)

        //btn pause
        binding.imagePause.setOnClickListener {
            when {
                mediaPlayer!!.isPlaying -> {
                    binding.imagePause.setImageResource(R.drawable.play)
                    MainActivity.binding.imageViewPlayN.setImageResource(R.drawable.ic_round_play)
                    mediaPlayer!!.pause()
                    animationZoom(binding.imagePause)
                }
                else -> {
                    binding.imagePause.setImageResource(R.drawable.pause)
                    MainActivity.binding.imageViewPlayN.setImageResource(R.drawable.ic_round_pause)
                    mediaPlayer!!.start()
                    animationZoom(binding.imagePause)
                }
            }
        }

        var isRepeat = false
        var isShuffle = false

        loadData(isRepeat, isShuffle)

        when {
            !isShuffle && !isRepeat -> {
                binding.randomButton.setImageResource(R.drawable.random)
                binding.imageTekrar.setImageResource(R.drawable.refresh)
            }
            isShuffle or !isRepeat -> {
                binding.imageTekrar.setImageResource(R.drawable.refresh1)
                isRepeat = true
                binding.randomButton.setImageResource(R.drawable.random)
                isShuffle = false
            }
            !isShuffle or isRepeat -> {
                binding.randomButton.setImageResource(R.drawable.random1)
                isShuffle = true
                binding.imageTekrar.setImageResource(R.drawable.refresh)
                isRepeat = false
            }
        }

        binding.randomButton.setOnClickListener {
            when {
                isShuffle -> {
                    binding.randomButton.setImageResource(R.drawable.random)
                    isShuffle = false
                    animationBlur(binding.randomButton)
                }
                !isShuffle -> {
                    binding.randomButton.setImageResource(R.drawable.random1)
                    isShuffle = true
                    binding.imageTekrar.setImageResource(R.drawable.refresh)
                    isRepeat = false
                    animationBlur(binding.randomButton)
                }
            }
            saveData(isRepeat, isShuffle)
        }

        //btn Repeat
        binding.imageTekrar.setOnClickListener {
            when {
                isRepeat -> {
                    binding.imageTekrar.setImageResource(R.drawable.refresh)
                    isRepeat = false
                    animationRepeat()
                }
                !isRepeat -> {
                    binding.imageTekrar.setImageResource(R.drawable.refresh1)
                    isRepeat = true
                    binding.randomButton.setImageResource(R.drawable.random)
                    isShuffle = false
                    animationRepeat()
                }
            }
            saveData(isRepeat, isShuffle)
        }

        //ToDo this fixes   mediaPlayer => isRepeat & isShuffle
        mediaPlayer!!.setOnCompletionListener {
            when {
                isRepeat -> playSong(currentSongIndex)
                isShuffle -> {
                    val rand = Random
                    currentSongIndex = rand.nextInt((myListTrack.size - 1))
                    playSong(currentSongIndex)
                }
                else -> {
                    when {
                        currentSongIndex<(myListTrack.size - 1) -> {
                            playSong(currentSongIndex + 1)
                            currentSongIndex += 1
                        }
                        else -> {
                            playSong(0)
                            currentSongIndex = 0
                        }
                    }
                }
            }
        }

        //btn forward
        var currentPosition = 0
        val duration = mediaPlayer!!.duration
        binding.btnForward.setOnClickListener {
            currentPosition = mediaPlayer!!.currentPosition
            if(mediaPlayer!!.isPlaying && duration != currentPosition) {
                currentPosition += 5000
                mediaPlayer!!.seekTo(currentPosition)
                animationBlur(binding.btnForward)
            }
        }

        //btn backward
        var backwardTime = 5000
        binding.btnBackward.setOnClickListener {
            currentPosition = mediaPlayer!!.currentPosition
            if(mediaPlayer!!.isPlaying && currentPosition > 5000) {
                currentPosition -= 5000
                mediaPlayer!!.seekTo(currentPosition)
                animationBlur(binding.btnBackward)
            }
        }

        // btn Speaker
        binding.imageSpeaker.setOnClickListener {
            val audioManager: AudioManager =getSystemService(AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.mediaMaxVolume
            val randomIndex = Random.nextInt(((maxVolume - 0) + 1) + 0)
            audioManager.setMediaVolume(randomIndex)
            animationBlur(binding.imageSpeaker)
        }

        //btn list
        binding.baseline.setOnClickListener {
            animationBlur(binding.baseline)
            onBackPressed()
        }
    }

    private fun saveData(repeat: Boolean, shuffle:Boolean) {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putBoolean("REPEAT_KEY", repeat)
            putBoolean("SHUFFLE_KEY", shuffle)
        }.apply()
    }

    private fun loadData(repeat: Boolean, shuffle:Boolean) {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val repeatSh:Boolean = sharedPreferences.getBoolean("REPEAT_KEY", false)
        val shuffleSh:Boolean = sharedPreferences.getBoolean("SHUFFLE_KEY", false)
        repeat == repeatSh
        shuffle == shuffleSh
    }

    // volume
    private fun AudioManager.setMediaVolume(volumeIndex: Int) {
        this.setStreamVolume(AudioManager.STREAM_MUSIC, volumeIndex, AudioManager.FLAG_SHOW_UI)
    }

    private val AudioManager.mediaMaxVolume
    get() = this.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    val AudioManager.mediaCurrentVolume
    get() = this.getStreamVolume(AudioManager.STREAM_MUSIC)

    //seekBar 2
    var handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val currentPosition = msg.what
            binding.seekBar.progress = currentPosition
            val elapsedTime = createTimeLabel(currentPosition)
            binding.elapsedTimeLable.text = elapsedTime

            val remainingTime = createTimeLabel(totalTime - currentPosition)
            binding.totalTimer.text = "-$remainingTime"
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel: String
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec
        return timeLabel
    }

    inner class mySongThread() : Thread() {
        override fun run() {
            while (true) {
                try { sleep(1000) }
                catch (ex: Exception) { }
                runOnUiThread {
                    if (mediaPlayer!= null)
                        binding.seekBar.progress = mediaPlayer!!.currentPosition
                }
            }

        }
    }

    private fun setCoverPlayActivity() {
        when {
            changeCover != null -> {
                Glide.with(this).asBitmap()
                    .load(changeCover)
                    .into(binding.coverPlayActivity)
            }
            else -> {
                Glide.with(this)
                    .load(R.drawable.ic_baseline_headset_24)
                    .into(binding.coverPlayActivity)
            }
        }
    }

    //animation
    private fun animationZoom(imageView: ImageView) {
        val animScale = AnimationUtils.loadAnimation(this, R.anim.anim_pause)
        imageView.startAnimation(animScale)
    }

    private fun animationRepeat() {
        val animScale = AnimationUtils.loadAnimation(this, R.anim.anim_repeat)
        binding.imageTekrar.startAnimation(animScale)
    }

    private fun animationBlur(imageView: ImageView) {
        val animScale = AnimationUtils.loadAnimation(this, R.anim.anim_backward)
        imageView.startAnimation(animScale)
    }
}




