package com.santamaria.shakecontrolspotify

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Switch
import com.google.android.gms.ads.AdRequest
import com.santamaria.shakecontrolspotify.ShakeDetector.OnShakeListener
import com.google.android.gms.ads.AdView
import com.santamaria.shakecontrolspotify.R.id.*
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    //HelperClass
    private val helperClass = HelperClass(this)

    //Sensor variables
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mShakeDetector: ShakeDetector? = null

    //Boolean states switch views variables
    var isVibrateOn = false
    var isShowMessageOn = false

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

            override fun onShake(count: Int, time: Long) {
                handleShakeEvent(count, time)
            }
        })

        addIconActionBar()

        //Ad Code
        mAdView = findViewById(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)

        thread(start = true) {

            val TIME_LAPSE = 1000

            while(true){

                if (list.size == 2){

                    if ((list[1].time - list[0].time) < TIME_LAPSE){

                        if (list[1].actionValue == 2){
                            helperClass.nextSong()
                        } else {
                            helperClass.previousSong()
                        }
                    } else {
                        if (list[0].actionValue == 2){
                            helperClass.nextSong()
                        } else {
                            helperClass.previousSong()
                        }
                    }

                    list.clear()

                } else {

                    var now = System.currentTimeMillis()

                    if (list.size == 1){

                        if ((now - list[0].time) > TIME_LAPSE-100 ){
                            if (list[0].actionValue == 2){
                                helperClass.nextSong()
                            } else {
                                helperClass.previousSong()
                            }

                            list.clear()
                        }
                    }
                }
            }
        }

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

    var list = LinkedList<ActionRegister>()

    //Inner class to store values such as
    // action and the time that was created.
    inner class ActionRegister{
        var actionValue: Int = 0
        var time: Long = 0

        constructor(actionValue:Int, time:Long){
            this.actionValue = actionValue
            this.time = time
        }
    }

    private fun handleShakeEvent(count: Int, time: Long) {
        //Log.d("ema", count.toString() + " " + time.toString())
        if (count == 2 ) {
            list.add(ActionRegister(2, time))
        } else if (count == 3 ) {
            list.add(ActionRegister(3, time))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        var menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.upper_right_items, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item != null && item.itemId == exit_menu_id) {
            helperClass.alertDialogClose()
        }

        return super.onOptionsItemSelected(item)
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
