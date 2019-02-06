package adem.com.myweatherapplication;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.*;

public class weather_activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<weather_model> {

    public static final String TAG = weather_activity.class.getSimpleName();

    private static final String API_KEY = "b7878601c0c943fabf683746181705";

    private ProgressBar mLoadingSpinner;
    private EditText mSearchInput;
    private TextView mEmptyStateTextView;
    private LoaderManager mLoaderManager;
    private Button bSearchButton;
    private Button cfButton;
    private android.support.constraint.Group mWeatherDetailsGroup;
    private android.support.constraint.Group mErrorGroup;
    private String cityName;

    private weather_model wml;
    private boolean isFahrenheit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        /*
          Initialising default Views
         */
        mLoadingSpinner = findViewById(R.id.loading_indicator);
        mLoadingSpinner.setVisibility(View.GONE);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        bSearchButton = findViewById(R.id.button2);
        cfButton = findViewById(R.id.button);

        bSearchButton.setVisibility(View.INVISIBLE);
        cfButton.setVisibility(View.INVISIBLE);

        // TODO: Load this information from local storage and set it here
        SharedPreferences sharedPrefs = this.getPreferences(Context.MODE_PRIVATE);
        boolean isFahrenheit = sharedPrefs.getBoolean("isFahrenheit", false);
        Log.i("isFahrenheit Load",Boolean.toString(isFahrenheit));
        this.isFahrenheit = isFahrenheit;

        // Check if API key exists
        if (API_KEY.isEmpty()) {

            // Set empty state text to display "Please obtain your API KEY first from APIXU.com"
            mEmptyStateTextView.setText(R.string.no_api_key);

            // Check internet connectivity
        } else if (!network_check.isOnline(this)) {

            // Set empty state text to display "No internet connection."
            mEmptyStateTextView.setText(R.string.no_internet);

        } else {
            // Hiding all error messages and weather
            Button mSearchButton = findViewById(R.id.search_button);
            mSearchInput = findViewById(R.id.search_input);
            mLoaderManager = getLoaderManager();


            mSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLoadingSpinner.setVisibility(View.VISIBLE);
                    cityName = mSearchInput.getText().toString();
                    if (TextUtils.isEmpty(cityName)) {
                        Toast.makeText(weather_activity.this, "Type the city name", Toast.LENGTH_SHORT).show();

                    } else {

                        // Restart a Loader to refresh data
                        mLoaderManager.restartLoader(1, null, weather_activity.this);
                        mLoaderManager.initLoader(1, null, weather_activity.this);
                    }

                    // Hide keyboard when button is pressed.
                    dismissKeyboard(weather_activity.this);
                }
            });

            // Deliver results after screen rotation
            if (mLoaderManager.getLoader(1) != null) {
                mLoaderManager.initLoader(1, null, this);
            }

        }
    }

    private void updateUi() {
        weather_model weather = this.wml;

        // Display the city name in the UI.
        TextView city = findViewById(R.id.city_name);
        city.setText(weather.getCityName());

        // Display the country name in the UI.
        TextView country = findViewById(R.id.country);
        country.setText(weather.getCountry());

        // Display the current temperature inside the circle that changes
        // colours depends on temperature heights.
        TextView tempCelsius = findViewById(R.id.temp);
        GradientDrawable tempCircle = (GradientDrawable) tempCelsius.getBackground();
        double tempColor = getTemperatureColor(weather.getTemp());
        double tempValue = weather.getTemp();
        if (this.isFahrenheit) {
            tempValue = (tempValue * 1.8) + 32;
        }
        tempCircle.setColor( (int) tempColor);
        tempCelsius.setText(getString(this.isFahrenheit ? R.string.temp_degree_f : R.string.temp_degree, String.format("%.1f",tempValue)));

        // Display the weather condition icon in the UI.
        ImageView conditionIcon = findViewById(R.id.condition_icon);

        Picasso.get()
                .load("http:" + weather.getConditionIconUrl())
                .into(conditionIcon);

        String url = weather.getConditionIconUrl();

        // Change which Umbrella icon is being shown.
        if (!url.contains("260.png") && !url.contains("248.png") && !url.contains("143.png") && !url.contains("122.png") && !url.contains("119.png") && !url.contains("116.png") && !url.contains("113.png")) {
            ImageView umbrellaIcon = findViewById(R.id.imageView3);
            umbrellaIcon.setVisibility(View.INVISIBLE);

            ImageView umbrellaCrossIcon = findViewById(R.id.imageView2);
            umbrellaCrossIcon.setVisibility(View.VISIBLE);
        } else {
            ImageView umbrellaIcon = findViewById(R.id.imageView3);
            umbrellaIcon.setVisibility(View.VISIBLE);

            ImageView umbrellaCrossIcon = findViewById(R.id.imageView2);
            umbrellaCrossIcon.setVisibility(View.INVISIBLE);
        }

        // Display the condition text in the UI.
        TextView conditionText = findViewById(R.id.condition_text);
        conditionText.setText(weather.getConditionText());

        // Update forecast section.
        Resources r = getResources();
        String name = getPackageName();

        // Create an array of integers for resource IDs for forecast day name, condition icon and
        // temperature (4 days).
        int[] resId = new int[4];

        for (int i = 0; i < 4; i++) {

            resId[i] = r.getIdentifier("forecast_day" + (i + 1) + "_name", "id", name);
            TextView forecastDayName = findViewById(resId[i]);

            // Get the day name from from forecast array at current position.
            forecastDayName.setText(weather.getForecastModelArray().get(i).getDayName());

            resId[i] = r.getIdentifier("forecast_day" + (i + 1) + "_icon", "id", name);

            // Get the condition icon URL from from forecast array at current position.
            ImageView forecastConditionIcon = findViewById(resId[i]);
            Picasso.get()
                    .load("http:" + weather.getForecastModelArray()
                            .get(i).getConditionIconUrl())
                    .into(forecastConditionIcon);

            // Get the temperature from forecast array and display it inside the circle that changes
            // colours depends on temperature heights.
            resId[i] = r.getIdentifier("forecast_day" + (i + 1) + "_temp", "id", name);
            TextView tempForecast = findViewById(resId[i]);
            GradientDrawable tempCircleForecast = (GradientDrawable) tempForecast.getBackground();
            double tempColorForecast = getTemperatureColor(weather.getForecastModelArray().get(i).getTemp());
            double forecastTemp = weather.getForecastModelArray().get(i).getTemp();
            if (this.isFahrenheit) {
                forecastTemp = (forecastTemp * 1.8) + 32;
            }
            tempCircleForecast.setColor((int) tempColorForecast);

            tempForecast.setText(getString(this.isFahrenheit ? R.string.temp_degree_f : R.string.temp_degree, String.format("%.1f",forecastTemp)));

            mLoadingSpinner.setVisibility(View.GONE);
        }
    }

    public void switchCelsiusFahrenheit(View view) {
        this.isFahrenheit = !this.isFahrenheit;
        SharedPreferences sharedPrefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean("isFahrenheit", this.isFahrenheit);
        Log.i("isFahrenheit Save", Boolean.toString(this.isFahrenheit));
        editor.apply();
        updateUi();
    }

    public void searchNotFound() {
        Intent intent = new Intent(this, pop.class);
        startActivity(intent);
    }

    public void goToManual(View view){
        Intent intent = new Intent(this, usermanual.class);
        startActivity(intent);
    }

    public void goToEmail(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_SUBJECT, this.wml.getCityName()+"'s Temperature");
        if (!this.isFahrenheit) {
            intent.putExtra(Intent.EXTRA_TEXT, "This is the current temperature for " + this.wml.getCityName() + ": " + this.wml.getTemp() + "°C");
        } else {
            intent.putExtra(Intent.EXTRA_TEXT, "This is the current temperature for " + this.wml.getCityName() + ": " + (this.wml.getTemp()*1.8)+32 + "°F");
        }
        startActivity(Intent.createChooser(intent, ""));
    }

    public double getTemperatureColor(double temp) {
        double tempColorResourceId;

        if (temp <= -21)
            tempColorResourceId = R.color.tempM21;
        else if (-20 <= temp && temp <= -16)
            tempColorResourceId = R.color.tempM20_M16;
        else if (-15 <= temp && temp <= -11)
            tempColorResourceId = R.color.tempM15_M11;
        else if (-10 <= temp && temp <= -6)
            tempColorResourceId = R.color.tempM10_M6;
        else if (-5 <= temp && temp <= -1)
            tempColorResourceId = R.color.tempM5_M1;
        else if (0 <= temp && temp <= 4)
            tempColorResourceId = R.color.temp0_P4;
        else if (5 <= temp && temp <= 9)
            tempColorResourceId = R.color.tempP5_P9;
        else if (10 <= temp && temp <= 14)
            tempColorResourceId = R.color.tempP10_P14;
        else if (15 <= temp && temp <= 19)
            tempColorResourceId = R.color.tempP15_P19;
        else if (20 <= temp && temp <= 24)
            tempColorResourceId = R.color.tempP20_P24;
        else if (25 <= temp && temp <= 29)
            tempColorResourceId = R.color.tempP25_P29;
        else if (30 <= temp && temp <= 34)
            tempColorResourceId = R.color.tempP30_P34;
        else if (temp >= 35)
            tempColorResourceId = R.color.tempP35;
        else
            tempColorResourceId = R.color.colorAccent;

        return ContextCompat.getColor(weather_activity.this, (int) tempColorResourceId);
    }

    @Override
    public Loader<weather_model> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader fteor the given URL, API Key and city name
        return new weather_loader(this, String.format("http://api.apixu.com/v1/forecast.json?key=%s&days=5&q=%s", API_KEY, cityName));
    }

    @Override
    public void onLoadFinished(Loader<weather_model> loader, weather_model model) {
        // If {@link WeatherModel} object is empty then hide the weather UI and display error message.
        if (model == null) {
            mLoadingSpinner.setVisibility(View.GONE);
            searchNotFound();
            mEmptyStateTextView.setText(R.string.location_no_found);

        } else {
            mEmptyStateTextView.setText("");
            this.wml = model;
            bSearchButton.setVisibility(View.VISIBLE);
            cfButton.setVisibility(View.VISIBLE);
            // If the model is valid then populate it in the UI.
            updateUi();
        }
    }

    // Force to hide keyboard using InputMethodManager in current Activity view.
    private void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getApplicationWindowToken(), 0);
            }
    }

    @Override
    public void onLoaderReset(Loader<weather_model> loader) {
    }
}