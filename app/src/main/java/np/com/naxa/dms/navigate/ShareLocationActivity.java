package np.com.naxa.dms.navigate;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import np.com.naxa.dms.App;
import np.com.naxa.dms.R;
import np.com.naxa.dms.ViewModelFactory;
import np.com.naxa.dms.common.Util;
import timber.log.Timber;

public class ShareLocationActivity extends AppCompatActivity {

    TextView mTextLatLng;
    @Inject
    ViewModelFactory mViewModelFactory;
    ShareLocationViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

         mTextLatLng = findViewById(R.id.text_curr_latlng);
        ((App) getApplication()).getAppComponent().inject(this);
        initViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.checkLocationPermission(this)) {
            subscribeToLocationUpdate();
        }
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
            subscribeToLocationUpdate();
        } else {
            Toast.makeText(this, "Please grant permission to this app",
                    Toast.LENGTH_LONG).show();
        }
    }


    public void onBroadcastBtnClick(View view) {

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


    }

    private void onLocationUpdateError(Throwable t) {
        if (t instanceof SecurityException) {
            // Access to coarse or fine location are not allowed by the user
            Util.checkLocationPermission(ShareLocationActivity.this);
        }
        Timber.d("LocationUpdateErr: %s", t.toString());
    }


    @SuppressLint("CheckResult")
    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ShareLocationViewModel.class);

    }


}

