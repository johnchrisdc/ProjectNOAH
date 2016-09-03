package xyz.jcdc.projectnoah.objects;

import java.io.Serializable;

/**
 * Created by jcdc on 9/3/2016.
 */

public class Layer implements Serializable {

    private String category;
    private String action;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
