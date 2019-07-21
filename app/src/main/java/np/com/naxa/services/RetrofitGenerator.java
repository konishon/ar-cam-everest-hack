package np.com.naxa.services;

import android.text.TextUtils;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import np.com.naxa.dms.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

public class RetrofitGenerator {

    private static Retrofit INSTANCE = null;

    private RetrofitGenerator() {
    }

    public static Retrofit instance() {
        if (INSTANCE == null) {
            INSTANCE = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .client(createOkHttpClient())
                    .build();
        }
        return INSTANCE;

    }

    private static Interceptor createAuthInterceptor(final String token) {
        return chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("x-access-token",
                            token)
                    .build();
            return chain.proceed(request);
        };
    }
//    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
//        String userProfile = new Pref(MainApplication.context).getPreferences(Pref.LOGIN);
////        String user_token = "";
////        try {
////            if (!TextUtils.isEmpty(userProfile))
////                user_token = new JSONObject(userProfile).optString("session_token");
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        okHttpClientBuilder.addInterceptor(createAuthInterceptor(user_token));
//        okHttpClientBuilder.connectTimeout(10, TimeUnit.SECONDS);
//        okHttpClientBuilder.writeTimeout(3600, TimeUnit.SECONDS);
//        okHttpClientBuilder.readTimeout(3600, TimeUnit.SECONDS);
//        if (BuildConfig.DEBUG) {
//            okHttpClientBuilder.addNetworkInterceptor(new Stethointer());
//        }
//
//        return okHttpClientBuilder
//                .build();
//    }
}
