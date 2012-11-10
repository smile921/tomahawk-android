/* == This file is part of Tomahawk Player - <http://tomahawk-player.org> ===
 *
 *   Copyright 2012, Christopher Reichert <creichert07@gmail.com>
 *
 *   Tomahawk is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Tomahawk is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Tomahawk. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tomahawk.libtomahawk.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import com.codebutler.android_websockets.WebSocketClient;

/**
 * Represents a Tomahawk ControlConnection. Used for LAN communications.
 */
public class TomahawkService extends Service implements WebSocketClient.Listener {

    private final static String TAG = TomahawkService.class.getName();

    public static final String AUTH_URL = "http://auth.toma.hk/";
    public static final String HATCHET_URL = "ws://hatchet.toma.hk/";

    public static final String ACCOUNT_TYPE = "org.tomahawk";
    public static final String AUTH_TOKEN_TYPE = "org.tomahawk.authtoken";
    public static final String ACCOUNT_NAME = "Tomahawk";

    private final IBinder mBinder = new TomahawkServiceBinder();

    private WebSocketClient mWebSocketClient;
    private HandlerThread mCollectionUpdateHandlerThread;
    private Handler mHandler;

    private String mUserId;
    private String mAuthToken;

    private List<AccessToken> mAccessTokens;

    public static class TomahawkServiceConnection implements ServiceConnection {

        private TomahawkServiceConnectionListener mTomahawkServiceConnectionListener;

        public interface TomahawkServiceConnectionListener {
            public void setTomahawkService(TomahawkService ps);

            public void onTomahawkServiceReady();
        }

        public TomahawkServiceConnection(TomahawkServiceConnectionListener tomahawkServiceConnectedListener) {
            mTomahawkServiceConnectionListener = tomahawkServiceConnectedListener;
        }

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            TomahawkServiceBinder binder = (TomahawkServiceBinder) service;
            mTomahawkServiceConnectionListener.setTomahawkService(binder.getService());
            mTomahawkServiceConnectionListener.onTomahawkServiceReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mTomahawkServiceConnectionListener.setTomahawkService(null);
        }
    };

    /**
     * Runnable that requests accessTokens to start a Connection on.
     */
    private Runnable mStartupConnectionRunnable = new Runnable() {
        @Override
        public void run() {

            try {
                mAccessTokens = requestAccessTokens(mUserId, mAuthToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (mAccessTokens == null)
                return;

            mWebSocketClient = new WebSocketClient(URI.create(HATCHET_URL),
                                                   TomahawkService.this, null);
            mWebSocketClient.connect();
        }
    };

    public class TomahawkServiceBinder extends Binder {
        public TomahawkService getService() {
            return TomahawkService.this;
        }
    }

    private static class AccessToken {
        String token;
        String host;
        String type;
        int port;
        int expiration;

        AccessToken(String token, String host, String type, int port, int expiration) {
            this.token = token;
            this.host = host;
            this.type = type;
            this.port = port;
            this.expiration = expiration;
        }
    }

    @Override
    public int onStartCommand(Intent i, int j, int k) {
        super.onStartCommand(i, j, k);

        if (!i.hasExtra(ACCOUNT_NAME) || !i.hasExtra(AUTH_TOKEN_TYPE)) {
            stopSelf();
            return -1;
        }

        mUserId = i.getStringExtra(ACCOUNT_NAME);
        mAuthToken = i.getStringExtra(AUTH_TOKEN_TYPE);

        mCollectionUpdateHandlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        mCollectionUpdateHandlerThread.start();
        mHandler = new Handler(mCollectionUpdateHandlerThread.getLooper());

        mHandler.postDelayed(mStartupConnectionRunnable, 1);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Called when the websocket client has connected.
     */
    @Override
    public void onConnect() {
        Log.d(TAG, "Tomahawk websocket connected.");

        /** For testing we will attempt to register. */
        AccessToken token = mAccessTokens.get(0);

        JSONObject register = new JSONObject();
        try {
            register.put("command", "register");
            register.put("hostname", token.host);
            register.put("port", token.port);
            register.put("accesstoken", token.token);
            // register.put("username", mUserId);
            register.put("dbid", "nil");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebSocketClient.send(register.toString());
    }

    /**
     * Called when the websocket client has received a message.
     */
    @Override
    public void onMessage(String msg) {
        Log.d(TAG, "Message from Tomahawk server: " + msg);
    }

    /**
     * Called when the websocket client has received a binary message.
     */
    @Override
    public void onMessage(byte[] data) {
        Log.d(TAG, "Binary message from Tomahawk server.");
    }

    /**
     * Called when the websocket client has been disconnected.
     */
    @Override
    public void onDisconnect(int code, String reason) {
        Log.d(TAG, "Tomahawk websocket disconnected.");
    }

    /**
     * Called when an error has occurred with the websocket client.
     */
    @Override
    public void onError(Exception error) {
        throw new IllegalArgumentException(error.toString());
    }

    /**s
     * Requests access tokens for the given user id and valid auth token.
     * 
     * @param userid
     * @param authToken
     * @return
     * @throws JSONException
     */
    private static List<AccessToken> requestAccessTokens(String userid, String authToken) throws JSONException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", userid);
        params.put("authtoken", authToken);

        String json = post(new JSONObject(params));
        if (json == null)
            return null;

        List<AccessToken> accessTokens = new ArrayList<AccessToken>();
        JSONArray tokens = new JSONArray(json);
        for (int i = 0; i < tokens.length(); i++) {

            JSONObject host = tokens.getJSONObject(i);
            AccessToken token = new AccessToken(host.getString("token"), host.getString("host"), host.getString("type"), host.getInt("port"), host.getInt("expiration"));

            accessTokens.add(token);
        }

        return accessTokens;
    }

    /**
     * Authenticates the credentials against the Tomahawk server and return the
     * auth token.
     * 
     * @param username
     * @param password
     * @return auth token.
     */
    public static String authenticate(String name, String passwd) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", name);
        params.put("password", passwd);

        return post(new JSONObject(params));
    }

    /**
     * Post parameters to the Tomahawk server.
     * 
     * @param params
     * @return
     */
    @TargetApi(14)
    private static String post(JSONObject params) {

        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        TomahawkHttpClient httpclient = new TomahawkHttpClient(httpParams);

        String query = params.has("authtoken") ? "tokens" : "login";
        HttpPost httpost = new HttpPost(AUTH_URL + query);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            TrafficStats.setThreadStatsTag(0xF00D);

        try {

            httpost.setEntity(new StringEntity(params.toString()));

            httpost.setHeader("Accept", "application/json; charset=utf-8");
            httpost.setHeader("Content-type", "application/json; charset=utf-8");
            HttpResponse httpresponse = httpclient.execute(httpost);

            BufferedReader reader = null;
            JSONObject jsonObj = null;

            reader = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent(), "UTF-8"));
            String json = reader.readLine();
            jsonObj = new JSONObject(json);

            Log.d(TAG, "Tomahawk server response: " + jsonObj.toString());

            /* Test if an error occurred. */
            if (jsonObj.has("error") && jsonObj.getString("error").equals("true"))
                throw new IllegalArgumentException(jsonObj.getString("errormsg"));

            JSONObject msg = jsonObj.getJSONObject("message");

            if (msg.has("accesstokens"))
                return msg.getJSONArray("accesstokens").toString();

            String token = msg.getJSONObject("authtoken").getString("token");
            return token;

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                TrafficStats.clearThreadStatsTag();
        }

        Log.e(TAG, "Uknown error authenticating against Tomahawk server.");
        return null;
    }
}
