package xyz.jcdc.projectnoah.chance_of_rain;

/**
 * Created by jcdc on 9/2/2016.
 */

public class Data {
    private double percent_chance_of_rain;
    private String chance_of_rain;
    private String icon;
    private String time;

    public double getPercent_chance_of_rain() {
        return percent_chance_of_rain;
    }

    public void setPercent_chance_of_rain(double percent_chance_of_rain) {
        this.percent_chance_of_rain = percent_chance_of_rain;
    }

    public String getChance_of_rain() {
        return chance_of_rain;
    }

    public void setChance_of_rain(String chance_of_rain) {
        this.chance_of_rain = chance_of_rain;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
