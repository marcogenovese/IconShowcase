/*
 * Copyright (c) 2016.  Jahir Fiquitiva
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
 * Big thanks to the project contributors. Check them in the repository.
 *
 */

/*
 *
 */

package jahirfiquitiva.apps.iconshowcase.sample;

import android.content.Intent;
import android.os.Bundle;

import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;

public class HomeActivity extends ShowcaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean ENABLE_LICENSE_CHECK = true;

        //the ones below are the defaults; you may remove them if you don't need them or modify them accordingly
        enableDonations(false);
        enableGoogleDonations(false);
        enablePaypalDonations(false);
        enableFlattrDonations(false);
        enableBitcoinDonations(false);
        enableLicenseCheck(ENABLE_LICENSE_CHECK && !BuildConfig.DEBUG);
        enableAmazonInstalls(false);
        setGooglePubkey("insert key here");

        Intent intent = new Intent(HomeActivity.this, jahirfiquitiva.iconshowcase.activities.ShowcaseActivity.class);
        intent.putExtra("installer", getAppInstaller());
        startActivity(intent);

        finish();

    }

    private String getAppInstaller() {
        return getPackageManager().getInstallerPackageName(getPackageName());
    }

}