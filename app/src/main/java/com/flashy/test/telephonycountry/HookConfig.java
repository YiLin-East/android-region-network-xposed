package com.flashy.test.telephonycountry;

/** Immutable configuration returned to Hooked processes. */
public final class HookConfig {
    public static final String DEFAULT_COUNTRY_ISO = "in";
    public static final String DEFAULT_LOCALE_TAG = "en-IN";
    public static final String DEFAULT_WIFI_SSID = "Office_5G_Test";
    public static final String DEFAULT_WIFI_BSSID = "02:1A:2B:3C:4D:5E";
    public static final String DEFAULT_WIFI_IPV4 = "203.0.113.25";
    public static final boolean DEFAULT_WIFI_ENABLED = true;
    public static final boolean DEFAULT_SCAN_SUCCEEDS = true;
    public static final int DEFAULT_SCAN_RSSI = -48;
    public static final int DEFAULT_SCAN_FREQUENCY = 5180;
    public static final String DEFAULT_SCAN_CAPABILITIES = "[WPA2-PSK-CCMP][ESS]";

    public final String countryIso;
    public final String localeTag;
    public final String wifiSsid;
    public final String wifiBssid;
    public final String wifiIpv4;
    public final boolean wifiEnabled;
    public final boolean scanSucceeds;
    public final String scanSsid;
    public final String scanBssid;
    public final int scanRssi;
    public final int scanFrequency;
    public final String scanCapabilities;

    HookConfig(
            String countryIso,
            String localeTag,
            String wifiSsid,
            String wifiBssid,
            String wifiIpv4,
            boolean wifiEnabled,
            boolean scanSucceeds,
            String scanSsid,
            String scanBssid,
            int scanRssi,
            int scanFrequency,
            String scanCapabilities
    ) {
        this.countryIso = countryIso;
        this.localeTag = localeTag;
        this.wifiSsid = wifiSsid;
        this.wifiBssid = wifiBssid;
        this.wifiIpv4 = wifiIpv4;
        this.wifiEnabled = wifiEnabled;
        this.scanSucceeds = scanSucceeds;
        this.scanSsid = scanSsid;
        this.scanBssid = scanBssid;
        this.scanRssi = scanRssi;
        this.scanFrequency = scanFrequency;
        this.scanCapabilities = scanCapabilities;
    }

    public static HookConfig defaults() {
        return new HookConfig(
                DEFAULT_COUNTRY_ISO,
                DEFAULT_LOCALE_TAG,
                DEFAULT_WIFI_SSID,
                DEFAULT_WIFI_BSSID,
                DEFAULT_WIFI_IPV4,
                DEFAULT_WIFI_ENABLED,
                DEFAULT_SCAN_SUCCEEDS,
                DEFAULT_WIFI_SSID,
                DEFAULT_WIFI_BSSID,
                DEFAULT_SCAN_RSSI,
                DEFAULT_SCAN_FREQUENCY,
                DEFAULT_SCAN_CAPABILITIES
        );
    }

    public static final class Draft {
        public final String countryIso;
        public final String localeTag;
        public final String wifiSsid;
        public final String wifiBssid;
        public final String wifiIpv4;
        public final boolean wifiEnabled;
        public final boolean scanSucceeds;
        public final String scanSsid;
        public final String scanBssid;
        public final String scanRssi;
        public final String scanFrequency;
        public final String scanCapabilities;

        public Draft(
                String countryIso,
                String localeTag,
                String wifiSsid,
                String wifiBssid,
                String wifiIpv4,
                boolean wifiEnabled,
                boolean scanSucceeds,
                String scanSsid,
                String scanBssid,
                String scanRssi,
                String scanFrequency,
                String scanCapabilities
        ) {
            this.countryIso = countryIso;
            this.localeTag = localeTag;
            this.wifiSsid = wifiSsid;
            this.wifiBssid = wifiBssid;
            this.wifiIpv4 = wifiIpv4;
            this.wifiEnabled = wifiEnabled;
            this.scanSucceeds = scanSucceeds;
            this.scanSsid = scanSsid;
            this.scanBssid = scanBssid;
            this.scanRssi = scanRssi;
            this.scanFrequency = scanFrequency;
            this.scanCapabilities = scanCapabilities;
        }
    }
}
