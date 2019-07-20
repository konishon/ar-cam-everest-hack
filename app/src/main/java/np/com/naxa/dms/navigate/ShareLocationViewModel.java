package np.com.naxa.dms.navigate;


import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;


import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public class ShareLocationViewModel extends AndroidViewModel {

    private Location mLastLocation;
    private Flowable<Location> mLocationUpdatesObserver;
    Locator locator;


    @Inject
    public ShareLocationViewModel(Application application) {
        super(application);
        this.locator = new Locator(application);

    }


    public void geLocationUpdates(Locator.Listener callback) {
        this.locator.getLocation(Locator.Method.GPS, new Locator.Listener() {
            @Override
            public void onLocationFound(Location location) {
                mLastLocation = location;
                callback.onLocationFound(location);
            }

            @Override
            public void onLocationNotFound() {
                callback.onLocationNotFound();
            }
        });
    }



    public Location getLastCachedLocation() {
        return mLastLocation;
    }

}

