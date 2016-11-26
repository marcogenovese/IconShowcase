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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * This Class was created by Patrick J on 07.01.16. For more Details and Licensing have a look at
 * the README.md
 */

public final class PermissionUtils {

    public static final int PERMISSION_REQUEST_CODE = 42;
    private static String VIEWER_ACTIVITY_ACTION;
    private static OnPermissionResultListener onPermissionResultListener;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canAccessStorage(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public static void requestStoragePermission(Activity activity, OnPermissionResultListener permissionResultListener) {
        onPermissionResultListener = permissionResultListener;
        requestStoragePermission(activity);
    }

    public static OnPermissionResultListener permissionReceived() {
        return onPermissionResultListener;
    }

    public static String getViewerActivityAction() {
        return VIEWER_ACTIVITY_ACTION;
    }

    public static void setViewerActivityAction(String viewerActivityAction) {
        VIEWER_ACTIVITY_ACTION = viewerActivityAction;
    }

    public interface OnPermissionResultListener {
        void onStoragePermissionGranted();
    }
}