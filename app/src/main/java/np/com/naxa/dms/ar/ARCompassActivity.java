package np.com.naxa.dms.ar;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import np.com.naxa.dms.App;
import np.com.naxa.dms.R;
import np.com.naxa.dms.ViewModelFactory;
import np.com.naxa.dms.common.Util;
import np.com.naxa.dms.compass.Compass;
import np.com.naxa.dms.compass.SOTWFormatter;
import np.com.naxa.dms.navigate.CustomBearingCompassViewmodel;
import np.com.naxa.dms.navigate.Locator;
import timber.log.Timber;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;

public class ARCompassActivity extends AppCompatActivity {

    TextView mTextLatLng;
    @Inject
    ViewModelFactory mViewModelFactory;
    CustomBearingCompassViewmodel mViewModel;
    private Disposable dis;

    private Compass compass;
    private ImageView arrowView;
    private TextView sotwLabel;  // SOTW is for "side of the world"
    private TextView tvDistance;

    private float currentAzimuth;
    private SOTWFormatter sotwFormatter;

    private boolean installRequested;
    private boolean hasFinishedLoading = false;

    private Snackbar loadingMessageSnackbar = null;

    private ArSceneView arSceneView;

    // Renderables for this example
    private ModelRenderable andyRenderable;
    private ViewRenderable exampleLayoutRenderable;

    // Our ARCore-Location scene
    private LocationScene locationScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_ar);

        sotwFormatter = new SOTWFormatter(this);

        arrowView = findViewById(R.id.main_image_hands);
        sotwLabel = findViewById(R.id.sotw_label);
        tvDistance = findViewById(R.id.text_curr_distance);

        setupCompass();

        mTextLatLng = findViewById(R.id.text_curr_latlng);
        ((App) getApplication()).getAppComponent().inject(this);
        initViewModel();
        setupARScene();
    }

    private void setupARScene() {
        arSceneView = findViewById(R.id.ar_scene_view);

        // Build a renderable from a 2D View.
        CompletableFuture<ViewRenderable> exampleLayout =
                ViewRenderable.builder()
                        .setView(this, R.layout.ar_overlay_layout)
                        .build();

        CompletableFuture.allOf(
                exampleLayout)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                exampleLayoutRenderable = exampleLayout.get();
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(this, "Unable to load renderables", ex);
                            }

                            return null;
                        });

        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.
        arSceneView
                .getScene()
                .addOnUpdateListener(new Scene.OnUpdateListener() {
                    @Override
                    public void onUpdate(FrameTime frameTime) {

                        if (!hasFinishedLoading) {
                            return;
                        }

                        if (locationScene == null) {
                            // If our locationScene object hasn't been setup yet, this is a good time to do it
                            // We know that here, the AR components have been initiated.
                            locationScene = new LocationScene(ARCompassActivity.this, arSceneView);

                            Disposable disposable = Observable.fromCallable(new Callable<ArrayList<PointOfInterest>>() {
                                @Override
                                public ArrayList<PointOfInterest> call() throws Exception {
                                    return DataGenerator.getData();
                                }
                            }).flatMapIterable((Function<ArrayList<PointOfInterest>, Iterable<PointOfInterest>>) pointOfInterests -> pointOfInterests)
                                    .subscribe(pointOfInterest -> {
                                        Marker.render(ARCompassActivity.this, pointOfInterest, locationScene);
//
                                    }, Timber::e);


                        }

                        Frame frame = arSceneView.getArFrame();
                        if (frame == null) {
                            return;
                        }

                        if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                            return;
                        }

                        if (locationScene != null) {
                            locationScene.processFrame(frame);
                        }

                        if (loadingMessageSnackbar != null) {
                            for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                if (plane.getTrackingState() == TrackingState.TRACKING) {
                                    hideLoadingMessage();
                                }
                            }
                        }
                    }
                });


        // Lastly request CAMERA & fine location permission which is required by ARCore-Location.
        ARLocationPermissionHelper.requestPermission(this);
    }

    /**
     * Make sure we call locationScene.resume();
     */
    @Override
    protected void onResume() {
        super.onResume();
        compass.start();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ARCompassActivity.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
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
        switch (requestCode) {
            case Util.REQ_PERM_CODE:
                if (Util.checkLocationPermissionsResult(requestCode, permissions, grantResults)) {
                    subcribeToLocationUpdatePerodic();
                } else {
                    Toast.makeText(this, "Please grant permission to this app",
                            Toast.LENGTH_LONG).show();
                }
                break;
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
        if (locationScene != null) {
            locationScene.pause();
        }

        arSceneView.pause();
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
            Util.checkLocationPermission(ARCompassActivity.this);
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

