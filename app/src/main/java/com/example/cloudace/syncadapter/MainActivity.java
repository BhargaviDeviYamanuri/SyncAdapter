package com.example.cloudace.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String ACCOUNT_TYPE="com.example.cloudace.syncadapter";
    private static final String AUTHORITY="com.example.cloudace.syncadapter";
    private static final String ACCOUNT_NAME="Sync";

    public TextView mIp,mCountryCod,mCountryName,mCity,mLatitude,mLongitude;
    CursorAdapter cursorAdapter;
    Account mAccount;
    private String TAG=this.getClass().getCanonicalName();
    ListView mListView;
    public SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mListView = (ListView) findViewById(R.id.list);
        mIp=(TextView)findViewById(R.id.txt_ip);
        mCountryCod=(TextView)findViewById(R.id.txt_country_code);
        mCountryName=(TextView)findViewById(R.id.txt_country_name);
        mCity=(TextView)findViewById(R.id.txt_city);
        mLatitude=(TextView)findViewById(R.id.txt_latitude);
        mLongitude=(TextView)findViewById(R.id.txt_longitude);
        mSharedPreferences=getSharedPreferences("MyIp",0);

//Using shared preference iam displaying values in text view.
        String txtIp=mSharedPreferences.getString("ipAdr","");
        String txtCC=mSharedPreferences.getString("CCode","");
        String txtCN=mSharedPreferences.getString("CName","");
        String txtC=mSharedPreferences.getString("City","");
        String txtLP=mSharedPreferences.getString("Latitude","");
        String txtLN=mSharedPreferences.getString("Longitude","");

        mIp.setText(txtIp);
        mCountryCod.setText(txtCC);
        mCountryName.setText(txtCN);
        mCity.setText(txtC);
        mLatitude.setText(txtLP);
        mLongitude.setText(txtLN);

        mAccount=createSyncAccount(this);
//In this code i am using content provider to save data.
           /* Cursor cursor=getContentResolver().query(MyIPContentProvider.CONTENT_URI,null,null,null,null);
            cursorAdapter=new SimpleCursorAdapter(this,R.layout.list_item,cursor,new String []{"ip","country_code","country_name","city","latitude","longitude"},
                                                                new int[] {R.id.txt_ip,R.id.txt_country_code,R.id.txt_country_name,R.id.txt_city,R.id.txt_latitude,R.id.txt_longitude},0);

            mListView.setAdapter(cursorAdapter);
            getContentResolver().registerContentObserver(MyIPContentProvider.CONTENT_URI,true,new StockContentObserver(new Handler()));
    */
        Bundle settingBundle=new Bundle();
        settingBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
        settingBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,true);
        ContentResolver.requestSync(mAccount,AUTHORITY,settingBundle);
        ContentResolver.setSyncAutomatically(mAccount,AUTHORITY,true);
        ContentResolver.addPeriodicSync(mAccount,AUTHORITY,Bundle.EMPTY,60);
    }

    private Account createSyncAccount(MainActivity mainActivity) {
        Account account=new Account(ACCOUNT_NAME,ACCOUNT_TYPE);
        AccountManager accountManager=(AccountManager)mainActivity.getSystemService(ACCOUNT_SERVICE);
        if(accountManager.addAccountExplicitly(account,null,null))
        {

        }else
        {

        }
        return account;
    }


    private class StockContentObserver extends ContentObserver {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(TAG, "CHANGE OBSERVED AT URI: " + uri);
            cursorAdapter.swapCursor(getContentResolver().query(MyIPContentProvider.CONTENT_URI, null, null, null, null));
        }

        public StockContentObserver(Handler handler) {
            super(handler);

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(syncStaredReceiver, new IntentFilter(SyncAdapter.SYNC_STARTED));
        registerReceiver(syncFinishedReceiver, new IntentFilter(SyncAdapter.SYNC_FINISHED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(syncStaredReceiver);
        unregisterReceiver(syncFinishedReceiver);
    }
    private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Sync finished!");
            Toast.makeText(getApplicationContext(), "Sync Finished", Toast.LENGTH_SHORT).show();
        }
    };
    private BroadcastReceiver syncStaredReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Sync started!");
            Toast.makeText(getApplicationContext(), "Sync started...", Toast.LENGTH_SHORT).show();
        }
    };
}

