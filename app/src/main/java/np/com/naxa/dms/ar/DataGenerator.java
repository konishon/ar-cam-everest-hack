package np.com.naxa.dms.ar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataGenerator {
    static String data = "[{\n" +
            "  \"label\":\"hall\",\n" +
            "  \"lat\":  27.71975041,\n" +
            "  \"lon\": 85.3838494\n" +
            "},\n" +
            "{\n" +
            "  \"label\":\"eco park\",\n" +
            "  \"lat\":  27.7197799,\n" +
            "  \"lon\": 85.3837391\n" +
            "},\n" +
            "{\n" +
            "  \"label\":\"nami board\",\n" +
            "  \"lat\":  27.71982946,\n" +
            "  \"lon\": 85.38343253\n" +
            "},\n" +
            "{\n" +
            "  \"label\":\"tukcha kamaldi\",\n" +
            "  \"lat\":  27.707306,\n" +
            "  \"lon\": 85.321749\n" +
            "},\n" +
            "{\n" +
            "  \"label\":\"parking\",\n" +
            "  \"lat\":  27.71977095,\n" +
            "  \"lon\": 85.3832425\n" +
            "}\n" +
            "]\n";


    public static ArrayList<PointOfInterest> getData() throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        ArrayList<PointOfInterest> pointOfInterests = new ArrayList<>();
        PointOfInterest pointOfInterest = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String label = obj.getString("label");
            double lat = obj.getDouble("lat");
            double lon = obj.getDouble("lon");

            pointOfInterest = new PointOfInterest(lat, lon, label);
            pointOfInterests.add(pointOfInterest);
        }

        return pointOfInterests;
    }
}
