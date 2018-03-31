package com.santamaria.shakecontrolspotify

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.android.billingclient.api.BillingClient
import com.santamaria.shakecontrolspotify.billing.BillingConstants
import com.santamaria.shakecontrolspotify.billing.BillingManager
import com.santamaria.shakecontrolspotify.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED
import com.santamaria.shakecontrolspotify.billing.BillingProvider

class ProSettingsActivity : AppCompatActivity(), BillingProvider {

    private var updateListener: UpdateListener? = null

    private var mBillingManager: BillingManager? = null

    private lateinit var btnPro: Button

    override fun getBillingManager(): BillingManager {
        return mBillingManager!!
    }

    override fun isPremiumPurchased(): Boolean {
        return updateListener!!.isPremium
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_settings)

        updateListener = UpdateListener()


        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = BillingManager(this, updateListener)

        btnPro = findViewById(R.id.idbtnBuy) as Button

        btnPro.setOnClickListener { _ ->
            if (!isPremiumPurchased) {

                if (mBillingManager != null && mBillingManager!!.billingClientResponseCode > BILLING_MANAGER_NOT_INITIALIZED) {
                    billingManager.initiatePurchaseFlow(BillingConstants.PREMIUM_APP, BillingClient.SkuType.INAPP)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (mBillingManager != null && mBillingManager?.billingClientResponseCode == BillingClient.BillingResponse.OK) {
            mBillingManager?.queryPurchases()
        }
    }

    override fun onDestroy() {
        if (mBillingManager != null) {
            mBillingManager?.destroy()
        }
        super.onDestroy()
    }
}
