package adem.com.myweatherapplication;

import android.util.SparseArray;
import android.util.Log;

public class weather_model {

    private String cityName;
    private String country;
    private double temp;
    private String conditionIconUrl;
    private String conditionText;
    private SparseArray<forecast_model> forecastModelArray;

    public weather_model(String cityName, String country, double temp, String conditionIconUrl, String conditionText, SparseArray<forecast_model> forecastModelArray) {
        this.cityName = cityName;
        this.country = country;
        this.temp = temp;
        this.conditionIconUrl = conditionIconUrl;
        this.conditionText = conditionText;
        this.forecastModelArray = forecastModelArray;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public double getTemp() {
        return temp;
    }

    public String getConditionIconUrl() {
        return conditionIconUrl;
    }

    public String getConditionText() {
        return conditionText;
    }

    public SparseArray<forecast_model> getForecastModelArray() {
        return forecastModelArray;
    }
}