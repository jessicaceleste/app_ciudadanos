package com.estilo.app_ciudadanos.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
/*Auxilio K66, Version 1
 * Applicación Móvil para Denuncias de Incidentes
 * Desarrollado por: @jessicaCoronel y @miliceSanabria
 * Fecha de Inicio: Mayo 2021
 * Fecha Fin: Octubre 2021*/

/**NOMBRE DE LA CLASE: App Controller***/
public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static AppController mInstance;
    private Request req1;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        this.req1 = req;
        req1.setTag( TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req1);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
