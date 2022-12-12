package org.hoppergames.library;

import android.app.Activity;
import android.content.Intent;

public class GoogleLogin {
    public static String webClientId;

    public static void Login(Activity activity, String clientId)
    {
        webClientId = clientId;

        Intent myIntent = new Intent(activity, GoogleLogin.class);
        activity.startActivity(myIntent);
    }
}
