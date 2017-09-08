package com.example.cloudace.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Cloudace on 08-09-2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    ContentResolver mContentResolver;
    Context mContext;
    public static final String SYNC_STARTED="Sync Started";
    public static final String SYNC_FINISHED="Sync Finished";
    private static final String TAG=SyncAdapter.class.getCanonicalName();
    public SharedPreferences mSharedPreferences;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContext=context;
        mContentResolver=context.getContentResolver();
        Log.i("SyncAdapter","SyncAdapter");
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        Intent intent = new Intent(SYNC_STARTED);
        mContext.sendBroadcast(intent);

        Log.i(TAG, "onPerformSync");

        intent = new Intent(SYNC_FINISHED);
        mContext.sendBroadcast(intent);
        mSharedPreferences =mContext.getSharedPreferences("MyIp",0);
        SharedPreferences.Editor editor=mSharedPreferences.edit();

        mContentResolver.delete(MyIPContentProvider.CONTENT_URI,null,null);

        String data="";

        try {
            URL url =new URL("https://freegeoip.net/json/");
            Log.d(TAG, "URL :"+url);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            Log.d(TAG,"Connection :"+connection);
            connection.connect();
            Log.d(TAG,"Connection 1:"+connection);
            InputStream inputStream=connection.getInputStream();
            data=getInputData(inputStream);
            Log.d(TAG,"Data :"+data);

            if (data != null || !data.equals("null")) {
                JSONObject jsonObject = new JSONObject(data);


                String ipa = jsonObject.getString("ip");
                String country_code = jsonObject.getString("country_code");
                String country_name = jsonObject.getString("country_name");
                String region_code=jsonObject.getString("region_code");
                String region_name=jsonObject.getString("region_name");
                String zip_code=jsonObject.getString("zip_code");
                String time_zone=jsonObject.getString("time_zone");
                String metro_code=jsonObject.getString("metro_code");

                String city = jsonObject.getString("city");
                String latitude = jsonObject.getString("latitude");
                String longitude = jsonObject.getString("longitude");
           /* ContentValues values = new ContentValues();
            values.put("ip", ipa);
            values.put("country_code", country_code);
            values.put("country_name", country_name);
            values.put("city", city);
            values.put("latitude", latitude);
            values.put("longitude", longitude);*/
                //Using cursor adapter for results.
                //mContentResolver.insert(MyIPContentProvider.CONTENT_URI, values);

                //Using Shared preference for results.
                editor.putString("ipAdr",ipa);
                editor.putString("CCode",country_code);
                editor.putString("CName",country_name);
                editor.putString("City",city);
                editor.putString("Latitude",latitude);
                editor.putString("Longitude",longitude);
                editor.commit();

            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private String getInputData(InputStream inputStream) throws IOException {
        StringBuilder builder=new StringBuilder();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
        //String data=null;
    /*Log.d(TAG,"Builder 2:"+ bufferedReader.readLine());
    while ((data=bufferedReader.readLine())!= null);
    {
        builder.append(data);
        Log.d(TAG,"Builder :"+data);
    }
    Log.d(TAG,"Builder 1 :"+data);
    bufferedReader.close();*/
        String data=bufferedReader.readLine();
        bufferedReader.close();
        return data.toString();
    }
}
