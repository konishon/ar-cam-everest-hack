package np.com.naxa.dms.ar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.concurrent.CompletableFuture;

import np.com.naxa.dms.R;
import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;

/**
 * https://github.com/appoly/ARCore-Location/issues/20
 */

public class Marker {

    private static String TAG = "Marker";

    public static void render(Context c, PointOfInterest couponInfo, LocationScene locationScene) {

        CompletableFuture<ViewRenderable> couponLayout =
                ViewRenderable.builder()
                        .setView(c, R.layout.ar_overlay_layout)
                        .build();


        CompletableFuture.allOf(couponLayout)
                .handle(
                        (notUsed, throwable) -> {

                            if (throwable != null) {
                                Log.i(TAG, "Unable to load renderables");
                                return null;
                            }

                            try {
                                // Non scalable info outside location
                                ViewRenderable vr = couponLayout.get();
                                Node base = new Node();
                                base.setRenderable(vr);
                                TextView title = vr.getView().findViewById(R.id.textView);

                                title.setText( couponInfo.getLabel() );


                                LocationMarker couponLocationMarker = new LocationMarker(
                                        couponInfo.getLon(),
                                        couponInfo.getLat(),
                                        base
                                );

                                couponLocationMarker.setRenderEvent(node -> {
                                    View eView = vr.getView();
                                    TextView distanceTextView = eView.findViewById(R.id.textView2);
                                    distanceTextView.setText(Math.round(node.getDistance()) + "M");
                                });



                                locationScene.mLocationMarkers.add( couponLocationMarker );

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            return null;
                        });
    }
}