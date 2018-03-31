package com.santamaria.shakecontrolspotify;

import com.android.billingclient.api.Purchase;
import com.santamaria.shakecontrolspotify.billing.BillingManager;

import java.util.List;

import static com.santamaria.shakecontrolspotify.billing.BillingConstants.PREMIUM_APP;

/**
 * Created by Santamaria on 17/03/2018.
 */

public class UpdateListener implements BillingManager.BillingUpdatesListener {

    public boolean isPremium() {
        return isPremium;
    }
    private boolean isPremium = false;

    @Override
    public void onPurchasesUpdated(List<Purchase> purchaseList) {

        isPremium = false;

        for (Purchase purchase : purchaseList) {
            switch (purchase.getSku()) {
                case PREMIUM_APP:
                    isPremium = true;
                    break;
            }
        }
    }
}
