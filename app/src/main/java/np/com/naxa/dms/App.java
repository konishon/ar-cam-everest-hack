package np.com.naxa.dms;



import android.app.Application;

import np.com.naxa.dms.di.component.AppComponent;
import np.com.naxa.dms.di.component.DaggerAppComponent;
import np.com.naxa.dms.di.module.AppModule;
import timber.log.Timber;


public class App extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        AppModule appModule = new AppModule(this);
        mAppComponent = DaggerAppComponent.builder().appModule(appModule).build();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}

