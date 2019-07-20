package np.com.naxa.dms.di.module;

import android.app.Application;
import android.content.Context;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import np.com.naxa.dms.ViewModelFactory;
import np.com.naxa.dms.navigate.ShareLocationViewModel;

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
    ShareLocationViewModel provideTrackLocationViewModel() {
        return new ShareLocationViewModel(mApplication);
    }

    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(
            ShareLocationViewModel shareLocationViewModel) {
        return new ViewModelFactory(shareLocationViewModel);
    }

}