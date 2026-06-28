# Cycle Alarm

Cycle Alarm 是一个面向长周期提醒场景的轻量 Android 应用。

它不做后台保活，不做云同步，不自己调度长期通知，而是把重点放在：

- 周期任务的本地管理
- 到期日期计算
- 完成后重新计时
- 调起系统日历和系统闹钟

适合这类任务：

- SIM 卡保活
- 滤芯更换
- 域名续费
- 证件检查
- 账单周期检查

## 当前状态

当前仓库已经可以本地构建、安装并在真机上运行。

已验证：

- `assembleDebug`
- `testDebugUnitTest`
- `installDebug`

## 功能

- 新增、查看、编辑、删除周期任务
- 支持启用 / 停用任务
- 支持两种循环模式
- 支持完成历史记录
- 使用本地 `SharedPreferences + JSON` 持久化
- 支持调起系统日历新增事件页
- 支持调起系统闹钟设置页

### 两种循环模式

1. `AFTER_COMPLETION`

任务完成后，从完成当天重新开始计算下一轮。

2. `FIXED_DATE`

任务完成后，从上一次应到期日期继续顺延，不会因为提前完成而改变固定节奏。

## 技术选型

- Kotlin
- Jetpack Compose
- Single Activity
- `AndroidViewModel`
- `SharedPreferences`
- `kotlinx.serialization`

明确没有使用：

- Room
- WorkManager
- AlarmManager
- BootReceiver
- ForegroundService
- 云同步
- 后台常驻通知

## 项目结构

```text
app/src/main/java/com/example/cyclealarm/
├── MainActivity.kt
├── data/
├── domain/
├── model/
├── system/
├── ui/
└── viewmodel/
```

## 本地运行

1. 安装 Android Studio
2. 安装 Android SDK Platform 35、Build-Tools、Platform-Tools
3. 在项目根目录创建 `local.properties`
4. 用 Android Studio 打开项目
5. 连接安卓手机并开启 USB 调试
6. 运行 `app`

`local.properties` 示例：

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

也可以直接参考：

- [local.properties.example](local.properties.example)
- [MINIMAL_ANDROID_SETUP.md](MINIMAL_ANDROID_SETUP.md)

命令行构建：

```powershell
.\gradlew.bat assembleDebug
```

命令行安装到已连接设备：

```powershell
.\gradlew.bat installDebug
```

## 数据存储

任务数据保存在 App 本地 `SharedPreferences` 中，核心键名是：

`loop_alarms_json`

这意味着：

- 卸载 App 会丢失本地数据
- 当前没有云同步
- 当前没有跨设备同步

## 系统提醒限制

Cycle Alarm 当前通过 Intent 调起系统应用：

- 日历：`Intent.ACTION_INSERT`
- 闹钟：`AlarmClock.ACTION_SET_ALARM`

因此当前有一个明确限制：

App 不能自动修改或删除你已经在系统日历、系统闹钟中创建的提醒。

例如：

- 在 App 内停用任务，不会自动删除系统日历事件
- 在 App 内删除任务，不会自动删除系统闹钟

这不是遗漏，而是当前轻量方案的设计边界。

## 测试建议

优先手测以下流程：

1. 新增任务
2. 编辑任务
3. 点击“我已完成”
4. 关闭后重开，确认数据仍存在
5. 调起系统日历
6. 调起系统闹钟

单元测试目前覆盖了：

- 周期日期计算
- 两种完成模式的下一次到期逻辑
- 剩余天数 / 逾期天数计算

## 后续可扩展方向

- JSON 导入 / 导出
- 更好的模板系统
- 更细的提醒文案生成
- 更丰富的首页筛选和排序
- 可选的系统通知增强方案
