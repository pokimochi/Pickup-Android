package com.usf.pickup.api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.usf.pickup.R;
import com.usf.pickup.api.models.Game;
import com.usf.pickup.api.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    private  <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void login(final String email, final String password, final ApiResult.Listener<String> listener){
        try {
            String url = ctx.getResources().getString(R.string.api_url) +
                    ctx.getResources().getString(R.string.auth_login);

            final JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    listener.onResponse(ApiResult.Success(response.replaceAll("^\"|\"$", "")));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, "UTF-8"));

                            listener.onResponse(ApiResult.<String>Error(response.getString("message")));
                            return;
                        } catch (JSONException | UnsupportedEncodingException ignored) {
                        }
                    }

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

    public void register(final String email, final String password, final String confirmPassword, final String displayName, final ApiResult.Listener<User> listener){
        try {
            String url = ctx.getResources().getString(R.string.api_url) +
                    ctx.getResources().getString(R.string.auth_register);

            final JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);
            params.put("confirmPassword", confirmPassword);
            params.put("displayName", displayName);

            GsonRequest<User> gsonRequest = new GsonRequest<>(Request.Method.POST, url, User.class, params, null, new Response.Listener<User>() {
                @Override
                public void onResponse(User response) {
                    listener.onResponse(ApiResult.Success(response));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, "UTF-8"));

                            listener.onResponse(ApiResult.<User>Error(response.getString("message")));
                            return;
                        } catch (JSONException | UnsupportedEncodingException ignored) {
                        }
                    }

                    listener.onResponse(ApiResult.<User>Error(ctx.getString(R.string.register_failed)));
                }
            });

            addToRequestQueue(gsonRequest);
        } catch (JSONException ignored) {
        }
    }

    public void getMyUser(String jwt, final ApiResult.Listener<User> listener){
        String url = ctx.getResources().getString(R.string.api_url) +
                ctx.getResources().getString(R.string.settings);

        GsonRequest<User> gsonRequest = new GsonRequest<>(Request.Method.GET, url, User.class, null, Collections.singletonMap("Authorization", "Bearer " + jwt), new Response.Listener<User>() {
            @Override
            public void onResponse(User response) {
                listener.onResponse(ApiResult.Success(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse != null && error.networkResponse.data != null){
                    try {
                        JSONObject response = new JSONObject(new String(error.networkResponse.data, "UTF-8"));

                        listener.onResponse(ApiResult.<User>Error(response.getString("message")));
                        return;
                    } catch (JSONException | UnsupportedEncodingException ignored) {
                    }
                }

                listener.onResponse(ApiResult.<User>Error(ctx.getString(R.string.get_settings_failed)));
            }
        });

        addToRequestQueue(gsonRequest);
    }

    public void getSports(final String jwt, final ApiResult.Listener<List<String>> listener){
        String url = ctx.getResources().getString(R.string.api_url) +
                ctx.getResources().getString(R.string.game_sports);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<String> list = new ArrayList<>();
                for(int i = 0; i < response.length(); i++){
                    try {
                        list.add(response.getString(i));
                    } catch (JSONException ignored) {
                    }
                }
                listener.onResponse(ApiResult.Success(list));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse != null && error.networkResponse.data != null){
                    try {
                        JSONObject response = new JSONObject(new String(error.networkResponse.data, "UTF-8"));

                        listener.onResponse(ApiResult.<List<String>>Error(response.getString("message")));
                        return;
                    } catch (JSONException | UnsupportedEncodingException ignored) {
                    }
                }

                listener.onResponse(ApiResult.<List<String>>Error(ctx.getString(R.string.get_sports_failed)));
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                return Collections.singletonMap("Authorization", "Bearer " + jwt);
            }
        };

        addToRequestQueue(jsonArrayRequest);
    }

    public void createGame(final String jwt, final String title, final String sport, final int numberOfPlayers, final String address, final String locationName, final LatLng location, final String description, final Calendar startTime, final Calendar endTime, final ApiResult.Listener<Game> listener){
        try {
            String url = ctx.getResources().getString(R.string.api_url) +
                    ctx.getResources().getString(R.string.game);

            final JSONObject params = new JSONObject();
            final JSONArray locationArray = new JSONArray();
            locationArray.put(location.longitude);
            locationArray.put(location.latitude);

            params.put("title", title);
            params.put("sport", sport);
            params.put("numberOfPlayers", numberOfPlayers);
            params.put("address", address);
            params.put("locationName", locationName);
            params.put("location",  locationArray);
            params.put("description", description);
            params.put("startTime", startTime.getTimeInMillis());
            params.put("endTime", endTime.getTimeInMillis());

            GsonRequest<Game> gsonRequest = new GsonRequest<>(Request.Method.POST, url, Game.class, params, Collections.singletonMap("Authorization", "Bearer " + jwt), new Response.Listener<Game>() {
                @Override
                public void onResponse(Game response) {
                    listener.onResponse(ApiResult.Success(response));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, "UTF-8"));

                            listener.onResponse(ApiResult.<Game>Error(response.getString("message")));
                            return;
                        } catch (JSONException | UnsupportedEncodingException ignored) {
                        }
                    }

                    listener.onResponse(ApiResult.<Game>Error(ctx.getString(R.string.create_failed)));
                }
            });

            addToRequestQueue(gsonRequest);
        } catch (JSONException ignored) {
        }
    }
}
