package np.com.naxa.dms.ar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
                                vr.getView().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });

                                title.setText(couponInfo.getLabel());


                                LocationMarker couponLocationMarker = new LocationMarker(
                                        couponInfo.getLon(),
                                        couponInfo.getLat(),
                                        base
                                );


                                couponLocationMarker.setRenderEvent(node -> {
                                    View eView = vr.getView();
                                    TextView distanceTextView = eView.findViewById(R.id.textView2);
                                    int[] heightWidht = mapDistanceToHeightWidht(node.getDistance());
                                    vr.getView().setLayoutParams(new LinearLayout.LayoutParams(heightWidht[0], heightWidht[1]));

                                    distanceTextView.setText(Math.round(node.getDistance()) + "M");
                                });

                                locationScene.mLocationMarkers.add(couponLocationMarker);


                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            return null;
                        });
    }

    private static int[] mapDistanceToHeightWidht(int distance) {
        int[] heightWidht = new int[2];
        if (distance <= 50) {
            heightWidht[0] = 394;
            heightWidht[1] = 394;

        } else if (distance <= 150) {
            heightWidht[0] = 344;
            heightWidht[1] = 344;

        } else if (distance <= 250) {
            heightWidht[0] = 294;
            heightWidht[1] = 294;

        } else if (distance <= 350) {
            heightWidht[0] = 244;
            heightWidht[1] = 244;

        } else if (distance <= 450) {
            heightWidht[0] = 194;
            heightWidht[1] = 194;

        } else {
            heightWidht[0] = 144;
            heightWidht[1] = 144;
        }
        return heightWidht;
    }
}