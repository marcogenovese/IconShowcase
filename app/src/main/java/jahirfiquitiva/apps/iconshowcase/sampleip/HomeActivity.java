/*
 * Copyright (c) 2016 Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.apps.iconshowcase.sampleip;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import jahirfiquitiva.iconshowcase.utilities.LauncherIntents;
import jahirfiquitiva.iconshowcase.utilities.utils.NotificationUtils;
import jahirfiquitiva.iconshowcase.utilities.utils.Utils;
import timber.log.Timber;

public class HomeActivity extends AppCompatActivity {

    private static final boolean
            ENABLE_DONATIONS = false,

    ENABLE_GOOGLE_DONATIONS = false,
            ENABLE_PAYPAL_DONATIONS = false,
            ENABLE_FLATTR_DONATIONS = false,
            ENABLE_BITCOIN_DONATIONS = false,

    ENABLE_LICENSE_CHECK = false,
            ENABLE_AMAZON_INSTALLS = false;

    private static final String GOOGLE_PUBLISHER_KEY = "insert_key_here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class service = FirebaseService.class;
        Bundle bundle = getIntent().getExtras();
        if (NotificationUtils.hasNotificationExtraKey(this, getIntent(), "open_link", service)) {
            Utils.openLink(this, getIntent().getStringExtra("open_link"));
        } else {
            if ((getIntent().getDataString() != null && getIntent().getDataString().equals
                    ("apply_shortcut"))
                    && (Utils.getDefaultLauncherPackage(this) != null)) {
                try {
                    new LauncherIntents(this, Utils.getDefaultLauncherPackage(this));
                } catch (IllegalArgumentException ex) {
                    runIntent(service);
                }
            } else {
                runIntent(service);
            }
        }
        finish();
    }

    private void runIntent(Class service) {
        Intent intent = new Intent(HomeActivity.this, jahirfiquitiva.iconshowcase.activities
                .ShowcaseActivity.class);

        intent.putExtra("open_wallpapers",
                NotificationUtils.isNotificationExtraKeyTrue(this, getIntent(), "open_walls",
                        service));

        intent.putExtra("installer", getAppInstaller());

        intent.putExtra("curVersionCode", getAppCurrentVersionCode());

        intent.putExtra("enableDonations", ENABLE_DONATIONS);
        intent.putExtra("enableGoogleDonations", ENABLE_GOOGLE_DONATIONS);
        intent.putExtra("enablePayPalDonations", ENABLE_PAYPAL_DONATIONS);
        intent.putExtra("enableFlattrDonations", ENABLE_FLATTR_DONATIONS);
        intent.putExtra("enableBitcoinDonations", ENABLE_BITCOIN_DONATIONS);

        //noinspection PointlessBooleanExpression
        intent.putExtra("enableLicenseCheck", (ENABLE_LICENSE_CHECK && !BuildConfig.DEBUG));
        intent.putExtra("enableAmazonInstalls", ENABLE_AMAZON_INSTALLS);

        intent.putExtra("googlePubKey", GOOGLE_PUBLISHER_KEY);

        if (getIntent().getDataString() != null && getIntent().getDataString().contains
                ("_shortcut")) {
            intent.putExtra("shortcut", getIntent().getDataString());
        }

        startActivity(intent);
    }

    private String getAppInstaller() {
        return getPackageManager().getInstallerPackageName(getPackageName());
    }

    private int getAppCurrentVersionCode() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.d("Unable to get version code. Reason:", e.getLocalizedMessage());
            return -1;
        }
    }

}