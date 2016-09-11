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

package jahirfiquitiva.iconshowcase.holders;

import jahirfiquitiva.iconshowcase.events.OnLoadEvent;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.models.IconsCategory;
import jahirfiquitiva.iconshowcase.models.WallpaperItem;

/**
 * Created by Allan Wang on 2016-09-10.
 */
public class Holder {

    private CategoryList mIconsCategories = new CategoryList();
    private HomePreviewList mHome = new HomePreviewList();
    private WallpapersList mWalls = new WallpapersList();

    public CategoryList iconsCategories() {
        return mIconsCategories;
    }

    public HomePreviewList home() {
        return mHome;
    }

    public WallpapersList walls() {
        return mWalls;
    }

    public class CategoryList extends ListHolderFrame<IconsCategory> {

        @Override
        public OnLoadEvent.Type getEventType() {
            return OnLoadEvent.Type.PREVIEWS;
        }
    }

    public class HomePreviewList extends ListHolderFrame<IconItem> {

        @Override
        public OnLoadEvent.Type getEventType() {
            return OnLoadEvent.Type.HOMEPREVIEWS;
        }
    }

    public class WallpapersList extends ListHolderFrame<WallpaperItem> {

        @Override
        public OnLoadEvent.Type getEventType() {
            return OnLoadEvent.Type.WALLPAPERS;
        }
    }

}