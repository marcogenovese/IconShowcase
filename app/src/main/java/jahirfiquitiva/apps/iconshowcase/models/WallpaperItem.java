package jahirfiquitiva.apps.iconshowcase.models;

public class WallpaperItem {

    private final String wallName, wallAuthor, wallUrl, wallDimensions, wallCopyright;

    public WallpaperItem(String wallName, String wallAuthor, String wallUrl,
                         String wallDimensions, String wallCopyright) {
        this.wallName = wallName;
        this.wallAuthor = wallAuthor;
        this.wallUrl = wallUrl;
        this.wallDimensions = wallDimensions;
        this.wallCopyright = wallCopyright;
    }

    public String getWallName() {
        return wallName;
    }

    public String getWallAuthor() {
        return wallAuthor;
    }

    public String getWallURL() {
        return wallUrl;
    }

    public String getWallDimensions() {
        return wallDimensions;
    }

    public String getWallCopyright() {
        return wallCopyright;
    }

}
