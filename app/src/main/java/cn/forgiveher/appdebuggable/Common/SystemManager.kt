package cn.forgiveher.appdebuggable.Common

import android.app.Activity
import android.util.Log

import java.io.DataOutputStream

class SystemManager : Activity() {
    companion object {
        /**
         * @return 应用程序是/否获取Root权限
         */
        fun RootCommand(command: String): Boolean {
            var process: Process? = null
            var os: DataOutputStream? = null
            try {
                process = Runtime.getRuntime().exec("su")
                os = DataOutputStream(process!!.outputStream)
                os.writeBytes("$command \n")
                os.writeBytes("exit\n")
                os.flush()
                val exitValue = process.waitFor()
                return exitValue == 0
            } catch (e: Exception) {
                Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: " + e.message)
                return false
            } finally {
                try {
                    os?.close()
                    process!!.destroy()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}
