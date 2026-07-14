package com.flashy.test.telephonycountry;

import android.content.Context;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class TelephonyCountryHook implements IXposedHookLoadPackage {
    private static final String TAG = "TelephonyCountryHook";
    private static final String MODULE_PACKAGE = "com.flashy.test.telephonycountry";
    private static final Object INSTALL_LOCK = new Object();
    private static boolean hooksInstalled;
    private static boolean attachHookRegistered;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (MODULE_PACKAGE.equals(loadPackageParam.packageName)) {
            return;
        }

        Context availableContext = findAvailableContext();
        if (availableContext != null) {
            installHooks(availableContext, loadPackageParam.classLoader, loadPackageParam.packageName);
        }
        if (!hooksInstalled) {
            registerApplicationAttachHook(loadPackageParam.classLoader, loadPackageParam.packageName);
        }
    }

    private static void registerApplicationAttachHook(ClassLoader classLoader, String packageName) {
        synchronized (INSTALL_LOCK) {
            if (hooksInstalled || attachHookRegistered) {
                return;
            }
            try {
                XposedHelpers.findAndHookMethod(
                        "android.app.Application",
                        null,
                        "attach",
                        Context.class,
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) {
                                if (param.args == null || param.args.length == 0 || !(param.args[0] instanceof Context)) {
                                    return;
                                }
                                installHooks((Context) param.args[0], classLoader, packageName);
                            }
                        }
                );
                attachHookRegistered = true;
            } catch (Throwable throwable) {
                logFailure("Could not register Application.attach", packageName, throwable);
            }
        }
    }

    private static void installHooks(Context context, ClassLoader classLoader, String packageName) {
        synchronized (INSTALL_LOCK) {
            if (hooksInstalled) {
                return;
            }
            HookConfig config = readConfig(context, packageName);
            try {
                Locale locale = Locale.forLanguageTag(config.localeTag);
                int wifiIpv4 = ipv4ToWifiInfoInt(config.wifiIpv4);
                List<ScanResult> scanResults = config.scanSucceeds
                        ? createScanResults(config)
                        : Collections.emptyList();

                XposedHelpers.findAndHookMethod(
                        "android.telephony.TelephonyManager",
                        classLoader,
                        "getNetworkCountryIso",
                        XC_MethodReplacement.returnConstant(config.countryIso)
                );
                XposedHelpers.findAndHookMethod(
                        "android.telephony.TelephonyManager",
                        classLoader,
                        "getSimCountryIso",
                        XC_MethodReplacement.returnConstant(config.countryIso)
                );
                XposedHelpers.findAndHookMethod(
                        "java.util.Locale",
                        null,
                        "getDefault",
                        XC_MethodReplacement.returnConstant(locale)
                );
                XposedHelpers.findAndHookMethod(
                        "java.util.Locale",
                        null,
                        "getDefault",
                        Locale.Category.class,
                        XC_MethodReplacement.returnConstant(locale)
                );
                XposedHelpers.findAndHookMethod(
                        "android.net.wifi.WifiInfo",
                        null,
                        "getSSID",
                        XC_MethodReplacement.returnConstant("\"" + config.wifiSsid + "\"")
                );
                XposedHelpers.findAndHookMethod(
                        "android.net.wifi.WifiInfo",
                        null,
                        "getBSSID",
                        XC_MethodReplacement.returnConstant(config.wifiBssid)
                );
                XposedHelpers.findAndHookMethod(
                        "android.net.wifi.WifiInfo",
                        null,
                        "getIpAddress",
                        XC_MethodReplacement.returnConstant(wifiIpv4)
                );
                XposedHelpers.findAndHookMethod(
                        "android.net.wifi.WifiManager",
                        null,
                        "isWifiEnabled",
                        XC_MethodReplacement.returnConstant(config.wifiEnabled)
                );
                XposedHelpers.findAndHookMethod(
                        "android.net.wifi.WifiManager",
                        null,
                        "startScan",
                        XC_MethodReplacement.returnConstant(config.scanSucceeds)
                );
                XposedHelpers.findAndHookMethod(
                        "android.net.wifi.WifiManager",
                        null,
                        "getScanResults",
                        XC_MethodReplacement.returnConstant(scanResults)
                );
                hooksInstalled = true;
                XposedBridge.log(TAG + " active for " + packageName + " with country=" + config.countryIso
                        + ", locale=" + config.localeTag + ", Wi-Fi SSID=" + config.wifiSsid);
                Log.i(TAG, "Hook installed for " + packageName);
            } catch (Throwable throwable) {
                logFailure("Hook installation failed", packageName, throwable);
            }
        }
    }

    private static HookConfig readConfig(Context context, String packageName) {
        try (Cursor cursor = context.getContentResolver().query(ConfigContract.CONFIG_URI, null, null, null, null)) {
            if (cursor == null || !cursor.moveToFirst()) {
                throw new IllegalStateException("Configuration Provider returned no data");
            }
            HookConfig.Draft draft = new HookConfig.Draft(
                    value(cursor, ConfigContract.COUNTRY_ISO),
                    value(cursor, ConfigContract.LOCALE_TAG),
                    value(cursor, ConfigContract.WIFI_SSID),
                    value(cursor, ConfigContract.WIFI_BSSID),
                    value(cursor, ConfigContract.WIFI_IPV4),
                    cursor.getInt(cursor.getColumnIndexOrThrow(ConfigContract.WIFI_ENABLED)) != 0,
                    cursor.getInt(cursor.getColumnIndexOrThrow(ConfigContract.SCAN_SUCCEEDS)) != 0,
                    value(cursor, ConfigContract.SCAN_SSID),
                    value(cursor, ConfigContract.SCAN_BSSID),
                    String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ConfigContract.SCAN_RSSI))),
                    String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ConfigContract.SCAN_FREQUENCY))),
                    value(cursor, ConfigContract.SCAN_CAPABILITIES)
            );
            ConfigValidator.ValidationResult result = ConfigValidator.validate(draft);
            if (!result.isValid()) {
                throw new IllegalStateException("Invalid " + result.field + " from Configuration Provider: " + result.message);
            }
            return result.config;
        } catch (Throwable throwable) {
            logFailure("Could not load configuration; using defaults", packageName, throwable);
            return HookConfig.defaults();
        }
    }

    private static String value(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndexOrThrow(column));
    }

    private static Context findAvailableContext() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Method currentApplication = activityThread.getDeclaredMethod("currentApplication");
            currentApplication.setAccessible(true);
            Object application = currentApplication.invoke(null);
            if (application instanceof Context) {
                return (Context) application;
            }

            Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object thread = currentActivityThread.invoke(null);
            if (thread == null) {
                return null;
            }
            Method getSystemContext = activityThread.getDeclaredMethod("getSystemContext");
            getSystemContext.setAccessible(true);
            Object systemContext = getSystemContext.invoke(thread);
            return systemContext instanceof Context ? (Context) systemContext : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

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

    private static List<ScanResult> createScanResults(HookConfig config) {
        ScanResult result = new ScanResult();
        result.SSID = config.scanSsid;
        result.BSSID = config.scanBssid;
        result.level = config.scanRssi;
        result.frequency = config.scanFrequency;
        result.capabilities = config.scanCapabilities;

        List<ScanResult> results = new ArrayList<>();
        results.add(result);
        return results;
    }

    private static void logFailure(String action, String packageName, Throwable throwable) {
        String message = TAG + " " + action + " for " + packageName + ": " + throwable;
        XposedBridge.log(message);
        Log.e(TAG, message, throwable);
    }
}
