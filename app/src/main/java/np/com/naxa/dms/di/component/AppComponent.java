package np.com.naxa.dms.di.component;



import javax.inject.Singleton;

import dagger.Component;
import np.com.naxa.dms.ar.ARCompassActivity;
import np.com.naxa.dms.di.module.AppModule;
import np.com.naxa.dms.navigate.CustomBearingCompassActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(CustomBearingCompassActivity client);
    void inject(ARCompassActivity client);
}