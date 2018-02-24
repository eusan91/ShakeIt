package com.santamaria.shakecontrolspotify

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.*
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

    //variable updated with SharedPreferences in case the user change to 1
    private var gShakeCount = 2

    //Switch view variables
    private lateinit var vibrateSwitch: Switch
    private lateinit var showMessageSwitch: Switch

    //TextView for shakeCount
    private lateinit var shakeCountNumberTextView : TextView

    //seekbar for sensibility
    private lateinit var sensibilitySeekBar : SeekBar

    //dropdown box selection
    private lateinit var dropdownShakeNumber : Spinner

    //SharedPreferences variables
    private val SharedPreferencesName = "SHAKE_IT"
    private val keyNameVibrate = "VIBRATE_STATE"
    private val keyNameShowMessage = "SM_STATE"
    private val keyNameShakeCount = "SHAKE_CNT"
    private val keyNameSensibility = "SENSIBILITY_CNT"
    private var sharedPreferences: SharedPreferences? = null
    private var isLoading = false

    //Ad variables
    private var mAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rateThisApp()

        sharedPreferences = getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE)

        getViews()

        loadCheckViewStates()

        loadProSettings()

        setListenerToViews()

        // ShakeDetector initialization
        initShakeDetector()

        addIconActionBar()

        //Ad Code
        initAdOnView()


    }

    private fun initAdOnView(){
        mAdView = findViewById(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)
    }

    private fun initShakeDetector(){

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeDetector()
        mShakeDetector!!.setOnShakeListener(object : OnShakeListener {

            override fun onShake(count: Int, time: Long) {
                handleShakeEvent(count, time)
            }
        })

    }

    private fun getViews(){
        vibrateSwitch = findViewById(idVibrateSwith) as Switch
        showMessageSwitch = findViewById(idShowMessageSwitch) as Switch
        shakeCountNumberTextView = findViewById(idTextViewShakeTimes) as TextView
        sensibilitySeekBar = findViewById(idSensibilitySeekBar) as SeekBar
        dropdownShakeNumber = findViewById(idShakeCount) as Spinner
    }

    private fun setListenerToViews(){

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

        dropdownShakeNumber.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if (!isLoading) {
                    val position: Int = p2 + 1
                    shakeCountNumberTextView.text = "$position " + getString(R.string.times_next_song)
                    saveStateProSettings(position, keyNameShakeCount)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

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

    private fun saveStateProSettings(currentState: Int, key: String) {

        val editor = sharedPreferences!!.edit()
        editor.putInt(key, currentState)
        editor.apply()

    }

    private fun loadCheckViewStates() {

        isLoading = true

        isShowMessageOn = sharedPreferences!!.getBoolean(keyNameShowMessage, false)
        showMessageSwitch.isChecked = isShowMessageOn

        vibrateSwitch.isChecked = sharedPreferences!!.getBoolean(keyNameVibrate, false)

        isLoading = false
    }

    private fun loadProSettings() {

        isLoading = true

        gShakeCount = sharedPreferences!!.getInt(keyNameShakeCount, 2)
        dropdownShakeNumber.setSelection(gShakeCount-1)

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


    private fun handleShakeEvent(count: Int, time: Long) {

        if (count == gShakeCount) {
            helperClass.nextSong()
        }
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

        // Register the Session Manager Listener onResume
        mSensorManager!!.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()

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
