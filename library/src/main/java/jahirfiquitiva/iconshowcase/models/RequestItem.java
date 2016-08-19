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

package jahirfiquitiva.iconshowcase.models;

import android.graphics.drawable.Drawable;


public class RequestItem {

    private String appName = null, packageName = null, className = null;
    private final Drawable normalIcon, hiResIcon;
    private boolean selected = false;

    public RequestItem(String appName, String packageName, String className,
                       Drawable normalIcon, Drawable hiResIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.className = className;
        this.normalIcon = normalIcon;
        this.hiResIcon = hiResIcon;
    }

    public String getClassName() {
        return className;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getNormalIcon() {
        return normalIcon;
    }

    public Drawable getHiResIcon() {
        return hiResIcon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Used to compare object to object
     *
     * @param other
     * @return
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RequestItem)) {
            return false;
        }
        RequestItem that = (RequestItem) other;
        return this.appName.equals(that.appName)
                && this.packageName.equals(that.packageName)
                && this.className.equals(that.className);
    }
}