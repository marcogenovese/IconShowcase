/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.models;

public class DetailedCreditsItem {

    private final String bannerLink, photoLink, title, content, btnTexts[], btnLinks[];

    public DetailedCreditsItem(String bannerLink, String photoLink, String title, String content, String[] btnTexts, String[] btnLinks) {
        this.bannerLink = bannerLink;
        this.photoLink = photoLink;
        this.title = title;
        this.content = content;
        this.btnTexts = btnTexts;
        this.btnLinks = btnLinks;
    }

    public String getBannerLink() {
        return bannerLink;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String[] getBtnTexts() {
        return btnTexts;
    }

    public String[] getBtnLinks() {
        return btnLinks;
    }
}