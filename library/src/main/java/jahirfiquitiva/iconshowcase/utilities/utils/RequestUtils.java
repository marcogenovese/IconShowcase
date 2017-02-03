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

package jahirfiquitiva.iconshowcase.utilities.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.Preferences;

public class RequestUtils {

    @SuppressLint("DefaultLocale")
    public static int canRequestXApps(Context context, Preferences mPrefs) {
        int requestsLeft = mPrefs.getRequestsLeft(context);
        if (requestsLeft >= -1) {
            return requestsLeft;
        } else {
            mPrefs.resetRequestsLeft(context);
            return mPrefs.getRequestsLeft(context);
        }
    }

    public static String getTimeName(Context context, long minutes) {
        String text;
        if (minutes > 40320) {
            text = Utils.getStringFromResources(context, R.string.months).toLowerCase();
        } else if (minutes > 10080) {
            text = Utils.getStringFromResources(context, R.string.weeks).toLowerCase();
        } else if (minutes > 1440) {
            text = Utils.getStringFromResources(context, R.string.days).toLowerCase();
        } else if (minutes > 60) {
            text = Utils.getStringFromResources(context, R.string.hours).toLowerCase();
        } else {
            text = Utils.getStringFromResources(context, R.string.minutes).toLowerCase();
        }
        return text;
    }

    public static String getTimeNameInSeconds(Context context, long secs) {
        String text;
        if (secs > (40320 * 60)) {
            text = Utils.getStringFromResources(context, R.string.months).toLowerCase();
        } else if (secs > (10080 * 60)) {
            text = Utils.getStringFromResources(context, R.string.weeks).toLowerCase();
        } else if (secs > (1440 * 60)) {
            text = Utils.getStringFromResources(context, R.string.days).toLowerCase();
        } else if (secs > (60 * 60)) {
            text = Utils.getStringFromResources(context, R.string.hours).toLowerCase();
        } else if (secs > 60) {
            text = Utils.getStringFromResources(context, R.string.minutes).toLowerCase();
        } else {
            text = Utils.getStringFromResources(context, R.string.seconds).toLowerCase();
        }
        return text;
    }

    public static float getExactMinutes(long minutes, boolean withSeconds) {
        float time;
        if (minutes > 40320) {
            time = minutes / 40320.0f;
        } else if (minutes > 10080) {
            time = minutes / 10080.0f;
        } else if (minutes > 1440) {
            time = minutes / 1440.0f;
        } else if (minutes > 60) {
            time = minutes / 60.0f;
        } else {
            if (withSeconds) {
                time = minutes / 60.0f;
            } else {
                time = minutes;
            }
        }
        return time;
    }

}