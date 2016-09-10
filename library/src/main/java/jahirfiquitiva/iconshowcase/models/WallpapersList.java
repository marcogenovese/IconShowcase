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

package jahirfiquitiva.iconshowcase.models;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.events.WallJSONEvent;

public class WallpapersList {

    private static ArrayList<WallpaperItem> wallsList;

    public static void createWallpapersList(@NonNull ArrayList<WallpaperItem> wallsList) {
        WallpapersList.wallsList = wallsList;
        EventBus.getDefault().post(new WallJSONEvent(wallsList));
    }

    public static ArrayList<WallpaperItem> getWallpapersList() {
        return wallsList;
    }

    public static void clearList() {
        wallsList = null;
    }

    public static boolean hasList() {
        return wallsList != null && !wallsList.isEmpty();
    }

}
