package com.example.cloudace.syncadapter;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import static java.security.AccessController.getContext;

/**
 * Created by Cloudace on 08-09-2017.
 */

public class MyIPContentProvider extends ContentProvider {

    public static final int IP_DATA=1;
    private static final String AUTHORITY="sample.map.com.ipsyncadapter";
    private static final String TABLE_IP_DATA="ip_data";
    public static final Uri CONTENT_URI=Uri.parse("content://" + AUTHORITY + '/' + TABLE_IP_DATA);
    private static final UriMatcher URI_MATCHER= new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        URI_MATCHER.addURI(AUTHORITY,TABLE_IP_DATA,IP_DATA);
    }

    private IpDataDBHelper myDB;

    @Override
    public boolean onCreate() {
        myDB=new IpDataDBHelper(getContext(),null,null,1);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        int uriType=URI_MATCHER.match(uri);
        Cursor cursor=null;
        switch (uriType)
        {
            case IP_DATA:
                cursor=myDB.getAllIpData();
                break;
            default:
                throw new IllegalArgumentException("UNKNOWN URL");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType=URI_MATCHER.match(uri);
        long id=0;
        switch (uriType)
        {
            case IP_DATA:
                id=myDB.AddIPData(contentValues);
                break;
            default:
                throw new IllegalArgumentException("UNKNOWN URI :" +uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return Uri.parse(contentValues + "/" + id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int uriType=URI_MATCHER.match(uri);
        int rowsDeleted=0;

        switch (uriType)
        {
            case IP_DATA:
                rowsDeleted=myDB.deleteAllIpData();
                break;
            default:
                throw new IllegalArgumentException("UNKNOWN URI :" +uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
