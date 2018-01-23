package com.santamaria.shakecontrolspotify

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Switch
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.kobakei.ratethisapp.RateThisApp
import com.santamaria.shakecontrolspotify.R.id.*
import com.santamaria.shakecontrolspotify.ShakeDetector.OnShakeListener

class MainActivity : AppCompatActivity() {

    //HelperClass
    private val helperClass = HelperClass(this)

    //Sensor variables
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mShakeDetector: ShakeDetector? = null

    //Boolean states switch views variables
    var isVibrateOn = true
    var isShowMessageOn = true

    //Switch view variables
    private lateinit var vibrateSwitch: Switch
    private lateinit var showMessageSwitch: Switch

    //SharedPreferences variables
    private val SharedPreferencesName = "SHAKE_IT"
    private val keyNameVibrate = "VIBRATE_STATE"
    private val keyNameShowMessage = "SM_STATE"
    private var sharedPreferences: SharedPreferences? = null
    private var isLoading = false

    //Ad variables
    private var mAdView: AdView? = null

    //Notification variable
    //private var notHelper: NotificationHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rateThisApp()

        //notHelper = NotificationHelper(this)

        sharedPreferences = getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE)

        vibrateSwitch = findViewById(idVibrateSwith) as Switch
        showMessageSwitch = findViewById(idShowMessageSwitch) as Switch

        vibrateSwitch.setOnCheckedChangeListener { _, _ ->

            isVibrateOn = vibrateSwitch.isChecked

            if (!isLoading)
                saveStateCheckView(isVibrateOn, keyNameVibrate)

        }

        showMessageSwitch.setOnCheckedChangeListener { _, _ ->

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

        /* TODO: Need to find a way to handle different events on shake...
        thread(start = true) {

            val TIME_LAPSE = 1000

            while (true) {

                if (list.size == 2) {

                    if ((list[1].time - list[0].time) < TIME_LAPSE) {

                        if (list[1].actionValue == 2) {
                            helperClass.nextSong()
                        } else {
                            helperClass.previousSong()
                        }
                    } else {
                        if (list[0].actionValue == 2) {
                            helperClass.nextSong()
                        } else {
                            helperClass.previousSong()
                        }
                    }

                    list.clear()

                } else {

                    var now = System.currentTimeMillis()

                    if (list.size == 1) {

                        if ((now - list[0].time) > TIME_LAPSE - 100) {
                            if (list[0].actionValue == 2) {
                                helperClass.nextSong()
                            } else {
                                helperClass.previousSong()
                            }

                            list.clear()
                        }
                    }
                }
            }
        }*/
    }

    private fun rateThisApp() {

        val config = RateThisApp.Config(3, 5)
        config.setTitle(R.string.rate_title)
        config.setMessage(R.string.rate_msg)
        config.setYesButtonText(R.string.rate_now)
        config.setNoButtonText(R.string.rate_no)
        config.setCancelButtonText(R.string.rate_later)
        RateThisApp.init(config)

        //Monitor launch times and interval from installation
        RateThisApp.onCreate(this)

        //If the condition is satisfied "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this)

    }

    private fun saveStateCheckView(currentState: Boolean, key: String) {

        val editor = sharedPreferences!!.edit()
        editor.putBoolean(key, currentState)
        editor.apply()

    }

    private fun loadCheckViewStates() {

        isLoading = true

        sharedPreferences!!.getBoolean(keyNameVibrate, false)
        isShowMessageOn = sharedPreferences!!.getBoolean(keyNameShowMessage, false)


        vibrateSwitch.isChecked = sharedPreferences!!.getBoolean(keyNameVibrate, false)
        showMessageSwitch.isChecked = sharedPreferences!!.getBoolean(keyNameShowMessage, false)

        isLoading = false
    }

    private fun addIconActionBar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.ic_launcher)
            actionBar.setDisplayUseLogoEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
    }

    //var list = LinkedList<ActionRegister>()

    //Inner class to store values such as
    // action and the time that was created.
    /*inner class ActionRegister {
        private var actionValue: Int = 0
        private var time: Long = 0

        constructor(actionValue: Int, time: Long) {
            this.actionValue = actionValue
            this.time = time
        }
    }*/

    private fun handleShakeEvent(count: Int, time: Long) {

        if (count == 2) {
            helperClass.nextSong()
            //Toast.makeText(this, "times " + count, Toast.LENGTH_SHORT).show()
            //list.add(ActionRegister(2, time))
        } /*else if (count == 3) {
            list.add(ActionRegister(3, time))
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = MenuInflater(this)
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

        //hide notification
        //notHelper!!.cancelNotification()

        // Register the Session Manager Listener onResume
        mSensorManager!!.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    /*
    override fun onPause() {
        super.onPause()

        //show Notification
        notHelper!!.showNotification()
    }*/

    override fun onDestroy() {
        super.onDestroy()

        //hide notification
        //notHelper!!.cancelNotification()

        // Unregister the Session Manager Listener onDestroy
        mSensorManager!!.unregisterListener(mShakeDetector)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            this.moveTaskToBack(true)
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
}
