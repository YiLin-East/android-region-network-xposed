package com.flashy.test.telephonycountry;

import android.content.Context;
import android.content.SharedPreferences;

/** Private persistent storage used only by the module UI and its Provider. */
public final class ConfigStore {
    private static final String PREFERENCES_NAME = "hook_configuration";

    private ConfigStore() {
    }

    public static HookConfig read(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        HookConfig defaults = HookConfig.defaults();
        return new HookConfig(
                preferences.getString(ConfigContract.COUNTRY_ISO, defaults.countryIso),
                preferences.getString(ConfigContract.LOCALE_TAG, defaults.localeTag),
                preferences.getString(ConfigContract.WIFI_SSID, defaults.wifiSsid),
                preferences.getString(ConfigContract.WIFI_BSSID, defaults.wifiBssid),
                preferences.getString(ConfigContract.WIFI_IPV4, defaults.wifiIpv4),
                preferences.getBoolean(ConfigContract.WIFI_ENABLED, defaults.wifiEnabled),
                preferences.getBoolean(ConfigContract.SCAN_SUCCEEDS, defaults.scanSucceeds),
                preferences.getString(ConfigContract.SCAN_SSID, defaults.scanSsid),
                preferences.getString(ConfigContract.SCAN_BSSID, defaults.scanBssid),
                preferences.getInt(ConfigContract.SCAN_RSSI, defaults.scanRssi),
                preferences.getInt(ConfigContract.SCAN_FREQUENCY, defaults.scanFrequency),
                preferences.getString(ConfigContract.SCAN_CAPABILITIES, defaults.scanCapabilities)
        );
    }

    public static void write(Context context, HookConfig config) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(ConfigContract.COUNTRY_ISO, config.countryIso)
                .putString(ConfigContract.LOCALE_TAG, config.localeTag)
                .putString(ConfigContract.WIFI_SSID, config.wifiSsid)
                .putString(ConfigContract.WIFI_BSSID, config.wifiBssid)
                .putString(ConfigContract.WIFI_IPV4, config.wifiIpv4)
                .putBoolean(ConfigContract.WIFI_ENABLED, config.wifiEnabled)
                .putBoolean(ConfigContract.SCAN_SUCCEEDS, config.scanSucceeds)
                .putString(ConfigContract.SCAN_SSID, config.scanSsid)
                .putString(ConfigContract.SCAN_BSSID, config.scanBssid)
                .putInt(ConfigContract.SCAN_RSSI, config.scanRssi)
                .putInt(ConfigContract.SCAN_FREQUENCY, config.scanFrequency)
                .putString(ConfigContract.SCAN_CAPABILITIES, config.scanCapabilities)
                .apply();
    }

    public static void restoreDefaults(Context context) {
        write(context, HookConfig.defaults());
    }
}
