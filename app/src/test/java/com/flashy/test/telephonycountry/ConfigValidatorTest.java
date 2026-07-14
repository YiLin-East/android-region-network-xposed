package com.flashy.test.telephonycountry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConfigValidatorTest {
    @Test
    public void defaultsAreValidAndRetainExistingBehavior() {
        HookConfig defaults = HookConfig.defaults();
        ConfigValidator.ValidationResult result = ConfigValidator.validate(draftFrom(defaults));

        assertTrue(result.isValid());
        assertEquals("in", result.config.countryIso);
        assertEquals("en-IN", result.config.localeTag);
        assertEquals("Office_5G_Test", result.config.wifiSsid);
        assertEquals("203.0.113.25", result.config.wifiIpv4);
    }

    @Test
    public void normalizesCountryAndBssid() {
        HookConfig defaults = HookConfig.defaults();
        HookConfig.Draft draft = new HookConfig.Draft(
                "US", "en-us", defaults.wifiSsid, "aa:bb:cc:dd:ee:ff", defaults.wifiIpv4,
                true, true, defaults.scanSsid, defaults.scanBssid,
                String.valueOf(defaults.scanRssi), String.valueOf(defaults.scanFrequency), defaults.scanCapabilities
        );

        ConfigValidator.ValidationResult result = ConfigValidator.validate(draft);

        assertTrue(result.isValid());
        assertEquals("us", result.config.countryIso);
        assertEquals("en-US", result.config.localeTag);
        assertEquals("AA:BB:CC:DD:EE:FF", result.config.wifiBssid);
    }

    @Test
    public void rejectsInvalidNetworkValues() {
        HookConfig defaults = HookConfig.defaults();
        HookConfig.Draft invalidIp = new HookConfig.Draft(
                defaults.countryIso, defaults.localeTag, defaults.wifiSsid, defaults.wifiBssid, "300.0.0.1",
                true, true, defaults.scanSsid, defaults.scanBssid,
                String.valueOf(defaults.scanRssi), String.valueOf(defaults.scanFrequency), defaults.scanCapabilities
        );
        HookConfig.Draft invalidRssi = new HookConfig.Draft(
                defaults.countryIso, defaults.localeTag, defaults.wifiSsid, defaults.wifiBssid, defaults.wifiIpv4,
                true, true, defaults.scanSsid, defaults.scanBssid,
                "1", String.valueOf(defaults.scanFrequency), defaults.scanCapabilities
        );

        ConfigValidator.ValidationResult ipResult = ConfigValidator.validate(invalidIp);
        ConfigValidator.ValidationResult rssiResult = ConfigValidator.validate(invalidRssi);

        assertFalse(ipResult.isValid());
        assertEquals(ConfigValidator.FIELD_WIFI_IPV4, ipResult.field);
        assertFalse(rssiResult.isValid());
        assertEquals(ConfigValidator.FIELD_SCAN_RSSI, rssiResult.field);
    }

    private static HookConfig.Draft draftFrom(HookConfig config) {
        return new HookConfig.Draft(
                config.countryIso,
                config.localeTag,
                config.wifiSsid,
                config.wifiBssid,
                config.wifiIpv4,
                config.wifiEnabled,
                config.scanSucceeds,
                config.scanSsid,
                config.scanBssid,
                String.valueOf(config.scanRssi),
                String.valueOf(config.scanFrequency),
                config.scanCapabilities
        );
    }
}
