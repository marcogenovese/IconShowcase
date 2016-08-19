package jahirfiquitiva.iconshowcase.utilities;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.XmlRes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jahirfiquitiva.iconshowcase.R;

/**
 * @author Allan Wang
 */
public class ChangelogXmlParser {

    public static class ChangelogItem implements Parcelable {

        private final String mTitle;
        private final ArrayList<String> mPoints;

        public ChangelogItem(String name) {
            mTitle = name;
            mPoints = new ArrayList<>();
        }

        public String getTitle() {
            return mTitle;
        }

        public List<String> getItems() {
            return mPoints;
        }

        public void addItem(String s) {
            mPoints.add(s);
        }

        public int size() {
            return mPoints.size();
        }

        protected ChangelogItem(Parcel in) {
            mTitle = in.readString();
            if (in.readByte() == 0x01) {
                mPoints = new ArrayList<>();
                in.readList(mPoints, String.class.getClassLoader());
            } else {
                mPoints = null;
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mTitle);
            if (mPoints == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeList(mPoints);
            }
        }

        @SuppressWarnings("unused")
        public static final Creator<ChangelogItem> CREATOR = new Creator<ChangelogItem>() {
            @Override
            public ChangelogItem createFromParcel(Parcel in) {
                return new ChangelogItem(in);
            }

            @Override
            public ChangelogItem[] newArray(int size) {
                return new ChangelogItem[size];
            }
        };
    }

    public static ArrayList<ChangelogItem> parse(@NonNull Context context, @XmlRes int xmlRes) {
        ChangelogItem mCurrentChangelogItem = null;
        ArrayList<ChangelogItem> mChangelogItems = new ArrayList<>();

        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getXml(xmlRes);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        final String tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("version")) {
                            mCurrentChangelogItem = new ChangelogItem(parser.getAttributeValue(null, "title"));
                            mChangelogItems.add(mCurrentChangelogItem);
                        } else if (tagName.equalsIgnoreCase("item")) {
                            if (mCurrentChangelogItem == null) {
                                mCurrentChangelogItem = new ChangelogItem(context.getString(R.string.default_new_version_title));
                                mChangelogItems.add(mCurrentChangelogItem);
                            }
                            mCurrentChangelogItem.addItem(parser.getAttributeValue(null, "text"));
                        }
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            if (parser != null)
                parser.close();
        }
        return mChangelogItems;
    }
}