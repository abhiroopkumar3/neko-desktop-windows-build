package androidx.biometric;

import java.util.concurrent.Executor;

/** Windows Hello is a later port; authenticate() reports "no hardware" so the lock is skipped. */
public class BiometricPrompt {
    public static class AuthenticationResult {}

    public static class AuthenticationCallback {
        public void onAuthenticationError(int errorCode, CharSequence errString) {}
        public void onAuthenticationSucceeded(AuthenticationResult result) {}
        public void onAuthenticationFailed() {}
    }

    public static class PromptInfo {
        public static class Builder {
            public Builder setTitle(CharSequence t) { return this; }
            public Builder setSubtitle(CharSequence t) { return this; }
            public Builder setDescription(CharSequence t) { return this; }
            public Builder setNegativeButtonText(CharSequence t) { return this; }
            public Builder setAllowedAuthenticators(int a) { return this; }
            public Builder setConfirmationRequired(boolean v) { return this; }
            public PromptInfo build() { return new PromptInfo(); }
        }
    }

    public static final int ERROR_HW_NOT_PRESENT = 12;
    public static final int ERROR_NEGATIVE_BUTTON = 13;
    public static final int ERROR_USER_CANCELED = 10;
    public static final int ERROR_CANCELED = 5;
    public static final int ERROR_LOCKOUT = 7;

    private final AuthenticationCallback callback;

    public BiometricPrompt(androidx.fragment.app.FragmentActivity activity, Executor executor, AuthenticationCallback callback) {
        this.callback = callback;
    }

    public void authenticate(PromptInfo info) { callback.onAuthenticationError(ERROR_HW_NOT_PRESENT, "No biometric hardware"); }
    public void cancelAuthentication() {}
}
