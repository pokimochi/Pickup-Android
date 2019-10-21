package com.usf.pickup.ui.login;

import android.app.Application;
import android.util.Pair;
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
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(final String email, final String password) {
        ApiClient.getInstance(getApplication()).login(email, password, new ApiResult.Listener<String>() {
            @Override
            public void onResponse(ApiResult<String> response) {
                loginResult.setValue(new LoginResult(response, email, password));
            }
        });
    }

    public void loginDataChanged(String email, String password) {
        LoginFormState login = new LoginFormState();

        if (!isEmailValid(email)) {
            login.setEmailError(R.string.invalid_email);
        }

        if (!isPasswordValid(password)) {
            login.setPasswordError(R.string.invalid_password);
        }

        loginFormState.setValue(login);
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && !password.isEmpty();
    }

    public static class LoginResult{
        private ApiResult<String> apiResult;
        private String email;
        private String password;

        public LoginResult(ApiResult<String> apiResult, String email, String password) {
            this.apiResult = apiResult;
            this.email = email;
            this.password = password;
        }

        public ApiResult<String> getApiResult() {
            return apiResult;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }
}
