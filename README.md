# android-region-network-xposed

用于已获授权测试环境的 LSPosed/Xposed 模块。它可向被选中作用域内的应用模拟 Android 地区与 Wi-Fi 环境信息，包括 SIM/网络国家码、系统区域、Wi-Fi SSID/BSSID/IPv4 地址和 Wi-Fi 扫描结果。

## 设置页

安装后从应用列表打开 **Telephony Country Hook**，即可修改 Hook 返回值。设置页支持：

- 国家码和默认 Locale（BCP-47 标签）
- 当前 Wi-Fi 的 SSID、BSSID、IPv4 地址和启用状态
- `WifiManager.startScan()` 的返回值
- 一个模拟扫描热点的 SSID、BSSID、RSSI、频率和能力字符串

保存配置后，必须强制停止并重新打开已在 LSPosed 中选定的目标应用。Hook 会在目标进程启动时读取配置，因此不会热更新已经运行的进程。

设置页将值保存到模块私有的 `SharedPreferences`。模块以只读 `ContentProvider` 向被 Hook 进程提供配置；其他应用可读取测试值，但不能通过该 Provider 修改它们。

## 默认 Hook 行为

| 目标方法 | 默认返回值 |
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
| `WifiManager.getScanResults()` | 一个 `Office_5G_Test` 模拟热点 |

Wi-Fi Hook 仅模拟应用读到的状态：它不会开启设备 Wi-Fi 无线电，也不会实际连接接入点。

## 前置条件

- 已 root 的 Android 测试设备（Magisk、KernelSU 或 APatch）。
- 已安装 **LSPosed**（Zygisk 版本）或兼容的 **Xposed 框架**。
- 使用 Magisk 时需开启 Zygisk；可执行 `enable-zygisk.sh` 辅助启用。

## 构建

项目包含 Gradle Wrapper。Windows 下执行：

```powershell
.\gradlew.bat :app:assembleDebug
```

其他系统执行：

```bash
./gradlew :app:assembleDebug
```

APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。

## 安装与激活

1. 安装 `app-debug.apk`，并在应用列表中打开 **Telephony Country Hook** 配置测试值。
2. 在 LSPosed Manager 中启用该模块。
3. 勾选需要测试的目标应用；仅在需要测试系统进程时才选择“系统框架”。
4. 每次修改配置后，强制停止并重新打开目标应用。

## 验证

```bash
# 查看模块日志
adb logcat -s TelephonyCountryHook:I Xposed:I

# 检查设备上报的 SIM 国家码属性（不等同于 Hook 返回值）
adb shell getprop gsm.sim.operator.iso-country
```

## 注意事项

- Hook 会改变目标进程读取到的值，可能影响依赖电话或 Wi-Fi 信息的业务逻辑。
- 模块的 LSPosed 作用域由 LSPosed Manager 管理，设置页不管理目标应用列表。
- 配置读取失败时，模块会记录错误并回退到本 README 中的默认值，以免阻塞目标进程启动。
- 仅用于已获授权的测试环境；禁用或卸载模块后请重启相关设备或进程。

## 相关文件

| 文件 | 说明 |
|---|---|
| `SettingsActivity.java` | 配置界面和输入校验提示 |
| `ConfigProvider.java` | 供被 Hook 进程读取的只读配置接口 |
| `TelephonyCountryHook.java` | 在目标进程启动时读取配置并安装 Hook |
