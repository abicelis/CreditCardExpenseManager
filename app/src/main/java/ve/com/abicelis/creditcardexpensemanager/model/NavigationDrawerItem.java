package ve.com.abicelis.creditcardexpensemanager.model;

/**
 * Created by Alex on 5/8/2016.
 */
public class NavigationDrawerItem {

    String title;
    int iconID;

    public NavigationDrawerItem(String title, int iconID) {
        this.title = title;
        this.iconID = iconID;
    }

    public String getTitle() {
        return title;
    }

    public int getIconID() {
        return iconID;
    }
}
