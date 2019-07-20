package np.com.naxa.dms;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;



import javax.inject.Inject;

import np.com.naxa.dms.navigate.CustomBearingCompassViewmodel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private CustomBearingCompassViewmodel mShareLocationViewModel;

    @Inject
    public ViewModelFactory(CustomBearingCompassViewmodel shareLocationViewModel) {
        mShareLocationViewModel = shareLocationViewModel;
     }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CustomBearingCompassViewmodel.class)) {
            return (T) mShareLocationViewModel;
        }

        throw new IllegalArgumentException("Unknown view model type");
    }
}
