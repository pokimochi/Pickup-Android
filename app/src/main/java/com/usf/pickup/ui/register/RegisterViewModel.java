package com.usf.pickup.ui.register;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.usf.pickup.R;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.User;

import java.util.regex.Pattern;

public class RegisterViewModel extends AndroidViewModel {
    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<ApiResult<User>> registerResult = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    public MutableLiveData<ApiResult<User>> getRegisterResult() {
        return registerResult;
    }

    public void register(String email, String password, String confirmPassword, String displayName) {
        ApiClient.getInstance(getApplication()).register(email, password, confirmPassword, displayName, new ApiResult.Listener<User>() {
            @Override
            public void onResponse(ApiResult<User> response) {
                registerResult.setValue(response);
            }
        });
    }

    public void registerDataChanged(String email, String password, String confirmPassword, String displayName) {
        RegisterFormState register = new RegisterFormState();

        if (!isEmailValid(email)) {
            register.setEmailError(R.string.invalid_email);
        }

        if (!isDisplayNameValid(displayName)) {
            register.setDisplayNameError(R.string.invalid_display_name);
        }
        if (!isPasswordValid(password)) {
            register.setPasswordError(R.string.invalid_register_password);
        }

        if (!isConfirmPasswordValid(password, confirmPassword)) {
            register.setConfirmPasswordError(R.string.invalid_confirm_password);
        }

        registerFormState.setValue(register);
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isDisplayNameValid(String displayName) {
        return displayName != null && displayName.length() >= 2 && displayName.length() <= 30 &&
                Pattern.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$", displayName);
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }

    private boolean isConfirmPasswordValid(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }
}
