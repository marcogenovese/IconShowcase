package jahirfiquitiva.iconshowcase.holders;

/**
 * Created by Allan Wang on 2016-09-10.
 */
public class FullListHolder {

    private static Holder sLists = new Holder();

    public static Holder get() {
        if (sLists == null) return new Holder();
        return sLists;
    }

}
