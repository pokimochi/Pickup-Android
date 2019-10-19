package com.usf.pickup.ui.login;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.usf.pickup.R;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;

public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<ApiResult<String>> loginResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<ApiResult<String>> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        ApiClient.getInstance(getApplication()).login(username, password, new ApiResult.Listener<String>() {
            @Override
            public void onResponse(ApiResult<String> response) {
                loginResult.setValue(response);
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        LoginFormState login = new LoginFormState();

        if (!isUserNameValid(username)) {
            login.setUsernameError(R.string.invalid_username);
        }

        if (!isPasswordValid(password)) {
            login.setPasswordError(R.string.invalid_password);
        }

        loginFormState.setValue(login);
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }

        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && !password.isEmpty();
    }
}
