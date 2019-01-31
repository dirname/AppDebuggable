# App Debuggable

[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](https://github.com/hugeterry/CoordinatorTabLayout/blob/master/LICENSE.txt)
[![Download](https://api.bintray.com/packages/hugeterry/CoordinatorTabLayout/CoordinatorTabLayout/images/download.svg) ](https://www.coolapk.com/apk/217046)

+ 测试 Android 设备 Huawei EVA-AL10
+ Android 设备版本 8.0.0
+ 最小 SDK版本 21
+ Gradle 4.10.1
+ Android Plugin Version 3.3.0
+ Kotlin 1.3
+ Android 设备 Xposed 版本 90-beta3

> 引用项目 [CoordinatorTabLayout](https://github.com/hugeterry/CoordinatorTabLayout)

# 使用步骤

## 运行
<img src="Images/1.jpg" width = 30% height = 30%/>
在 Xposed 中激活 App Debuggable 并运行. 需要申请root权限和储存权限

## 应用开关
<img src="Images/2.jpg" width = 30% height = 30%/>
应用开关中开关需要打开调试的应用

## 重启生效
<img src="Images/3.png" width = 80% height = 80%/>
选择完应用后，重启设备，随后在 Logcat 中选择打开的应用

# 为什么要请求 root ?

Xposed 会在设备启动时加载，此时 `XSharedPreferences` 或 `ContentProvider` 无法工作.因此需要同步一份配置文件至 `/data/` 并在设备启动时读取它

当然如果你有更好的方式读取配置文件，可以联系我的邮箱

App Debuggable 流程图

<img src="Images/4.png" width = 80% height = 80%/>