package jahirfiquitiva.iconshowcase.events;

import android.support.annotation.NonNull;

/**
 * Created by Allan Wang on 2016-09-06.
 */
public class WallpaperEvent {

    private String url;
    private boolean success;
    private Step nextStep;

    public enum Step {
        START, LOADING, APPLYING, FINISH
    }

    public WallpaperEvent(@NonNull String u, boolean b, Step s) {
        url = u;
        success = b;
        nextStep = s;
    }

    public String getUrl() {
        return url;
    }

    public boolean isSuccess() {
        return success;
    }

    public Step getNextStep() {
        return nextStep;
    }

}
