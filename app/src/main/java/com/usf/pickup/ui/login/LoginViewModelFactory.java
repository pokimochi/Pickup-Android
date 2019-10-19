package com.usf.pickup.ui.login;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public LoginViewModelFactory(@NonNull Application application) {
        super(application);
    }
}
