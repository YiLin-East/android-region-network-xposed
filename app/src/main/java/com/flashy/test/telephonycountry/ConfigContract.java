package com.flashy.test.telephonycountry;

import android.net.Uri;

public final class ConfigContract {
    public static final String AUTHORITY = "com.flashy.test.telephonycountry.config";
    public static final String PATH_CONFIG = "config";
    public static final Uri CONFIG_URI = new Uri.Builder()
            .scheme("content")
            .authority(AUTHORITY)
            .appendPath(PATH_CONFIG)
            .build();

    public static final String COUNTRY_ISO = "country_iso";
    public static final String LOCALE_TAG = "locale_tag";
    public static final String WIFI_SSID = "wifi_ssid";
    public static final String WIFI_BSSID = "wifi_bssid";
    public static final String WIFI_IPV4 = "wifi_ipv4";
    public static final String WIFI_ENABLED = "wifi_enabled";
    public static final String SCAN_SUCCEEDS = "scan_succeeds";
    public static final String SCAN_SSID = "scan_ssid";
    public static final String SCAN_BSSID = "scan_bssid";
    public static final String SCAN_RSSI = "scan_rssi";
    public static final String SCAN_FREQUENCY = "scan_frequency";
    public static final String SCAN_CAPABILITIES = "scan_capabilities";

    public static final String[] ALL_COLUMNS = {
            COUNTRY_ISO,
            LOCALE_TAG,
            WIFI_SSID,
            WIFI_BSSID,
            WIFI_IPV4,
            WIFI_ENABLED,
            SCAN_SUCCEEDS,
            SCAN_SSID,
            SCAN_BSSID,
            SCAN_RSSI,
            SCAN_FREQUENCY,
            SCAN_CAPABILITIES
    };

    private ConfigContract() {
    }
}
