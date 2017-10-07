package com.santamaria.shakecontrolspotify

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.view.KeyEvent
import android.widget.Switch
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.santamaria.shakecontrolspotify.R.id.idShowMessageSwitch
import com.santamaria.shakecontrolspotify.R.id.idVibrateSwith
import com.santamaria.shakecontrolspotify.ShakeDetector.OnShakeListener
import com.google.android.gms.ads.AdView





class MainActivity : AppCompatActivity() {

    //Sensor variables
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mShakeDetector: ShakeDetector? = null

    //Boolean states switch views variables
    private var isVibrateOn = false
    private var isShowMessageOn = false

    //Switch view variables
    private lateinit var vibrateSwitch : Switch
    private lateinit var showMessageSwitch : Switch

    //SharedPreferences variables
    val SharedPreferencesName = "SHAKE_IT"
    val keyNameVibrate = "VIBRATE_STATE"
    val keyNameShowMessage = "SM_STATE"
    var sharedPreferences:SharedPreferences ?= null
    var isLoading = false

    //Ad variables
    private val TAG = "MainActivity"
    private var mAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE)

        vibrateSwitch = findViewById(idVibrateSwith) as Switch
        showMessageSwitch = findViewById(idShowMessageSwitch)  as Switch

        vibrateSwitch.setOnCheckedChangeListener { compoundButton, b ->

            isVibrateOn = vibrateSwitch.isChecked

            if (!isLoading)
                saveStateCheckView(isVibrateOn, keyNameVibrate)

        }

        showMessageSwitch.setOnCheckedChangeListener { compoundButton, b ->

            isShowMessageOn = showMessageSwitch.isChecked

            if (!isLoading)
                saveStateCheckView(isShowMessageOn, keyNameShowMessage)

        }

        loadCheckViewStates()

        // ShakeDetector initialization
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeDetector()
        mShakeDetector!!.setOnShakeListener(object : OnShakeListener {

            override fun onShake(count: Int) {
                handleShakeEvent(count)
            }
        })

        addIconActionBar()

        //Ad Code
        mAdView = findViewById(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)
    }

    private fun saveStateCheckView(currentState:Boolean, key:String){

        var editor = sharedPreferences!!.edit()
        editor.putBoolean(key, currentState)
        editor.commit()

    }

    private fun loadCheckViewStates(){

        isLoading = true

        sharedPreferences!!.getBoolean(keyNameVibrate, false)
        isShowMessageOn = sharedPreferences!!.getBoolean(keyNameShowMessage, false)


        vibrateSwitch.isChecked = sharedPreferences!!.getBoolean(keyNameVibrate, false)
        showMessageSwitch.isChecked = sharedPreferences!!.getBoolean(keyNameShowMessage, false)

        isLoading = false
    }

    private fun addIconActionBar(){
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.ic_launcher)
            actionBar.setDisplayUseLogoEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
    }

    private fun handleShakeEvent(count: Int) {

        if (count == 2 ) {
            nextSong()

        } else if (count == 3 ) {
            previousSong()
        }

        if (isVibrateOn){
            vibrate()
        }

        if (isShowMessageOn){
            Toast.makeText(applicationContext, "Next Song", Toast.LENGTH_SHORT).show()
        }

    }

    private fun nextSong(){

        var keyCode = KeyEvent.KEYCODE_MEDIA_NEXT

        var intent = Intent(Intent.ACTION_MEDIA_BUTTON)

        intent.`package` = "com.spotify.music"
        synchronized (this) {
            intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            applicationContext.sendOrderedBroadcast(intent, null)
            intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_UP, keyCode))
            applicationContext.sendOrderedBroadcast(intent, null)

        }
    }

    private fun previousSong(){

        var keyCode = KeyEvent.KEYCODE_MEDIA_PREVIOUS

        var intent = Intent(Intent.ACTION_MEDIA_BUTTON)

        intent.`package` = "com.spotify.music"
        synchronized (this) {
            intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            applicationContext.sendOrderedBroadcast(intent, null)
            intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_UP, keyCode))
            applicationContext.sendOrderedBroadcast(intent, null)

        }
    }

    private fun vibrate() {
        var v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(300)
    }

    public override fun onResume() {
        super.onResume()
        // Register the Session Manager Listener onResume
        mSensorManager!!.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the Session Manager Listener onDestroy
        mSensorManager!!.unregisterListener(mShakeDetector)
    }

}
