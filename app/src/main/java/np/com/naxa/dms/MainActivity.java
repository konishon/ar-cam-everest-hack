package np.com.naxa.dms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import np.com.naxa.dms.ar.ARMarkerActivity;
import np.com.naxa.dms.ar.ARCompassActivity;
import np.com.naxa.dms.compass.CompassActivity;
import np.com.naxa.dms.navigate.CustomBearingCompassActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_normal_compass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CompassActivity.class));
            }
        });

        findViewById(R.id.btn_location_compass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CustomBearingCompassActivity.class));
            }
        });

        findViewById(R.id.btn_ar_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ARMarkerActivity.class));
            }
        });
        findViewById(R.id.btn_ar_camera_with_compass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ARCompassActivity.class));

            }
        });
    }


}
