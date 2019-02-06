package adem.com.myweatherapplication;

public class forecast_model {

    private String dayName;
    private double temp;
    private String conditionIconUrl;

    public forecast_model(String dayName, double temp, String conditionIconUrl) {
        this.dayName = dayName;
        this.temp = temp;
        this.conditionIconUrl = conditionIconUrl;
    }

    public String getDayName() {
        return dayName;
    }

    public double getTemp() {
        return temp;
    }

    public String getConditionIconUrl() {
        return conditionIconUrl;
    }
}