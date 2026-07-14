# android-region-network-xposed

用于授权测试环境的 LSPosed/Xposed 模块，可向应用模拟 Android 设备的地区和 Wi-Fi 环境信息，包括 SIM/网络国家码、系统语言区域、Wi-Fi SSID/BSSID/IPv4 地址，以及 Wi-Fi 扫描和启用状态。

## 适用场景

- 验证应用的地区相关逻辑，例如语言、定价或合规页面。
- 在受控测试环境中验证应用读取 Wi-Fi 身份、IP 地址和扫描结果时的处理逻辑。

## Hook 行为

| 目标方法 | 返回值 |
|---|---|
| `TelephonyManager.getNetworkCountryIso()` | `"in"` |
| `TelephonyManager.getSimCountryIso()` | `"in"` |
| `Locale.getDefault()` | `Locale.forLanguageTag("en-IN")` |
| `Locale.getDefault(Locale.Category)` | `Locale.forLanguageTag("en-IN")` |
| `WifiInfo.getSSID()` | `"Office_5G_Test"` |
| `WifiInfo.getBSSID()` | `02:1A:2B:3C:4D:5E` |
| `WifiInfo.getIpAddress()` | `203.0.113.25` |
| `WifiManager.isWifiEnabled()` | `true` |
| `WifiManager.startScan()` | `true` |
| `WifiManager.getScanResults()` | 一个固定的 `Office_5G_Test` 扫描结果 |

模块通过 `xposed_init` 注册。其 Hook 会影响被选中作用域内的进程；Wi-Fi 相关返回值仅用于模拟应用可见状态，不会开启设备 Wi-Fi 无线电，也不会实际连接接入点。

## 前置条件

- 已 root 的 Android 设备（Magisk、KernelSU 或 APatch）。
- 已安装 **LSPosed**（Zygisk 版本）或 **Xposed 框架**。
- 使用 Magisk 时需开启 Zygisk；可执行 `enable-zygisk.sh` 快速启用。

## 构建

```bash
./gradlew :app:assembleDebug
```

构建产物位于 `app/build/outputs/apk/debug/app-debug.apk`。

## 安装与激活

1. 将 `app-debug.apk` 安装到设备。
2. 打开 LSPosed Manager，进入“模块”，找到 **Telephony Country Hook** 并启用。
3. 勾选需要测试的目标应用；需要验证系统进程时，再勾选“系统框架”作用域。
4. 重启设备或软重启，使模块生效。

## 验证

```bash
# 查看 Hook 日志
adb logcat -s TelephonyCountryHook:I Xposed:I

# 检查 SIM 国家码系统属性
adb shell getprop gsm.sim.operator.iso-country
```

## 注意事项

- 这些 Hook 会改变目标进程读取到的值，并可能影响依赖 `TelephonyManager` 或 Wi-Fi 信息的业务逻辑。
- 仅用于已获授权的测试环境，不要在生产设备或未经授权的应用上使用。
- 卸载或禁用模块后，重启设备以确保相关进程恢复未 Hook 状态。

## 相关文件

| 文件 | 说明 |
|---|---|
| `app/src/main/java/com/flashy/test/telephonycountry/TelephonyCountryHook.java` | Hook 核心逻辑与固定测试值 |
| `app/src/main/assets/xposed_init` | Xposed 模块入口声明 |
| `enable-zygisk.sh` | 启用 Magisk Zygisk 的辅助脚本 |
