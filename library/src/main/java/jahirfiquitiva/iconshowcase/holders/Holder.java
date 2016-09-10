package jahirfiquitiva.iconshowcase.holders;

import jahirfiquitiva.iconshowcase.events.OnLoadEvent;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.models.IconsCategory;
import jahirfiquitiva.iconshowcase.models.WallpaperItem;

/**
 * Created by Allan Wang on 2016-09-10.
 */
public class Holder {
    private CategoryList mPreview = new CategoryList();
    private HomePreviewList mHome = new HomePreviewList();
    private WallpapersList mWalls = new WallpapersList();

    public CategoryList preview() {
        return mPreview;
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

