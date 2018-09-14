package com.example.jason.fabflixproject4;
import android.content.Context;
import android.media.MediaCas;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;

public class SessionManager {
    private static SessionManager instance = null;
    RequestQueue queue;

    private SessionManager() {
        NukeSSLCerts.nuke();  // disable ssl cert self-sign check
    }

    static SessionManager sharedManager(Context ctx) {
        if (instance == null) {
            instance = new SessionManager();
            instance.queue = Volley.newRequestQueue(ctx.getApplicationContext());

            // Create a new cookie store, which handles sessions information with the server.
            // This cookie store will be shared across all the network requests.
            CookieHandler.setDefault(new CookieManager());
        }
        return instance;
    }
}