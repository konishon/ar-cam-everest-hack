package np.com.naxa.dms;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import np.com.naxa.dms.navigate.Locator;
import np.com.naxa.services.ApiInterfaceService;
import np.com.naxa.services.RetrofitGenerator;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class HelpMainActivity extends AppCompatActivity {
    Retrofit retrofit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_main);
        setSupportActionBar(findViewById(R.id.mtoolbar));
        // get the list of open spaces
        retrofit = RetrofitGenerator.instance();


        Locator locator = new Locator(this);
        locator.getLocation(Locator.Method.GPS, new Locator.Listener() {
            @Override
            public void onLocationFound(Location location) {
                HashMap<String, String> locationMap = new HashMap<>();
                locationMap.put("lat", location.getLatitude()+"");
                locationMap.put("long", location.getLongitude()+"");
                Log.d("dms", "Found Location");
                fetchDataFromOnline(locationMap);
            }

            @Override
            public void onLocationNotFound() {
//                we can send message to first nearest ones in person
                Log.d("dms", "Found not Location");
                updateView(Utils.data);
            }
        });



    }

    private void fetchDataFromOnline(HashMap<String, String> queryParams) {
        getOpenSpaceList(queryParams).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.d("HelpMainActivity", "response = " + s);
                String data = s;
                updateView(s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("HelpMain", e.getMessage());
                String data = Utils.data;
                updateView(data);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void updateView(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            RecyclerView recyclerView = findViewById(R.id.rv_opne_space_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new OpenSpaceRecyclerViewAdapter(jsonArray));
        }catch (Exception e) {e. printStackTrace();

        }
    }

    private Observable<String> getOpenSpaceList(HashMap<String, String> queryParams) {
        return retrofit.create(ApiInterfaceService.class).getOpenSpace(queryParams)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                })
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody response) throws Exception {
                        return response.string();
                    }
                });
    }

}

class OpenSpaceRecyclerViewAdapter extends RecyclerView.Adapter<OpenSpaceViewHolder> {
    JSONArray jsonArray;

    public OpenSpaceRecyclerViewAdapter(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public OpenSpaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OpenSpaceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_open_space, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OpenSpaceViewHolder holder, int position) {
            try{
                JSONObject jsonObject = jsonArray.optJSONObject(position);
                holder.tv_openspace_name.setText(jsonObject.optString("name"));
                holder.tv_openspace_distance.setText(jsonObject.optString("distance"));
                holder.tv_openspace_city.setText(TextUtils.isEmpty(jsonObject.optString("city"))? "": jsonObject.optString("city"));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        open in map
                    }
                });
            } catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }
}




class OpenSpaceViewHolder extends RecyclerView.ViewHolder {
    TextView tv_openspace_name, tv_openspace_city, tv_openspace_distance;
    public OpenSpaceViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_openspace_city = itemView.findViewById(R.id.tv_openspace_city);
        tv_openspace_name = itemView.findViewById(R.id.tv_openspace_name);
        tv_openspace_distance = itemView.findViewById(R.id.tv_openspace_distance);
    }
}