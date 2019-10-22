package com.usf.pickup.ui.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.usf.pickup.R;

public class FingerprintAuthenticationDialogFragment extends DialogFragment {
    private ImageView icon;
    private TextView errorTextView;
    private Callback callback;
    private FingerprintManager.CryptoObject cryptoObject;
    private CancellationSignal cancellationSignal;
    private boolean selfCancelled;
    private static final long ERROR_TIMEOUT_MILLIS = 1600L;
    private static final long SUCCESS_DELAY_MILLIS = 1300L;

    private Runnable resetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            icon.setImageResource(R.drawable.ic_fp_40px);
            errorTextView.setTextColor(getResources().getColor(R.color.hint_color, null));
            errorTextView.setText(getString(R.string.fingerprint_hint));
        }
    };

    public FingerprintAuthenticationDialogFragment(FingerprintManager.CryptoObject cryptoObject, Callback callback) {
        this.callback = callback;
        this.cryptoObject = cryptoObject;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FingerPrintDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if(dialog != null) dialog.setTitle("Login");

        return inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        icon = view.findViewById(R.id.fingerprint_icon);
        errorTextView = view.findViewById(R.id.fingerprint_status);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if(!isFingerPrintAuthAvailable(getActivity())){
            callback.onError();
            dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();

        if(activity != null){
            FingerprintManager fingerprintManager = activity.getSystemService(FingerprintManager.class);

            if(fingerprintManager != null && isFingerPrintAuthAvailable(getActivity())){
                cancellationSignal = new CancellationSignal();
                selfCancelled = false;
                fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);

                        if (!selfCancelled) {
                            showError(errString);
                            icon.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError();
                                    dismiss();
                                }
                            }, ERROR_TIMEOUT_MILLIS);
                        }
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);

                        showError(helpString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(final FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        errorTextView.removeCallbacks(resetErrorTextRunnable);
                        errorTextView.setTextColor(getResources().getColor(R.color.success_color, null));
                        errorTextView.setText(getString(R.string.fingerprint_success));
                        icon.setImageResource(R.drawable.ic_fingerprint_success);
                        icon.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callback.onAuthenticated(result.getCryptoObject());
                                dismiss();
                            }
                        }, SUCCESS_DELAY_MILLIS);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();

                        showError(getString(R.string.fingerprint_not_recognized));
                    }
                }, null);
                icon.setImageResource(R.drawable.ic_fp_40px);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (cancellationSignal != null) {
            selfCancelled = true;
            cancellationSignal.cancel();
        }

        cancellationSignal = null;
    }

    private void showError(CharSequence error){
        icon.setImageResource(R.drawable.ic_fingerprint_error);
        errorTextView.setText(error);
        errorTextView.setTextColor(getResources().getColor(R.color.warning_color, null));
        errorTextView.removeCallbacks(resetErrorTextRunnable);
        errorTextView.postDelayed(resetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    public static boolean isFingerPrintAuthAvailable(@Nullable Context ctx){
        if(ctx != null){
            FingerprintManager fingerprintManager = ctx.getSystemService(FingerprintManager.class);

            return fingerprintManager != null && fingerprintManager.isHardwareDetected() &&
                    fingerprintManager.hasEnrolledFingerprints();
        }

        return false;
    }

    public interface Callback {
        void onAuthenticated(FingerprintManager.CryptoObject cryptoObject);
        void onError();
    }
}
