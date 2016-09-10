package jahirfiquitiva.iconshowcase.events;

/**
 * Created by Allan Wang on 2016-09-10.
 */
public class OnLoadEvent {

    public enum Type {
        HOMEPREVIEWS, PREVIEWS, WALLPAPERS
    }

    public final Type type;

    public OnLoadEvent(Type type) {
        this.type = type;
    }
}
