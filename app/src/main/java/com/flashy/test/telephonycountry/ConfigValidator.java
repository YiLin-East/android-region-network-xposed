package com.flashy.test.telephonycountry;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/** Validates UI and Provider values before they become Hook return values. */
public final class ConfigValidator {
    public static final String FIELD_COUNTRY_ISO = "countryIso";
    public static final String FIELD_LOCALE_TAG = "localeTag";
    public static final String FIELD_WIFI_SSID = "wifiSsid";
    public static final String FIELD_WIFI_BSSID = "wifiBssid";
    public static final String FIELD_WIFI_IPV4 = "wifiIpv4";
    public static final String FIELD_SCAN_SSID = "scanSsid";
    public static final String FIELD_SCAN_BSSID = "scanBssid";
    public static final String FIELD_SCAN_RSSI = "scanRssi";
    public static final String FIELD_SCAN_FREQUENCY = "scanFrequency";
    public static final String FIELD_SCAN_CAPABILITIES = "scanCapabilities";

    private ConfigValidator() {
    }

    public static ValidationResult validate(HookConfig.Draft draft) {
        try {
            String countryIso = requireCountryIso(draft.countryIso);
            String localeTag = requireLocaleTag(draft.localeTag);
            String wifiSsid = requireSsid(draft.wifiSsid, FIELD_WIFI_SSID);
            String wifiBssid = requireBssid(draft.wifiBssid, FIELD_WIFI_BSSID);
            String wifiIpv4 = requireIpv4(draft.wifiIpv4);
            String scanSsid = requireSsid(draft.scanSsid, FIELD_SCAN_SSID);
            String scanBssid = requireBssid(draft.scanBssid, FIELD_SCAN_BSSID);
            int scanRssi = requireInt(draft.scanRssi, FIELD_SCAN_RSSI, -127, 0, "RSSI 必须介于 -127 和 0 之间。");
            int scanFrequency = requireInt(draft.scanFrequency, FIELD_SCAN_FREQUENCY, 2400, 7125, "频率必须介于 2400 和 7125 MHz 之间。");
            String scanCapabilities = requireCapabilities(draft.scanCapabilities);
            return ValidationResult.success(new HookConfig(
                    countryIso,
                    localeTag,
                    wifiSsid,
                    wifiBssid,
                    wifiIpv4,
                    draft.wifiEnabled,
                    draft.scanSucceeds,
                    scanSsid,
                    scanBssid,
                    scanRssi,
                    scanFrequency,
                    scanCapabilities
            ));
        } catch (ValidationException exception) {
            return ValidationResult.failure(exception.field, exception.getMessage());
        }
    }

    private static String requireCountryIso(String value) throws ValidationException {
        String normalized = trim(value);
        if (!normalized.matches("[A-Za-z]{2}")) {
            throw new ValidationException(FIELD_COUNTRY_ISO, "国家码必须是两个英文字母，例如 in。");
        }
        return normalized.toLowerCase(Locale.ROOT);
    }

    private static String requireLocaleTag(String value) throws ValidationException {
        String normalized = trim(value);
        Locale locale = Locale.forLanguageTag(normalized);
        if (normalized.isEmpty() || locale.getLanguage().isEmpty() || "und".equals(locale.toLanguageTag())) {
            throw new ValidationException(FIELD_LOCALE_TAG, "请输入有效的 BCP-47 区域标签，例如 en-IN。");
        }
        return locale.toLanguageTag();
    }

    private static String requireSsid(String value, String field) throws ValidationException {
        String normalized = trim(value);
        if (normalized.isEmpty() || normalized.getBytes(StandardCharsets.UTF_8).length > 32) {
            throw new ValidationException(field, "SSID 不能为空，且 UTF-8 编码不能超过 32 字节。");
        }
        return normalized;
    }

    private static String requireBssid(String value, String field) throws ValidationException {
        String normalized = trim(value);
        if (!normalized.matches("(?i)([0-9a-f]{2}:){5}[0-9a-f]{2}")) {
            throw new ValidationException(field, "BSSID 必须为 AA:BB:CC:DD:EE:FF 格式。");
        }
        return normalized.toUpperCase(Locale.ROOT);
    }

    private static String requireIpv4(String value) throws ValidationException {
        String normalized = trim(value);
        String[] octets = normalized.split("\\.", -1);
        if (octets.length != 4) {
            throw new ValidationException(FIELD_WIFI_IPV4, "请输入有效的 IPv4 地址。");
        }
        for (String octet : octets) {
            try {
                if (octet.isEmpty() || (octet.length() > 1 && octet.startsWith("+")) || Integer.parseInt(octet) < 0 || Integer.parseInt(octet) > 255) {
                    throw new ValidationException(FIELD_WIFI_IPV4, "请输入有效的 IPv4 地址。");
                }
            } catch (NumberFormatException exception) {
                throw new ValidationException(FIELD_WIFI_IPV4, "请输入有效的 IPv4 地址。");
            }
        }
        return normalized;
    }

    private static int requireInt(String value, String field, int minimum, int maximum, String message) throws ValidationException {
        try {
            int parsed = Integer.parseInt(trim(value));
            if (parsed < minimum || parsed > maximum) {
                throw new ValidationException(field, message);
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new ValidationException(field, message);
        }
    }

    private static String requireCapabilities(String value) throws ValidationException {
        String normalized = trim(value);
        if (normalized.isEmpty() || normalized.length() > 256) {
            throw new ValidationException(FIELD_SCAN_CAPABILITIES, "能力字符串不能为空，且不能超过 256 个字符。");
        }
        return normalized;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static final class ValidationResult {
        public final HookConfig config;
        public final String field;
        public final String message;

        private ValidationResult(HookConfig config, String field, String message) {
            this.config = config;
            this.field = field;
            this.message = message;
        }

        public static ValidationResult success(HookConfig config) {
            return new ValidationResult(config, null, null);
        }

        public static ValidationResult failure(String field, String message) {
            return new ValidationResult(null, field, message);
        }

        public boolean isValid() {
            return config != null;
        }
    }

    private static final class ValidationException extends Exception {
        final String field;

        ValidationException(String field, String message) {
            super(message);
            this.field = field;
        }
    }
}
