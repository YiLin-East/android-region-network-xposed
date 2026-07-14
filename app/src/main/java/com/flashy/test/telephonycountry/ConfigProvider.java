package com.flashy.test.telephonycountry;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

/**
 * Read-only bridge from the module process to Hooked application processes.
 * The underlying SharedPreferences file remains private to this package.
 */
public final class ConfigProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        requireConfigUri(uri);
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("Configuration Provider is not attached to a Context");
        }
        HookConfig config = ConfigStore.read(context);
        MatrixCursor cursor = new MatrixCursor(ConfigContract.ALL_COLUMNS, 1);
        cursor.addRow(new Object[]{
                config.countryIso,
                config.localeTag,
                config.wifiSsid,
                config.wifiBssid,
                config.wifiIpv4,
                config.wifiEnabled ? 1 : 0,
                config.scanSucceeds ? 1 : 0,
                config.scanSsid,
                config.scanBssid,
                config.scanRssi,
                config.scanFrequency,
                config.scanCapabilities
        });
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        requireConfigUri(uri);
        return "vnd.android.cursor.item/vnd.com.flashy.test.telephonycountry.config";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Configuration Provider is read-only");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Configuration Provider is read-only");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Configuration Provider is read-only");
    }

    private static void requireConfigUri(Uri uri) {
        if (!ConfigContract.CONFIG_URI.equals(uri)) {
            throw new IllegalArgumentException("Unsupported configuration URI: " + uri);
        }
    }
}
