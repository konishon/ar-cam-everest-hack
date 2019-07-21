package np.com.naxa.dms.navigate;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import np.com.naxa.dms.App;
import np.com.naxa.dms.R;
import np.com.naxa.dms.ViewModelFactory;
import np.com.naxa.dms.common.Util;
import np.com.naxa.dms.compass.Compass;
import np.com.naxa.dms.compass.SOTWFormatter;
import timber.log.Timber;

public class CustomBearingCompassActivity extends AppCompatActivity {

    TextView mTextLatLng;
    @Inject
    ViewModelFactory mViewModelFactory;
    CustomBearingCompassViewmodel mViewModel;
    private Runnable perodicTask = null;
    private Disposable dis;

    private Compass compass;
    private ImageView arrowView;
    private TextView sotwLabel;  // SOTW is for "side of the world"
    private TextView tvDistance;

    private float currentAzimuth;
    private SOTWFormatter sotwFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        sotwFormatter = new SOTWFormatter(this);

        arrowView = findViewById(R.id.main_image_hands);
        sotwLabel = findViewById(R.id.sotw_label);
        tvDistance = findViewById(R.id.text_curr_distance);

        setupCompass();

        mTextLatLng = findViewById(R.id.text_curr_latlng);
        ((App) getApplication()).getAppComponent().inject(this);
        initViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.checkLocationPermission(this)) {
            subcribeToLocationUpdatePerodic();
        }
        compass.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.d("stop compass");
        compass.stop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Util.checkLocationPermissionsResult(requestCode, permissions, grantResults)) {
            subcribeToLocationUpdatePerodic();
        } else {
            Toast.makeText(this, "Please grant permission to this app",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void subcribeToLocationUpdatePerodic() {
        dis = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        subscribeToLocationUpdate();
                    }
                }).subscribe();


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!dis.isDisposed()) {
            dis.dispose();
        }
    }

    private void subscribeToLocationUpdate() {

        mViewModel.geLocationUpdates(new Locator.Listener() {
            @Override
            public void onLocationFound(Location location) {
                onLocationUpdated(location);

            }

            @Override
            public void onLocationNotFound() {
                onLocationUpdateError(new RuntimeException("Opps"));
            }
        });
    }

    private void onLocationUpdated(Location location) {
        String latlng = location.getLatitude() + "/" + location.getLongitude();
        mTextLatLng.setText(latlng);
        Location yakAndYeti = new Location("");
        yakAndYeti.setLatitude(27.71166);
        yakAndYeti.setLongitude(85.320115);
        float adjustedAzimuth = location.bearingTo(yakAndYeti);
        float distance = location.distanceTo(yakAndYeti);
        drawDistance(distance);
        adjustArrow(adjustedAzimuth);
        adjustSotwLabel(adjustedAzimuth);

    }

    private void drawDistance(float distance) {
        tvDistance.setText(String.valueOf(distance));
    }

    private void onLocationUpdateError(Throwable t) {
        if (t instanceof SecurityException) {
            // Access to coarse or fine location are not allowed by the user
            Util.checkLocationPermission(CustomBearingCompassActivity.this);
        }
        Timber.d("LocationUpdateErr: %s", t.toString());
    }


    @SuppressLint("CheckResult")
    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CustomBearingCompassViewmodel.class);

    }


    private void setupCompass() {
        compass = new Compass(this);
    }

    private void adjustArrow(float azimuth) {
        Timber.d("will set rotation from " + currentAzimuth + " to "
                + azimuth);

        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }

    private void adjustSotwLabel(float azimuth) {
        sotwLabel.setText(sotwFormatter.format(azimuth));
    }


}

