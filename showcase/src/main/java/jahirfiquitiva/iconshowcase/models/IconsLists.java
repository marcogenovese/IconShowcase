package jahirfiquitiva.iconshowcase.models;

import java.util.ArrayList;

public class IconsLists {

    private String name;
    private ArrayList<IconItem> iconsArray = new ArrayList<>();
    private static ArrayList<IconsCategory> categoriesList = new ArrayList<>();

    public IconsLists(String name) {
        this.name = name;
    }

    public IconsLists(String name, ArrayList<IconItem> iconsArray) {
        this.name = name;
        this.iconsArray = iconsArray;
    }

    public IconsLists(ArrayList<IconsCategory> categoriesList) {
        IconsLists.categoriesList = categoriesList;
    }

    public String getCategoryName() {
        return this.name;
    }

    public void setCategoryName(String name) {
        this.name = name;
    }

    public ArrayList<IconItem> getIconsArray() {
        return iconsArray.size() > 0 ? this.iconsArray : null;
    }

    public static ArrayList<IconsCategory> getCategoriesList() {
        return categoriesList.size() > 0 ? categoriesList : null;
    }

}