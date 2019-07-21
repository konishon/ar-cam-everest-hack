package np.com.naxa.dms.di.module;

import android.app.Application;
import android.content.Context;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import np.com.naxa.dms.ViewModelFactory;
import np.com.naxa.dms.navigate.CustomBearingCompassViewmodel;

@Module
public class AppModule {

    private Application mApplication;

    public AppModule(Application app) {
        mApplication = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApplication;
    }


    @Provides
    @Singleton
    CustomBearingCompassViewmodel provideTrackLocationViewModel() {
        return new CustomBearingCompassViewmodel(mApplication);
    }

    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(
            CustomBearingCompassViewmodel shareLocationViewModel) {
        return new ViewModelFactory(shareLocationViewModel);
    }

}