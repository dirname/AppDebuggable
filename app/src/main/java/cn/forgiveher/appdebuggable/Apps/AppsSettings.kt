package cn.forgiveher.appdebuggable.Apps

import android.content.Context
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import cn.forgiveher.appdebuggable.Common.SystemManager
import cn.forgiveher.appdebuggable.R
import org.json.JSONArray
import org.json.JSONException
import java.io.*

class AppsSettings(private val mContext: Context) {
    private val AppsSettingsPath = Environment.getExternalStorageDirectory().path + "/AppDebuggable/AppsSwitch.json"

    private val settings: String?
        get() {
            try {
                val fis = FileInputStream(File(AppsSettingsPath))
                val isr = InputStreamReader(fis, "utf-8")
                val input = CharArray(fis.available())
                isr.read(input)
                isr.close()
                fis.close()
                var readed = String(input)
                try {
                    val jsonArray = JSONArray(readed)
                } catch (e: Exception) {
                    val fos = FileOutputStream(File(AppsSettingsPath))
                    val osw = OutputStreamWriter(fos, "utf-8")
                    osw.write("[]")
                    osw.flush()
                    fos.flush()
                    osw.close()
                    fos.close()
                    SyncSettings()
                    readed = "[]"
                }

                return readed
            } catch (e: Exception) {
                return null
            }

        }

    fun setApp(packageName: String) {
        val userSettings = settings
        try {
            val jsonArray: JSONArray
            if(userSettings!!.isEmpty()) {
                jsonArray = JSONArray()
            } else {
                jsonArray = JSONArray(userSettings)
            }
            if (!getApp(packageName)) {
                jsonArray.put(packageName)
            }
            val fos = FileOutputStream(File(AppsSettingsPath))
            val osw = OutputStreamWriter(fos, "utf-8")
            osw.write(jsonArray.toString())
            osw.flush()
            fos.flush()
            osw.close()
            fos.close()
            SyncSettings()
            Toast.makeText(mContext, packageName + " " + mContext.getString(R.string.is_debuggable), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.i("error", e.message)
        }

    }

    private fun SyncSettings() {
        try {
            if (!SystemManager.RootCommand("cp " + Environment.getExternalStorageDirectory().path + "/AppDebuggable/AppsSwitch.json /data/AppsSwitch.json")) {
                AlertDialog.Builder(mContext)
                        .setTitle(mContext.getString(R.string.sync_error_title))
                        .setMessage(mContext.getString(R.string.sync_error_desc))
                        .setIcon(R.mipmap.ic_launcher)
                        .setCancelable(false).show()
            }
        } catch (e: Exception) {
            AlertDialog.Builder(mContext)
                    .setTitle(mContext.getString(R.string.sync_error_title))
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(e.message + "\n\n" + mContext.getString(R.string.configure_not_valid))
                    .setCancelable(false).show()
        }

    }

    fun delApp(packageName: String) {
        val userSettings = settings
        try {
            val jsonArray = JSONArray(userSettings)
            jsonArray.remove(getPackgeIndex(packageName))
            val fos = FileOutputStream(File(AppsSettingsPath))
            val osw = OutputStreamWriter(fos, "utf-8")
            osw.write(jsonArray.toString())
            osw.flush()
            fos.flush()
            osw.close()
            fos.close()
            SyncSettings()
            Toast.makeText(mContext, packageName + " " + mContext.getString(R.string.not_debuggable), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {

        }

    }

    fun getApp(packageName: String): Boolean {
        val index = getPackgeIndex(packageName)
        return index > -1
    }

    private fun getPackgeIndex(packageName: String): Int {
        val userSettings = settings
        var result = -1
        try {
            val jsonArray = JSONArray(userSettings)
            for (i in 0 until jsonArray.length()) {
                val pName = jsonArray.getString(i)
                if (packageName == pName) {
                    result = i
                    break
                }
            }
        } catch (e: JSONException) {
            result = -1
        }

        return result
    }
}
