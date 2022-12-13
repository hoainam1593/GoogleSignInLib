package org.hoppergames.library;

import android.app.Activity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;

import com.unity3d.player.UnityPlayer;

public class GoogleLoginActivity extends Activity {

    private static final int REQ_ONE_TAP = 2;

    private SignInClient oneTapClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oneTapClient = Identity.getSignInClient(this);
        BeginSignInRequest signInRequest = BuildSignInRequest(GoogleLogin.webClientId);
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                null, 0, 0, 0);
                    }
                    catch (IntentSender.SendIntentException e) {
                        String msg = "cannot start login UI, reason=" + e.getLocalizedMessage();
                        FinishFailed(msg);
                    }
                })
                .addOnFailureListener(this, e -> {
                    String msg = "sign in google fail, reason=" + e.getLocalizedMessage();
                    FinishFailed(msg);
                });
    }

    private static BeginSignInRequest BuildSignInRequest(String clientId){
        BeginSignInRequest.PasswordRequestOptions passwordOption = BeginSignInRequest.PasswordRequestOptions
                .builder()
                .setSupported(true)
                .build();
        BeginSignInRequest.GoogleIdTokenRequestOptions tokenOption = BeginSignInRequest.GoogleIdTokenRequestOptions
                .builder()
                .setSupported(true)
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(false)
                .build();
        return BeginSignInRequest
                .builder()
                .setPasswordRequestOptions(passwordOption)
                .setGoogleIdTokenRequestOptions(tokenOption)
                .setAutoSelectEnabled(true)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    FinishSuccess(idToken);
                } catch (ApiException e) {
                    String msg = "cannot get token, reason=" + e.getLocalizedMessage();
                    FinishFailed(msg);
                }
                break;
        }
    }

    private void FinishSuccess(String token){
        oneTapClient.signOut();
        finish();
        UnityPlayer.UnitySendMessage("MobirixLib_GoogleLogin", "GoogleLoginSuccess", token);
    }

    private void FinishFailed(String errMsg){
        oneTapClient.signOut();
        finish();
        UnityPlayer.UnitySendMessage("MobirixLib_GoogleLogin", "GoogleLoginFailure", errMsg);
    }
}