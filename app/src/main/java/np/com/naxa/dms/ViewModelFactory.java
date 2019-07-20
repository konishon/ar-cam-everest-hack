package np.com.naxa.dms;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;



import javax.inject.Inject;

import np.com.naxa.dms.navigate.ShareLocationViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private ShareLocationViewModel mShareLocationViewModel;

    @Inject
    public ViewModelFactory(ShareLocationViewModel shareLocationViewModel) {
        mShareLocationViewModel = shareLocationViewModel;
     }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShareLocationViewModel.class)) {
            return (T) mShareLocationViewModel;
        }

        throw new IllegalArgumentException("Unknown view model type");
    }
}
