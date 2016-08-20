package jahirfiquitiva.iconshowcase.utilities;

import android.content.Context;
import android.support.annotation.Nullable;

import org.sufficientlysecure.donations.google.util.IabHelper;
import org.sufficientlysecure.donations.google.util.IabResult;
import org.sufficientlysecure.donations.google.util.Inventory;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.config.Config;
import timber.log.Timber;

/**
 * Created by Allan Wang on 2016-08-20.
 */
public class DonationUtil {

    public interface OnPremiumListener {
        void hasPurchase(String purchaseCatalogKey);
        void hasNoPurchase();
    }

    public static void hasPurchase(final Context context, @Nullable String pubKey, final OnPremiumListener listener) {
        if (pubKey == null || !Config.get(context).hasGoogleDonations()) {
            listener.hasNoPurchase();
            return;
        }

        final IabHelper mHelper = new IabHelper(context, pubKey);
        final IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (inventory != null) {
                    Timber.i("IAP inventory exists");
                    for (String key : Config.get().stringArray(R.array.nonconsumable_google_donation_items)) {
                        if (inventory.hasPurchase(key)) { //at least one donation value found, now premium
                            Timber.i("%s is purchased", key);
                            listener.hasPurchase(key);
                            return;
                        }
                    }
                }
                listener.hasNoPurchase();
            }
        };

        mHelper.queryInventoryAsync(mGotInventoryListener);
    }

}
