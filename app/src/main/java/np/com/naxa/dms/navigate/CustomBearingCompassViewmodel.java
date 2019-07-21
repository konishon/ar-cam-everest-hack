package np.com.naxa.dms.navigate;


import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;

import javax.inject.Inject;

public class CustomBearingCompassViewmodel extends AndroidViewModel {

    private Locator locator;

    @Inject
    public CustomBearingCompassViewmodel(Application application) {
        super(application);
        this.locator = new Locator(application);

    }


    public void geLocationUpdates(Locator.Listener callback) {
        this.locator.getLocation(Locator.Method.GPS, new Locator.Listener() {
            @Override
            public void onLocationFound(Location location) {
                callback.onLocationFound(location);
            }

            @Override
            public void onLocationNotFound() {
                callback.onLocationNotFound();
            }
        });
    }


}

