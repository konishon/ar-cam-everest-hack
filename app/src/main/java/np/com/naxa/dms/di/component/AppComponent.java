package np.com.naxa.dms.di.component;



import javax.inject.Singleton;

import dagger.Component;
import np.com.naxa.dms.di.module.AppModule;
import np.com.naxa.dms.navigate.ShareLocationActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(ShareLocationActivity client);
}