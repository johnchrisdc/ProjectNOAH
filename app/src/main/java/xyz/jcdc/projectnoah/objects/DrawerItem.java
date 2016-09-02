package xyz.jcdc.projectnoah.objects;

/**
 * Created by jcdc on 9/2/2016.
 */

public class DrawerItem {

    private int icon;
    private String title;

    public DrawerItem(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
