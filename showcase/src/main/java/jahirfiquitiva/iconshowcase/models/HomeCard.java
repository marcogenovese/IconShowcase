package jahirfiquitiva.iconshowcase.models;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 7681 on 2016-02-24.
 */
public class HomeCard implements Parcelable {

    public String title;
    public String desc;
    public Drawable img;
    public boolean imgEnabled;
    public String onClickLink;

    public HomeCard(Builder builder) {
        this.title = builder.title;
        this.desc = builder.desc;
        this.img = builder.img;
        this.imgEnabled = builder.imgEnabled;
        this.onClickLink = builder.onClickLink;
    }

    public static class Builder {

        private String title, desc;
        private Drawable img;
        private boolean imgEnabled = false;
        private String onClickLink;

        public Builder() {
            this.title = "Insert title here";
            this.desc = "Insert description here";
            this.img = null;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder icon(Drawable img) {
            this.img = img;
            this.imgEnabled = true;
            return this;
        }

        public Builder onClickLink(String s) {
            this.onClickLink = s;
            return this;
        }

        public HomeCard build() {
            return new HomeCard(this);
        }
    }

    protected HomeCard(Parcel in) { //TODO correct parcel
        title = in.readString();
        desc = in.readString();
        img = (Drawable) in.readValue(Drawable.class.getClassLoader());
        imgEnabled = in.readByte() != 0x00;
//        onClick = (Object) in.readValue(Object.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeValue(img);
        dest.writeByte((byte) (imgEnabled ? 0x01 : 0x00));
//        dest.writeValue(onClick);
    }

    @SuppressWarnings("unused")
    public static final Creator<HomeCard> CREATOR = new Creator<HomeCard>() {
        @Override
        public HomeCard createFromParcel(Parcel in) {
            return new HomeCard(in);
        }

        @Override
        public HomeCard[] newArray(int size) {
            return new HomeCard[size];
        }
    };
}