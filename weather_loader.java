package adem.com.myweatherapplication;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class weather_loader extends AsyncTaskLoader<weather_model> {

    private static final String TAG = weather_loader.class.getName();

    private String mUrl;

    public weather_loader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public weather_model loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract current weather and forecast.
        return http_handler.fetchData(mUrl);
    }
}
