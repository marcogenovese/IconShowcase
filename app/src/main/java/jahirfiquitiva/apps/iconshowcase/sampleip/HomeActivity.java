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

package jahirfiquitiva.apps.iconshowcase.sampleip;

import android.os.Bundle;

import jahirfiquitiva.iconshowcase.activities.base.LaunchActivity;

public class HomeActivity extends LaunchActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Class getFirebaseClass() {
        return FirebaseService.class;
    }

    @Override
    protected boolean enableDonations() {
        return false;
    }

    @Override
    protected boolean enableGoogleDonations() {
        return false;
    }

    @Override
    protected boolean enablePayPalDonations() {
        return false;
    }

    @Override
    protected boolean enableLicCheck() {
        // TODO: Make sure you set this to true if you want to check license.
        return !BuildConfig.DEBUG;
    }

    @Override
    protected boolean enableAmazonInstalls() {
        return false;
    }

    @Override
    protected boolean allowAptoideUse() {
        return false;
    }

    @Override
    protected String licKey() {
        return "insert_key_here";
    }

}