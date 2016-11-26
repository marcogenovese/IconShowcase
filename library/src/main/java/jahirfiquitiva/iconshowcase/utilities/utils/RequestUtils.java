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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.Preferences;

public class RequestUtils {

    @SuppressLint("DefaultLocale")
    public static int canRequestXApps(Context context, int numOfMinutes, Preferences mPrefs) {

        Calendar c = Calendar.getInstance();

        int requestsLeft = mPrefs.getRequestsLeft(context);

        if (requestsLeft >= -1) {
            return requestsLeft;
        } else {
            boolean hasHappenedTheTime = timeHappened(numOfMinutes, mPrefs, c);
            if (!hasHappenedTheTime) {
                return -2;
            } else {
                mPrefs.resetRequestsLeft(context);
                return mPrefs.getRequestsLeft(context);
            }
        }

    }

    @SuppressLint("DefaultLocale")
    public static void saveCurrentTimeOfRequest(Preferences mPrefs, Calendar c) {
        String time = String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.format("%02d", c.get(Calendar.MINUTE));
        String day = String.format("%02d", c.get(Calendar.DAY_OF_YEAR));
        mPrefs.setRequestHour(time);
        mPrefs.setRequestDay(Integer.valueOf(day));
        mPrefs.setRequestsCreated(true);
    }

    @SuppressLint("DefaultLocale")
    private static boolean timeHappened(int numOfMinutes, Preferences mPrefs, Calendar c) {
        float hours = (numOfMinutes + 1) / 60.0f;
        float hoursToDays = hours / 24.0f;

        String time = mPrefs.getRequestHour();
        int dayNum = mPrefs.getRequestDay();

        if (numOfMinutes <= 0) {
            return true;
        } else {
            if (!(time.equals("null"))) {

                String currentTime = String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" +
                        String.format("%02d", c.get(Calendar.MINUTE));
                String currentDay = String.format("%02d", c.get(Calendar.DAY_OF_YEAR));

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Date startDate = null;
                try {
                    startDate = simpleDateFormat.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date endDate = null;
                try {
                    endDate = simpleDateFormat.parse(currentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long difference = (endDate != null ? endDate.getTime() : 0) - (startDate != null ? startDate.getTime() : 0);
                if (difference < 0) {
                    Date dateMax = null;
                    try {
                        dateMax = simpleDateFormat.parse("24:00");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date dateMin = null;
                    try {
                        dateMin = simpleDateFormat.parse("00:00");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    difference = ((dateMax != null ? dateMax.getTime() : 0) - startDate.getTime()) + (endDate.getTime() - (dateMin != null ? dateMin.getTime() : 0));
                }
                int days = Integer.valueOf(currentDay) - dayNum;
                int hoursHappened = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hoursHappened)) / (1000 * 60);

                if (days >= hoursToDays) {
                    return true;
                } else {
                    return hoursHappened >= hours || min >= numOfMinutes;
                }
            } else {
                return true;
            }
        }
    }

    @SuppressLint("DefaultLocale")
    public static int getSecondsLeftToEnableRequest(Context context,
                                                    int numOfMinutes, Preferences mPrefs) {

        int secondsHappened = 0;

        Calendar c = Calendar.getInstance();

        String time;
        int dayNum;

        if (mPrefs.getRequestsCreated()) {
            time = mPrefs.getRequestHour();
            dayNum = mPrefs.getRequestDay();

            String currentTime = String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" +
                    String.format("%02d", c.get(Calendar.MINUTE));
            String currentDay = String.format("%02d", c.get(Calendar.DAY_OF_YEAR));

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date startDate = null;
            try {
                startDate = simpleDateFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date endDate = null;
            try {
                endDate = simpleDateFormat.parse(currentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long difference = (endDate != null ? endDate.getTime() : 0) - (startDate != null ? startDate.getTime() : 0);
            if (difference < 0) {
                Date dateMax = null;
                try {
                    dateMax = simpleDateFormat.parse("24:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date dateMin = null;
                try {
                    dateMin = simpleDateFormat.parse("00:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                difference = ((dateMax != null ? dateMax.getTime() : 0) - startDate.getTime()) + (endDate.getTime() - (dateMin != null ? dateMin.getTime() : 0));
            }
            int days = Integer.valueOf(currentDay) - dayNum;
            int hoursHappened = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));

            int minutes = (int) (difference - (1000 * 60 * 60 * 24 * days) -
                    (1000 * 60 * 60 * hoursHappened)) / (1000 * 60);

            secondsHappened = (int) (minutes * 60.0f);

        }

        int secondsLeft = (numOfMinutes * 60) - secondsHappened;

        if (secondsHappened < 0 || numOfMinutes <= 0) {
            mPrefs.resetRequestsLeft(context);
            secondsLeft = 0;
        }

        return secondsLeft;

    }

    public static String getTimeName(Context context, int minutes) {
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

    public static String getTimeNameInSeconds(Context context, int secs) {
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

    public static float getExactMinutes(int minutes, boolean withSeconds) {
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