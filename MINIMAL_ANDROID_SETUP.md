# Cycle Alarm 最小运行步骤

这份文档只讲一件事：

让这个项目在你的电脑上完成最小配置，并安装到安卓手机上测试。

适用目录：

`C:\Users\86158\Desktop\AndroidApp`

---

## 1. 先安装 Android Studio

如果电脑上还没有 Android Studio，先安装它。

原因很简单：

- 它会帮你安装 Android SDK
- 它会帮你管理构建工具
- 它最适合第一次把项目跑起来

安装完成后，先打开一次 Android Studio。

---

## 2. 安装项目需要的 Android SDK 组件

打开 Android Studio 后：

1. 进入 `More Actions` -> `SDK Manager`
2. 在 `SDK Platforms` 里安装：
   - `Android 15.0 (API 35)` 或 `Android SDK Platform 35`
3. 在 `SDK Tools` 里安装：
   - `Android SDK Build-Tools`
   - `Android SDK Platform-Tools`
   - `Android SDK Command-line Tools`

如果你已经打开了项目，也可以从：

`File` -> `Settings` -> `Android SDK`

进入同一个页面。

---

## 3. 确认 SDK 安装路径

常见默认路径是：

```text
C:\Users\86158\AppData\Local\Android\Sdk
```

你也可以在 Android Studio 的 `SDK Manager` 页面顶部直接看到真实路径。

记住这个路径，下一步要用。

---

## 4. 在项目根目录创建 `local.properties`

项目根目录就是：

`C:\Users\86158\Desktop\AndroidApp`

在这个目录下新建文件：

`local.properties`

内容写成这样：

```properties
sdk.dir=C\:\\Users\\86158\\AppData\\Local\\Android\\Sdk
```

如果你的 SDK 不在这个位置，就把后面的路径改成你自己的真实路径。

注意：

- 必须是 `sdk.dir=...`
- Windows 路径里的反斜杠要写成双反斜杠 `\\`

项目里已经有一个示例文件：

`local.properties.example`

可以直接照着写。

---

## 5. 用 Android Studio 打开项目

打开 Android Studio，选择：

`Open`

然后打开这个目录：

`C:\Users\86158\Desktop\AndroidApp`

第一次打开时，Android Studio 会做几件事：

- 识别 Gradle 项目
- 读取 `gradlew.bat`
- 读取 `local.properties`
- 下载或整理缺失依赖

如果它提示 `Trust Project` 或类似选项，选择允许即可。

---

## 6. 等待 Gradle 同步完成

打开项目后，先不要急着运行。

先看 Android Studio 底部或右下角的状态：

- 如果正在 `Gradle Sync`，等它结束
- 如果报错，先看是不是 `SDK location not found`

如果仍然报 `SDK location not found`，通常只有两个原因：

1. `local.properties` 没创建成功
2. `sdk.dir` 路径写错了

---

## 7. 打开手机开发者选项和 USB 调试

在安卓手机上操作：

1. 打开 `设置`
2. 找到 `关于手机`
3. 连续点击 `版本号`，直到开启开发者选项
4. 返回设置，进入 `开发者选项`
5. 打开 `USB 调试`

如果是小米 / HyperOS：

- 第一次连接电脑时，手机通常会弹出 USB 调试授权提示
- 选择 `允许`

---

## 8. 用数据线连接手机

把手机通过数据线连接电脑后：

1. 尽量使用可传数据的数据线，不要只充电线
2. 手机弹出提示时，允许 USB 调试
3. 如果通知栏里有 USB 模式，优先选择文件传输或默认模式，不要只充电

连接成功后，Android Studio 顶部的设备列表里应该能看到你的手机。

---

## 9. 直接在 Android Studio 里运行

这是最简单的方法。

在 Android Studio 顶部：

1. 选择运行目标设备，也就是你的手机
2. 点击绿色三角形 `Run`

Android Studio 会自动执行：

- 构建 APK
- 安装到手机
- 启动 App

如果成功，你会在手机上看到 `Cycle Alarm` 应用打开。

---

## 10. 如果你想用命令行构建

在项目根目录打开 PowerShell：

```powershell
cd C:\Users\86158\Desktop\AndroidApp
.\gradlew.bat assembleDebug
```

如果构建成功，APK 一般会出现在：

```text
app\build\outputs\apk\debug\app-debug.apk
```

然后你可以手动安装，或者继续用 Android Studio 安装到手机。

---

## 11. 第一次建议测试什么

安装成功后，先测这 4 个核心点：

1. 新增一个任务，看看首页是否出现
2. 进入详情页，看看日期和模式显示是否正确
3. 点击“我已完成”，看看 `nextDueDate` 是否刷新
4. 关闭 App 再打开，看看数据是否还在

再补测两个系统联动：

1. 点击“添加到系统日历”，看是否能拉起系统日历
2. 点击“设置系统闹钟”，看是否能拉起系统闹钟

---

## 12. 如果运行失败，优先检查这几项

### 情况 1：提示 `SDK location not found`

检查：

- `local.properties` 是否存在
- `sdk.dir` 路径是否正确

### 情况 2：手机不显示在设备列表

检查：

- USB 调试是否开启
- 手机是否点了“允许 USB 调试”
- 数据线是否支持传数据

### 情况 3：Gradle 同步失败

检查：

- Android Studio 是否已安装 SDK Platform 35
- 网络是否能让 Android Studio 下载依赖

### 情况 4：能编译但不能安装

检查：

- 手机是否允许通过 USB 安装
- 手机上是否有旧版本冲突

---

## 13. 最短结论

你现在最少只要做这几件事：

1. 安装 Android Studio
2. 安装 `SDK Platform 35`、`Build-Tools`、`Platform-Tools`
3. 创建 `local.properties`
4. 连接手机并开启 `USB 调试`
5. 用 Android Studio 点 `Run`

只要这 5 步完成，这个项目就有条件真正跑到手机上。
