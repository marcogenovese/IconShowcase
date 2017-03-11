/*
 * Copyright (c) 2017 Jahir Fiquitiva
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

package jahirfiquitiva.iconshowcase.activities.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.config.Config;
import jahirfiquitiva.iconshowcase.utilities.LauncherIntents;
import jahirfiquitiva.iconshowcase.utilities.utils.NotificationUtils;
import jahirfiquitiva.iconshowcase.utilities.utils.Utils;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class service = getFirebaseClass();
        if (NotificationUtils.hasNotificationExtraKey(this, getIntent(), "open_link", service)) {
            Utils.openLink(this, getIntent().getStringExtra("open_link"));
        } else {
            if ((getIntent().getDataString() != null && getIntent().getDataString().equals
                    ("apply_shortcut"))
                    && (Utils.getDefaultLauncherPackage(this) != null)) {
                try {
                    new LauncherIntents(this, Utils.getDefaultLauncherPackage(this));
                } catch (IllegalArgumentException ex) {
                    if (service != null)
                        runIntent(service);
                }
            } else {
                if (service != null)
                    runIntent(service);
            }
        }
        finish();
    }

    private void runIntent(Class service) {
        Intent intent = new Intent(this, ShowcaseActivity.class);

        if (service != null)
            intent.putExtra("open_wallpapers",
                    NotificationUtils.isNotificationExtraKeyTrue(this, getIntent(), "open_walls",
                            service));

        intent.putExtra("enableDonations", enableDonations());
        intent.putExtra("enableGoogleDonations", enableGoogleDonations());
        intent.putExtra("enablePayPalDonations", enablePayPalDonations());
        intent.putExtra("enableLicenseCheck", enableLicCheck());
        intent.putExtra("enableAmazonInstalls", enableAmazonInstalls());
        intent.putExtra("checkLPF", checkLPF());
        intent.putExtra("checkStores", checkStores());
        intent.putExtra("googlePubKey", licKey());

        if (getIntent().getDataString() != null && getIntent().getDataString().contains
                ("_shortcut")) {
            intent.putExtra("shortcut", getIntent().getDataString());
        }

        if (getIntent().getAction() != null) {
            switch (getIntent().getAction()) {
                case Config.ADW_ACTION:
                case Config.TURBO_ACTION:
                case Config.NOVA_ACTION:
                case Intent.ACTION_PICK:
                case Intent.ACTION_GET_CONTENT:
                    intent.putExtra("picker", Config.ICONS_PICKER);
                    break;
                case Intent.ACTION_SET_WALLPAPER:
                    intent.putExtra("picker", Config.WALLS_PICKER);
                    break;
            }
        }

        startActivity(intent);
    }

    protected Class getFirebaseClass() {
        return null;
    }

    protected boolean enableDonations() {
        return false;
    }

    protected boolean enableGoogleDonations() {
        return false;
    }

    protected boolean enablePayPalDonations() {
        return false;
    }

    protected boolean enableLicCheck() {
        return true;
    }

    protected boolean enableAmazonInstalls() {
        return false;
    }

    protected boolean checkLPF() {
        return true;
    }

    protected boolean checkStores() {
        return true;
    }

    protected String licKey() {
        return "insert_key_here";
    }

}