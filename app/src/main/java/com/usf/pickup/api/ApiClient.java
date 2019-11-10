package com.usf.pickup.api;

import android.content.Context;
import android.content.res.Resources;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.usf.pickup.R;
import com.usf.pickup.api.models.Game;
import com.usf.pickup.api.models.MyGames;
import com.usf.pickup.api.models.User;
import com.usf.pickup.api.models.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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

    private RequestQueue getRequestQueue() {
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
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                            listener.onResponse(ApiResult.<String>Error(response.getString("message")));
                            return;
                        } catch (JSONException ignored) {
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
                public byte[] getBody() {
                    return params.toString().getBytes(StandardCharsets.UTF_8);
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
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                            listener.onResponse(ApiResult.<User>Error(response.getString("message")));
                            return;
                        } catch (JSONException ignored) {
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
                        JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                        listener.onResponse(ApiResult.<User>Error(response.getString("message")));
                        return;
                    } catch (JSONException ignored) {
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
                        JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                        listener.onResponse(ApiResult.<List<String>>Error(response.getString("message")));
                        return;
                    } catch (JSONException ignored) {
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
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                            listener.onResponse(ApiResult.<Game>Error(response.getString("message")));
                            return;
                        } catch (JSONException ignored) {
                        }
                    }

                    listener.onResponse(ApiResult.<Game>Error(ctx.getString(R.string.create_failed)));
                }
            });

            addToRequestQueue(gsonRequest);
        } catch (JSONException ignored) {
        }
    }

    public void search(final String jwt, String sport, Integer maxDistance, LatLng latLng, boolean hideFull, boolean hideOngoing, Calendar startsBy, final ApiResult.Listener<Game[]> listener){
        try {
            String url = ctx.getResources().getString(R.string.api_url) +
                    ctx.getResources().getString(R.string.game_search);

            final JSONObject params = new JSONObject();
            final JSONArray locationArray = new JSONArray();
            locationArray.put(latLng.longitude);
            locationArray.put(latLng.latitude);

            params.put("sport", sport);
            params.put("maxDistance", maxDistance);
            params.put("location", locationArray);
            params.put("hideFull", hideFull);
            params.put("hideOngoing", hideOngoing);
            params.put("startsBy", startsBy.getTimeInMillis());

            GsonRequest<Game[]> gsonRequest = new GsonRequest<>(Request.Method.POST, url, Game[].class, params, Collections.singletonMap("Authorization", "Bearer " + jwt), new Response.Listener<Game[]>() {
                @Override
                public void onResponse(Game[] response) {
                    listener.onResponse(ApiResult.Success(response));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                            listener.onResponse(ApiResult.<Game[]>Error(response.getString("message")));
                            return;
                        } catch (JSONException ignored) {
                        }
                    }

                    listener.onResponse(ApiResult.<Game[]>Error(ctx.getString(R.string.search_failed)));
                }
            });

            addToRequestQueue(gsonRequest);
        } catch (JSONException ignored) {
        }
    }

    public void updateDisplayName(String jwt, String profileDescription, final ApiResult.Listener<User> listener) {
        try {
            String url = ctx.getResources().getString(R.string.api_url) +
                    ctx.getResources().getString(R.string.display_name);

            final JSONObject params = new JSONObject();
            params.put("displayName", profileDescription);

            GsonRequest<User> gsonRequest = new GsonRequest<>(Request.Method.PUT, url, User.class, params, Collections.singletonMap("Authorization", "Bearer " + jwt), new Response.Listener<User>() {
                @Override
                public void onResponse(User response) {
                    listener.onResponse(ApiResult.Success(response));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                            listener.onResponse(ApiResult.<User>Error(response.getString("message")));
                            return;
                        } catch (JSONException ignored) {
                        }
                    }

                    listener.onResponse(ApiResult.<User>Error(ctx.getString(R.string.update_name_failed)));
                }
            });

            addToRequestQueue(gsonRequest);
        } catch (JSONException ignored) {
        }
    }

    public void updateProfileDescription(String jwt, String profileDescription, final ApiResult.Listener<User> listener) {
        try {
            String url = ctx.getResources().getString(R.string.api_url) +
                    ctx.getResources().getString(R.string.profil_desc);

            final JSONObject params = new JSONObject();
            params.put("profileDescription", profileDescription);

            GsonRequest<User> gsonRequest = new GsonRequest<>(Request.Method.PUT, url, User.class, params, Collections.singletonMap("Authorization", "Bearer " + jwt), new Response.Listener<User>() {
                @Override
                public void onResponse(User response) {
                    listener.onResponse(ApiResult.Success(response));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                            listener.onResponse(ApiResult.<User>Error(response.getString("message")));
                            return;
                        } catch (JSONException ignored) {
                        }
                    }

                    listener.onResponse(ApiResult.<User>Error(ctx.getString(R.string.update_desc_failed)));
                }
            });

            addToRequestQueue(gsonRequest);
        } catch (JSONException ignored) {
        }
    }

    public void getMyGames(final String jwt, final ApiResult.Listener<MyGames> listener){
        try {
            String url = ctx.getResources().getString(R.string.api_url) +
                    ctx.getResources().getString(R.string.game);

            GsonRequest<MyGames> gsonRequest = new GsonRequest<>(Request.Method.GET, url, MyGames.class, null, Collections.singletonMap("Authorization", "Bearer " + jwt), new Response.Listener<MyGames>() {
                @Override
                public void onResponse(MyGames response) {
                    listener.onResponse(ApiResult.Success(response));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        try {
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                            listener.onResponse(ApiResult.<MyGames>Error(response.getString("message")));
                            return;
                        } catch (JSONException ignored) {
                        }
                    }

                    listener.onResponse(ApiResult.<MyGames>Error(ctx.getString(R.string.current_games_failed)));
                }
            });

            addToRequestQueue(gsonRequest);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void uploadProfilePicture(final String jwt, final byte[] image, final ApiResult.Listener<User> listener){
        String url = ctx.getResources().getString(R.string.api_url) +
                ctx.getResources().getString(R.string.profil_pic);

        ProfilePictureUploadRequest profilePictureUploadRequest = new ProfilePictureUploadRequest(url, image, Collections.singletonMap("Authorization", "Bearer " + jwt), new Response.Listener<User>() {
            @Override
            public void onResponse(User response) {
                listener.onResponse(ApiResult.Success(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse != null && error.networkResponse.data != null){
                    try {
                        JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                        listener.onResponse(ApiResult.<User>Error(response.getString("message")));
                        return;
                    } catch (JSONException ignored) {
                    }
                }

                listener.onResponse(ApiResult.<User>Error(ctx.getString(R.string.update_profile_pic_failed)));
            }
        });

        addToRequestQueue(profilePictureUploadRequest);
    }

    public void getWeather(final Game game, final ApiResult.Listener<Weather> listener){
        String url = ctx.getResources().getString(R.string.weather_api_url) + "?APPID=" +
                ctx.getResources().getString(R.string.weather_api_key) + "&lat=" +
                game.getLocation().getCoordinates()[1] + "&lon=" +
                game.getLocation().getCoordinates()[0] + "&units=imperial";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("cod").equals("200")){
                        JSONArray array = response.getJSONArray("list");
                        JSONObject obj = null;
                        for (int i = 0; i < array.length(); i++){
                            JSONObject row = array.getJSONObject(i);
                            Date time = new java.util.Date((long)row.getInt("dt")*1000);

                            if(time.after(game.getStartTime())){
                                obj = row;
                                break;
                            }
                        }

                        if(obj != null){
                            Weather weather = new Weather(obj.getJSONObject("main").getDouble("temp"),
                                    obj.getJSONObject("main").getInt("humidity"),
                                    obj.getJSONArray("weather").getJSONObject(0).getString("main"));

                            listener.onResponse(ApiResult.Success(weather));
                        }else {
                            listener.onResponse(ApiResult.<Weather>Error(ctx.getString(R.string.get_weather_failed)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onResponse(ApiResult.<Weather>Error(ctx.getString(R.string.get_weather_failed)));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onResponse(ApiResult.<Weather>Error(ctx.getString(R.string.get_weather_failed)));
            }
        });

        addToRequestQueue(jsonObjectRequest);
    }

    public void joinGame(final String jwt, final String gameId, final ApiResult.Listener<Game> listener){
        try {
            String url = ctx.getResources().getString(R.string.api_url) +
                    ctx.getResources().getString(R.string.join_game);

            final JSONObject params = new JSONObject();

            params.put("gameId", gameId);

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
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                            listener.onResponse(ApiResult.<Game>Error(response.getString("message")));
                            return;
                        } catch (JSONException ignored) {
                        }
                    }

                    listener.onResponse(ApiResult.<Game>Error(ctx.getString(R.string.join_failed)));
                }
            });

            addToRequestQueue(gsonRequest);
        } catch (JSONException ignored) {
        }
    }

    public void leaveGame(final String jwt, final String gameId, final ApiResult.Listener<Game> listener){
        try {
            String url = ctx.getResources().getString(R.string.api_url) +
                    ctx.getResources().getString(R.string.leave_game);

            final JSONObject params = new JSONObject();

            params.put("gameId", gameId);

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
                            JSONObject response = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));

                            listener.onResponse(ApiResult.<Game>Error(response.getString("message")));
                            return;
                        } catch (JSONException ignored) {
                        }
                    }

                    listener.onResponse(ApiResult.<Game>Error(ctx.getString(R.string.leave_failed)));
                }
            });

            addToRequestQueue(gsonRequest);
        } catch (JSONException ignored) {
        }
    }
}
