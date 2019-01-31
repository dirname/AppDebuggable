package cn.forgiveher.appdebuggable.Xposed

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo

import org.json.JSONArray

import java.io.InputStream
import java.io.InputStreamReader

import cn.forgiveher.appdebuggable.BuildConfig
import cn.forgiveher.appdebuggable.R
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.SELinuxHelper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

import de.robv.android.xposed.XposedBridge.hookAllMethods
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass

class Main : IXposedHookLoadPackage {
    override fun handleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        if (loadPackageParam.packageName == "android") {
            hookAllMethods(findClass("com.android.server.pm.PackageManagerService", loadPackageParam.classLoader),
                    "getPackageInfo", object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val debugParam = 32768
                    val result = param!!.result
                    if (result != null) {
                        val info = (result as PackageInfo).applicationInfo
                        val fis = SELinuxHelper.getAppDataFileService().getFileInputStream("/data/AppsSwitch.json")
                        val isr = InputStreamReader(fis, "utf-8")
                        val input = CharArray(fis.available())
                        isr.read(input)
                        isr.close()
                        fis.close()
                        val s = String(input)
                        try {
                            val jsonArray = JSONArray(s)
                            var packageName: String
                            for (i in 0 until jsonArray.length()) {
                                packageName = jsonArray.getString(i)
                                if (packageName == info.packageName) {
                                    var flags = info.flags
                                    if (flags and debugParam == 0) {
                                        flags = flags or debugParam
                                    }

                                    if (flags and 2 == 0) {
                                        flags = flags or 2
                                    }

                                    info.flags = flags
                                    param.result = result
                                }
                            }
                        } catch (e: Exception) {
                            XposedBridge.log("App Debuggable Throw an Exception:" + e.message)
                        }

                    }
                }
            })
        }

        if (loadPackageParam.packageName == "cn.forgiveher.appdebuggable") {
            findAndHookMethod("cn.forgiveher.appdebuggable.Fragment.FragmentAbout", loadPackageParam.classLoader, "setStatus", Boolean::class.javaPrimitiveType, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                    param!!.args[0] = true
                    super.beforeHookedMethod(param)
                }
            })
        }
    }
}

