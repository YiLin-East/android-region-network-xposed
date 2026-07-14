package com.flashy.test.telephonycountry;

import android.net.wifi.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class TelephonyCountryHook implements IXposedHookLoadPackage {
    private static final String TAG = "TelephonyCountryHook";
    private static final String INDIA_COUNTRY_ISO = "in";
    private static final Locale INDIA_LOCALE = Locale.forLanguageTag("en-IN");
    private static final String FIXED_WIFI_SSID = "Office_5G_Test";
    private static final String FIXED_WIFI_BSSID = "02:1A:2B:3C:4D:5E";
    private static final String FIXED_WIFI_IPV4 = "203.0.113.25";

    // The app sees Wi-Fi as enabled so code that gates scanning on this value
    // continues. This does not enable the device radio or associate to an AP.
    private static final boolean REPORT_WIFI_ENABLED = true;
    private static final int FIXED_WIFI_IPV4_INT = ipv4ToWifiInfoInt(FIXED_WIFI_IPV4);
    private static final List<ScanResult> FIXED_SCAN_RESULTS = createScanResults();

    private static int ipv4ToWifiInfoInt(String address) {
        String[] octets = address.split("\\.");
        if (octets.length != 4) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + address);
        }

        int value = 0;
        for (int index = 0; index < octets.length; index++) {
            int octet = Integer.parseInt(octets[index]);
            if (octet < 0 || octet > 255) {
                throw new IllegalArgumentException("Invalid IPv4 address: " + address);
            }
            // WifiInfo.getIpAddress() uses the Android/little-endian layout.
            value |= octet << (index * 8);
        }
        return value;
    }

    private static List<ScanResult> createScanResults() {
        ScanResult result = new ScanResult();
        result.SSID = FIXED_WIFI_SSID;
        result.BSSID = FIXED_WIFI_BSSID;
        result.level = -48;
        result.frequency = 5180;
        result.capabilities = "[WPA2-PSK-CCMP][ESS]";

        List<ScanResult> results = new ArrayList<>();
        results.add(result);
        return results;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
            Log.i(TAG, "handleLoadPackage: " + loadPackageParam.packageName);
            XposedHelpers.findAndHookMethod(
                    "android.telephony.TelephonyManager",
                    loadPackageParam.classLoader,
                    "getNetworkCountryIso",
                    XC_MethodReplacement.returnConstant(INDIA_COUNTRY_ISO)
            );
            XposedHelpers.findAndHookMethod(
                    "android.telephony.TelephonyManager",
                    loadPackageParam.classLoader,
                    "getSimCountryIso",
                    XC_MethodReplacement.returnConstant(INDIA_COUNTRY_ISO)
            );
            XposedHelpers.findAndHookMethod(
                    "java.util.Locale",
                    null,
                    "getDefault",
                    XC_MethodReplacement.returnConstant(INDIA_LOCALE)
            );
            XposedHelpers.findAndHookMethod(
                    "java.util.Locale",
                    null,
                    "getDefault",
                    Locale.Category.class,
                    XC_MethodReplacement.returnConstant(INDIA_LOCALE)
            );
            XposedHelpers.findAndHookMethod(
                    "android.net.wifi.WifiInfo",
                    null,
                    "getSSID",
                    XC_MethodReplacement.returnConstant("\"" + FIXED_WIFI_SSID + "\"")
            );
            XposedHelpers.findAndHookMethod(
                    "android.net.wifi.WifiInfo",
                    null,
                    "getBSSID",
                    XC_MethodReplacement.returnConstant(FIXED_WIFI_BSSID)
            );
            XposedHelpers.findAndHookMethod(
                    "android.net.wifi.WifiInfo",
                    null,
                    "getIpAddress",
                    XC_MethodReplacement.returnConstant(FIXED_WIFI_IPV4_INT)
            );
            XposedHelpers.findAndHookMethod(
                    "android.net.wifi.WifiManager",
                    null,
                    "isWifiEnabled",
                    XC_MethodReplacement.returnConstant(REPORT_WIFI_ENABLED)
            );
            XposedHelpers.findAndHookMethod(
                    "android.net.wifi.WifiManager",
                    null,
                    "startScan",
                    XC_MethodReplacement.returnConstant(true)
            );
            XposedHelpers.findAndHookMethod(
                    "android.net.wifi.WifiManager",
                    null,
                    "getScanResults",
                    XC_MethodReplacement.returnConstant(FIXED_SCAN_RESULTS)
            );
            XposedBridge.log("TelephonyCountryHook active for " + loadPackageParam.packageName);
            Log.i(TAG, "Hook installed for " + loadPackageParam.packageName);
            Log.i(TAG, "Locale default now "
                    + Locale.getDefault().toLanguageTag()
                    + ", country="
                    + Locale.getDefault().getCountry());
            Log.i(TAG, "WiFi BSSID now " + FIXED_WIFI_BSSID);
            Log.i(TAG, "WiFi SSID now " + FIXED_WIFI_SSID);
            Log.i(TAG, "WiFi IPv4 now " + FIXED_WIFI_IPV4);
            Log.i(TAG, "WiFi scan results now contain " + FIXED_SCAN_RESULTS.size() + " item(s)");
        } catch (Throwable throwable) {
            XposedBridge.log("TelephonyCountryHook failed for "
                    + loadPackageParam.packageName
                    + ": "
                    + throwable);
            Log.e(TAG, "Hook failed for " + loadPackageParam.packageName, throwable);
        }
    }
}
