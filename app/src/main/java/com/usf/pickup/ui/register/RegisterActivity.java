package com.usf.pickup.ui.register;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.usf.pickup.R;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.User;

public class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerViewModel = ViewModelProviders.of(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(RegisterViewModel.class);

        final EditText emailEditText = findViewById(R.id.email_register_text_box);
        final EditText displayNameEditText = findViewById(R.id.display_name_register_text_box);
        final EditText passwordEditText = findViewById(R.id.password_register_text_box);
        final EditText confirmPasswordEditText = findViewById(R.id.confirm_password_text_box);
        final Button registerSubmitButton = findViewById(R.id.register_submit_button);
        final ProgressBar loadingProgressBar = findViewById(R.id.register_loading);

        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }
                registerSubmitButton.setEnabled(registerFormState.isDataValid());

                // Clear existing errors
                emailEditText.setError(null);
                passwordEditText.setError(null);
                confirmPasswordEditText.setError(null);
                displayNameEditText.setError(null);

                if (registerFormState.getEmailError() != null) {
                    emailEditText.setError(getString(registerFormState.getEmailError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getConfirmPasswordError() != null) {
                    confirmPasswordEditText.setError(getString(registerFormState.getConfirmPasswordError()));
                }
                if (registerFormState.getDisplayNameError() != null) {
                    displayNameEditText.setError(getString(registerFormState.getDisplayNameError()));
                }
            }
        });


        registerViewModel.getRegisterResult().observe(this, new Observer<ApiResult<User>>() {
            @Override
            public void onChanged(ApiResult<User> loginResult) {
                if (loginResult == null) {
                    return;
                }

                loadingProgressBar.setVisibility(View.GONE);
                if(loginResult.isSuccess()){
                    showRegisterSuccess(loginResult.getData());

                    //Complete and destroy login activity once successful
                    setResult(Activity.RESULT_OK);
                    finish();
                }else {
                    showRegisterFailed(loginResult.getErrorMessage());
                }
            }
        });

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
                registerViewModel.registerDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        confirmPasswordEditText.getText().toString(),
                        displayNameEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener);
        displayNameEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                RegisterFormState formState = registerViewModel.getRegisterFormState().getValue();
                if (actionId == EditorInfo.IME_ACTION_DONE && formState != null &&
                        formState.isDataValid()) {
                    registerViewModel.register(emailEditText.getText().toString(),
                            passwordEditText.getText().toString(),
                            confirmPasswordEditText.getText().toString(),
                            displayNameEditText.getText().toString());
                }
                return false;
            }
        });

        registerSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                registerViewModel.register(emailEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        confirmPasswordEditText.getText().toString(),
                        displayNameEditText.getText().toString());
            }
        });
    }

    private void showRegisterSuccess(User user){
        Toast.makeText(getApplicationContext(), String.format("User \"%s\" successfully created", user.getDisplayName()), Toast.LENGTH_SHORT).show();
    }

    private void showRegisterFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
