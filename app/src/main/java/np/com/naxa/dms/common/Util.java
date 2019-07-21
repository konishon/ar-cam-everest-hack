package np.com.naxa.dms.common;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public final class Util {

    public static final int REQ_PERM_CODE = 9874;

    public static boolean checkLocationPermission(Activity activity) {
        // check permission untuk lokasi (fine/coarse)
        // runtime permission checking ini digunakan untuk android 6.0 (marshmellow) ke atas
        int fineLoc = ContextCompat
                .checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLoc = ContextCompat
                .checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (fineLoc != PackageManager.PERMISSION_GRANTED ||
                coarseLoc != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_PERM_CODE);
            return false;
        }

        return true;
    }

    public static boolean checkLocationPermissionsResult(int requestCode,
                                                         @NonNull String[] permissions,
                                                         @NonNull int[] grantResults) {
        return requestCode == REQ_PERM_CODE &&
                grantResults.length >= 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED;
    }

}
