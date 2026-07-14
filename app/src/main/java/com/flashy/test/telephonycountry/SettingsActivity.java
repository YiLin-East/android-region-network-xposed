package com.flashy.test.telephonycountry;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public final class SettingsActivity extends Activity {
    private EditText countryIso;
    private EditText localeTag;
    private EditText wifiSsid;
    private EditText wifiBssid;
    private EditText wifiIpv4;
    private Switch wifiEnabled;
    private Switch scanSucceeds;
    private EditText scanSsid;
    private EditText scanBssid;
    private EditText scanRssi;
    private EditText scanFrequency;
    private EditText scanCapabilities;
    private Map<String, EditText> fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bindViews();
        load(ConfigStore.read(this));

        ((Button) findViewById(R.id.save_button)).setOnClickListener(view -> save());
        ((Button) findViewById(R.id.restore_defaults_button)).setOnClickListener(view -> restoreDefaults());
    }

    private void bindViews() {
        countryIso = findViewById(R.id.country_iso);
        localeTag = findViewById(R.id.locale_tag);
        wifiSsid = findViewById(R.id.wifi_ssid);
        wifiBssid = findViewById(R.id.wifi_bssid);
        wifiIpv4 = findViewById(R.id.wifi_ipv4);
        wifiEnabled = findViewById(R.id.wifi_enabled);
        scanSucceeds = findViewById(R.id.scan_succeeds);
        scanSsid = findViewById(R.id.scan_ssid);
        scanBssid = findViewById(R.id.scan_bssid);
        scanRssi = findViewById(R.id.scan_rssi);
        scanFrequency = findViewById(R.id.scan_frequency);
        scanCapabilities = findViewById(R.id.scan_capabilities);

        fields = new HashMap<>();
        fields.put(ConfigValidator.FIELD_COUNTRY_ISO, countryIso);
        fields.put(ConfigValidator.FIELD_LOCALE_TAG, localeTag);
        fields.put(ConfigValidator.FIELD_WIFI_SSID, wifiSsid);
        fields.put(ConfigValidator.FIELD_WIFI_BSSID, wifiBssid);
        fields.put(ConfigValidator.FIELD_WIFI_IPV4, wifiIpv4);
        fields.put(ConfigValidator.FIELD_SCAN_SSID, scanSsid);
        fields.put(ConfigValidator.FIELD_SCAN_BSSID, scanBssid);
        fields.put(ConfigValidator.FIELD_SCAN_RSSI, scanRssi);
        fields.put(ConfigValidator.FIELD_SCAN_FREQUENCY, scanFrequency);
        fields.put(ConfigValidator.FIELD_SCAN_CAPABILITIES, scanCapabilities);
    }

    private void load(HookConfig config) {
        countryIso.setText(config.countryIso);
        localeTag.setText(config.localeTag);
        wifiSsid.setText(config.wifiSsid);
        wifiBssid.setText(config.wifiBssid);
        wifiIpv4.setText(config.wifiIpv4);
        wifiEnabled.setChecked(config.wifiEnabled);
        scanSucceeds.setChecked(config.scanSucceeds);
        scanSsid.setText(config.scanSsid);
        scanBssid.setText(config.scanBssid);
        scanRssi.setText(String.valueOf(config.scanRssi));
        scanFrequency.setText(String.valueOf(config.scanFrequency));
        scanCapabilities.setText(config.scanCapabilities);
    }

    private void save() {
        clearErrors();
        ConfigValidator.ValidationResult result = ConfigValidator.validate(new HookConfig.Draft(
                valueOf(countryIso),
                valueOf(localeTag),
                valueOf(wifiSsid),
                valueOf(wifiBssid),
                valueOf(wifiIpv4),
                wifiEnabled.isChecked(),
                scanSucceeds.isChecked(),
                valueOf(scanSsid),
                valueOf(scanBssid),
                valueOf(scanRssi),
                valueOf(scanFrequency),
                valueOf(scanCapabilities)
        ));

        if (!result.isValid()) {
            EditText field = fields.get(result.field);
            if (field != null) {
                field.setError(result.message);
                field.requestFocus();
            }
            return;
        }

        ConfigStore.write(this, result.config);
        load(result.config);
        Toast.makeText(this, R.string.saved_message, Toast.LENGTH_LONG).show();
    }

    private void restoreDefaults() {
        ConfigStore.restoreDefaults(this);
        load(HookConfig.defaults());
        clearErrors();
        Toast.makeText(this, R.string.defaults_restored_message, Toast.LENGTH_LONG).show();
    }

    private void clearErrors() {
        for (EditText field : fields.values()) {
            field.setError(null);
        }
    }

    private static String valueOf(EditText field) {
        return field.getText().toString();
    }
}
