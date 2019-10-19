package com.usf.pickup.api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usf.pickup.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ApiClient {
    private static ApiClient instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private ApiClient(Context context){
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void login(String email, String password, final ApiResult.Listener<String> listener){
        try {
            String url = ctx.getResources().getString(R.string.api_url) +
                    ctx.getResources().getString(R.string.auth_login);

            final JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    listener.onResponse(ApiResult.Success(response));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onResponse(ApiResult.<String>Error(ctx.getString(R.string.login_failed)));
                }
            }){
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return params.toString().getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", params, "utf-8");
                        return null;
                    }
                }
            };

            addToRequestQueue(stringRequest);
        } catch (JSONException ignored) {
        }
    }
}
