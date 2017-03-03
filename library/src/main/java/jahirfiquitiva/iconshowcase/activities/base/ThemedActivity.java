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

import android.os.Bundle;

import com.pitchedapps.capsule.library.activities.CapsuleActivity;

import jahirfiquitiva.iconshowcase.utilities.utils.ThemeUtils;

public class ThemedActivity extends CapsuleActivity {

    private boolean mLastTheme;
    private boolean mLastNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLastTheme != ThemeUtils.isDarkTheme()) {
            ThemeUtils.restartActivity(this);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mLastTheme = ThemeUtils.isDarkTheme();
    }

    @Override
    protected int getFragmentId() {
        return 0;
    }

    @Override
    protected int getFabId() {
        return 0;
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }
}