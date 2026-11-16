package androidx.biometric.auth

import androidx.fragment.app.FragmentActivity

/** App lock authentication: Windows Hello support is later work; for now there is no prompt, so the lock stays off. */
abstract class AuthPromptCallback {
  open fun onAuthenticationError(activity: FragmentActivity?, errorCode: Int, errString: CharSequence) {}
  open fun onAuthenticationSucceeded(activity: FragmentActivity?, result: androidx.biometric.BiometricPrompt.AuthenticationResult) {}
  open fun onAuthenticationFailed(activity: FragmentActivity?) {}
}

fun FragmentActivity.startClass2BiometricOrCredentialAuthentication(
    title: CharSequence,
    subtitle: CharSequence? = null,
    description: CharSequence? = null,
    confirmationRequired: Boolean = true,
    executor: java.util.concurrent.Executor? = null,
    callback: AuthPromptCallback,
) {
  callback.onAuthenticationError(this, androidx.biometric.BiometricPrompt.ERROR_HW_NOT_PRESENT, "No authentication available")
}
