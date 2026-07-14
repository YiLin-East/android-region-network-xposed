package com.flashy.test.telephonycountry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ConfigProviderTest {
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        ConfigStore.restoreDefaults(context);
    }

    @After
    public void tearDown() {
        ConfigStore.restoreDefaults(context);
    }

    @Test
    public void queryReturnsSavedConfiguration() {
        HookConfig saved = new HookConfig(
                "us", "en-US", "LabWifi", "12:34:56:78:9A:BC", "198.51.100.10",
                false, true, "TestAP", "02:11:22:33:44:55", -65, 2412, "[ESS]"
        );
        ConfigStore.write(context, saved);

        try (Cursor cursor = context.getContentResolver().query(ConfigContract.CONFIG_URI, null, null, null, null)) {
            if (cursor == null || !cursor.moveToFirst()) {
                fail("Provider returned no configuration row");
            }
            assertEquals("us", cursor.getString(cursor.getColumnIndexOrThrow(ConfigContract.COUNTRY_ISO)));
            assertEquals("LabWifi", cursor.getString(cursor.getColumnIndexOrThrow(ConfigContract.WIFI_SSID)));
            assertEquals(-65, cursor.getInt(cursor.getColumnIndexOrThrow(ConfigContract.SCAN_RSSI)));
        }
    }

    @Test
    public void providerRejectsWrites() {
        try {
            context.getContentResolver().update(ConfigContract.CONFIG_URI, new ContentValues(), null, null);
            fail("Read-only Provider accepted an update");
        } catch (UnsupportedOperationException expected) {
            // Expected: only the module's private ConfigStore can write values.
        }
    }
}
