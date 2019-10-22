package com.usf.pickup.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.usf.pickup.BottomNav;
import com.usf.pickup.ForgetPassword;
import com.usf.pickup.R;
import com.usf.pickup.helpers.FingerprintEncryptionHelper;
import com.usf.pickup.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity implements FingerprintAuthenticationDialogFragment.Callback {
    public static final String HIDE_FINGERPRINT_DIALOG = "HIDE_FINGERPRINT_DIALOG";

    private final String KEY_EMAIL = "com.usf.pickup.ui.login.email";
    private final String KEY_PASSWORD = "com.usf.pickup.ui.login.password";
    private final String KEY_REMEMBER_ME = "com.usf.pickup.ui.login.rememberMe";
    private final String DIALOG_FRAGMENT_TAG = "com.usf.pickup.ui.login.dialog";
    private LoginViewModel loginViewModel;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(LoginViewModel.class);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final EditText emailEditText = findViewById(R.id.email_text_box);
        final CheckBox rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        final EditText passwordEditText = findViewById(R.id.password_text_box);
        final Button loginButton = findViewById(R.id.login_button);
        final Button registerButton = findViewById(R.id.register_button);
        final Button forgetButton = findViewById(R.id.forget_password_button);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final ImageButton fingerprintButton = findViewById(R.id.fingerprint_auth_button);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());

                // Clear existing errors
                emailEditText.setError(null);
                passwordEditText.setError(null);

                if (loginFormState.getEmailError() != null) {
                    emailEditText.setError(getString(loginFormState.getEmailError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });


        loginViewModel.getLoginResult().observe(this, new Observer<LoginViewModel.LoginResult>() {
            @Override
            public void onChanged(LoginViewModel.LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }

                loadingProgressBar.setVisibility(View.GONE);
                if(loginResult.getApiResult().isSuccess()){
                    startActivity(new Intent(LoginActivity.this, BottomNav.class));

                    // Encrypt + Save the password that was successful
                    if(rememberMeCheckbox.isChecked()){
                        String encrpyted = FingerprintEncryptionHelper.encryptString(loginResult.getPassword());
                        preferences.edit().putString(KEY_EMAIL, loginResult.getEmail()).apply();
                        preferences.edit().putString(KEY_PASSWORD, encrpyted).apply();
                    }else{
                        preferences.edit().remove(KEY_EMAIL).apply();
                        preferences.edit().remove(KEY_PASSWORD).apply();
                    }

                    //Complete and destroy login activity once successful
                    setResult(Activity.RESULT_OK);
                    finish();
                }else {
                    showError(loginResult.getApiResult().getErrorMessage());
                }
            }
        });

        if(preferences.contains(KEY_REMEMBER_ME)){
            boolean rememberMe = preferences.getBoolean(KEY_REMEMBER_ME, false);
            rememberMeCheckbox.setChecked(rememberMe);

            if(rememberMe && preferences.contains(KEY_EMAIL)){
                String email = preferences.getString(KEY_EMAIL, null);
                emailEditText.setText(email);
            }
        }

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LoginFormState formState = loginViewModel.getLoginFormState().getValue();
                if (actionId == EditorInfo.IME_ACTION_DONE && formState != null &&
                        formState.isDataValid()) {
                    loginViewModel.login(emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        rememberMeCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;

                preferences.edit().putBoolean(KEY_REMEMBER_ME,  checkBox.isChecked()).apply();

                if(!checkBox.isChecked()){
                    preferences.edit().remove(KEY_EMAIL).apply();
                    preferences.edit().remove(KEY_PASSWORD).apply();
                }
            }
        });

        fingerprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runFingerPrintAuth(false);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPassword.class));
            }
        });

        Intent intent = getIntent();
        if(intent == null || !intent.getBooleanExtra(HIDE_FINGERPRINT_DIALOG, false)){
            runFingerPrintAuth(true);
        }
    }

    private void runFingerPrintAuth(boolean silent){
        FingerprintAuthenticationDialogFragment dialogFragment =
                new FingerprintAuthenticationDialogFragment(
                        FingerprintEncryptionHelper.getCryptoObject(), this);

        if(preferences.contains(KEY_EMAIL) && preferences.contains(KEY_PASSWORD)){
            if(FingerprintAuthenticationDialogFragment.isFingerPrintAuthAvailable(this)){
                dialogFragment.show(getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            }else {
                if (!silent) showError(getString(R.string.fingerprint_auth_unavailable_hardware));
            }
        }else {
            if (!silent) showError(getString(R.string.fingerprint_auth_unavailable_user));
        }
    }

    private void showError(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticated(FingerprintManager.CryptoObject cryptoObject) {
        String email = preferences.getString(KEY_EMAIL, null);
        String encryptedPass = preferences.getString(KEY_PASSWORD, null);
        String pass = FingerprintEncryptionHelper.decryptString(encryptedPass, cryptoObject.getCipher());

        loginViewModel.login(email, pass);
    }

    @Override
    public void onError() {

    }
}
