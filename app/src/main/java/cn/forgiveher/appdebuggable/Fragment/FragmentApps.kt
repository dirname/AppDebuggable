package cn.forgiveher.appdebuggable.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.ContentProvider
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.Collections

import cn.forgiveher.appdebuggable.Apps.PinyinComparator
import cn.forgiveher.appdebuggable.Apps.PinyinUtils
import cn.forgiveher.appdebuggable.Apps.SideBar
import cn.forgiveher.appdebuggable.Apps.SortAdapter
import cn.forgiveher.appdebuggable.Apps.SortModel
import cn.forgiveher.appdebuggable.Common.SystemManager
import cn.forgiveher.appdebuggable.MainActivity
import cn.forgiveher.appdebuggable.R

@SuppressLint("ValidFragment")
class FragmentApps @SuppressLint("ValidFragment")
constructor(private val activity: Activity) : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var sideBar: SideBar? = null
    private var dialog: TextView? = null
    private var adapter: SortAdapter? = null
    internal lateinit var manager: LinearLayoutManager
    private var pinyinComparator: PinyinComparator? = null
    internal var view: View? = null
    var userSettings: SharedPreferences? = null //设置数据
    var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_apps, container, false)
        // Inflate the layout for this fragment
        pinyinComparator = PinyinComparator()

        sideBar = view!!.findViewById(R.id.sideBar)
        dialog = view!!.findViewById(R.id.dialog)
        sideBar!!.setTextView(dialog!!)

        //设置右侧SideBar触摸监听
        sideBar!!.setOnTouchingLetterChangedListener(object : SideBar.OnTouchingLetterChangedListener {

            override fun onTouchingLetterChanged(s: String) {
                //该字母首次出现的位置
                val position = adapter!!.getPositionForSection(s[0].toInt())
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0)
                }

            }
        })

        mRecyclerView = view!!.findViewById(R.id.recyclerView)
        startRequestPermision()
        return view
    }

    fun filledData(ShowSystemApp: Boolean?) {
        val mSortList = ArrayList<SortModel>()
        val WaitLoadAppDialog = ProgressDialog.show(view!!.context, view!!.context.getString(R.string.app_name), view!!.context.getString(R.string.load_app))
        val thread = Thread(Runnable {
            val packageManager = view!!.context.packageManager
            // 得到包含应用信息的列表
            val apps = packageManager.getInstalledApplications(0)
            // 遍历
            for (info in apps) {
                val icon = info.loadIcon(packageManager)//获取应用图标
                val appName = packageManager.getApplicationLabel(info).toString()//获取应用名称
                val packageName = info.packageName//获取应用包名
                if (info.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                    if (ShowSystemApp!!) {
                        val sortModel = SortModel()
                        sortModel.name = appName
                        sortModel.appPackageName = packageName
                        sortModel.appIcon = icon
                        //汉字转换成拼音
                        val pinyin = PinyinUtils.getPingYin(appName)
                        val sortString = pinyin.substring(0, 1).toUpperCase()
                        // 正则表达式，判断首字母是否是英文字母
                        if (sortString.matches("[A-Z]".toRegex())) {
                            sortModel.letters = sortString.toUpperCase()
                        } else {
                            sortModel.letters = "#"
                        }
                        mSortList.add(sortModel)
                    }
                } else {
                    val sortModel = SortModel()
                    sortModel.name = appName
                    sortModel.appPackageName = packageName
                    sortModel.appIcon = icon
                    //汉字转换成拼音
                    val pinyin = PinyinUtils.getPingYin(appName)
                    val sortString = pinyin.substring(0, 1).toUpperCase()
                    // 正则表达式，判断首字母是否是英文字母
                    if (sortString.matches("[A-Z]".toRegex())) {
                        sortModel.letters = sortString.toUpperCase()
                    } else {
                        sortModel.letters = "#"
                    }
                    mSortList.add(sortModel)
                }
            }
            //线程结束回调, 使用activity进行回调
            activity.runOnUiThread {
                WaitLoadAppDialog.dismiss()
                Collections.sort(mSortList, pinyinComparator)
                manager = LinearLayoutManager(view!!.context)
                manager.orientation = LinearLayoutManager.VERTICAL
                mRecyclerView!!.layoutManager = manager
                adapter = SortAdapter(view!!.context, mSortList)
                mRecyclerView!!.adapter = adapter
                val showAlert = userSettings!!.getBoolean("showSwitchAlert", true)
                if (showAlert) {
                    AlertDialog.Builder(activity)
                            .setTitle(activity.getString(R.string.tips))
                            .setMessage(activity.getString(R.string.switch_tips))
                            .setIcon(R.mipmap.ic_launcher)
                            .setCancelable(false)
                            .setPositiveButton(activity.getString(R.string.OK)) { dialog, which -> }
                            .setNegativeButton(activity.getString(R.string.do_not_show_again)) { dialog, which ->
                                editor!!.putBoolean("showSwitchAlert", false)
                                editor!!.commit()
                            }
                            .show()
                }
            }
        })
        thread.start()
    }

    private fun startRequestPermision() {
        this@FragmentApps.requestPermissions(PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getRoot()
                } else {
                    //如果拒绝授予权限,且勾选了再也不提醒
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {

                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle(activity.getString(R.string.permission_storage_title))
                                .setMessage(activity.getString(R.string.permission_storage_dec))
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton(activity.getString(R.string.go_to_allow)) { dialog, which ->
                                    // 跳转到应用设置界面
                                    goToAppSetting()
                                }
                                .setNegativeButton(activity.getString(R.string.exit)) { dialog, which ->
                                    dialog.cancel()
                                    System.exit(0)
                                }.setCancelable(false).show()
                    } else {
                        showTipGoSetting()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun doRoot() {
        val WaitRootDialog = ProgressDialog.show(view!!.context, view!!.context.getString(R.string.apply_root), view!!.context.getString(R.string.wait_root))
        val thread = Thread(object : Runnable {
            internal var result = false
            override fun run() {
                result = SystemManager.RootCommand("touch /data/AppsSwitch.json")
                activity.runOnUiThread {
                    WaitRootDialog.dismiss()
                    if (!result) {
                        RootDeny()
                    } else {
                        filledData(userSettings!!.getBoolean("showSystemApp", userSettings!!.getBoolean("showSystemApp", false)))
                    }
                }
            }
        })
        thread.start()
    }

    private fun getRoot() {
        val showAlert = userSettings!!.getBoolean("showRootAlert", true)
        if (showAlert) {
            AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.apply_root))
                    .setMessage(activity.getString(R.string.get_root_des))
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(false)
                    .setPositiveButton(activity.getString(R.string.OK)) { dialog, which -> doRoot() }
                    .setNegativeButton(activity.getString(R.string.do_not_show_again)) { dialog, which ->
                        editor!!.putBoolean("showRootAlert", false)
                        editor!!.commit()
                        doRoot()
                    }
                    .show()
        } else {
            doRoot()
        }
        val thread = Thread(Runnable {
            var file = File(Environment.getExternalStorageDirectory().path + "/AppDebuggable/")
            file.mkdir()
            file = File(Environment.getExternalStorageDirectory().path + "/AppDebuggable/AppsSwitch.json")
            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    AlertDialog.Builder(activity)
                            .setTitle(activity.getString(R.string.get_root_error_title))
                            .setMessage(e.message)
                            .setIcon(R.mipmap.ic_launcher)
                            .setCancelable(false)
                            .setPositiveButton(activity.getString(R.string.OK)) { dialog, which -> System.exit(0) }.show()
                }

            }
        })
        thread.start()
    }

    private fun RootDeny() {
        AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.get_root_error_title))
                .setMessage(activity.getString(R.string.get_root_error_desc))
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.OK)) { dialog, which -> System.exit(0) }.show()
    }

    private fun showTipGoSetting() {
        AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.permission_storage_title))
                .setMessage(activity.getString(R.string.permission_storage_dec))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(activity.getString(R.string.go_to_allow)) { dialog, which -> startRequestPermision() }
                .setNegativeButton(activity.getString(R.string.exit)) { dialog, which ->
                    dialog.cancel()
                    System.exit(0)
                }.setCancelable(false).show()

    }

    private fun goToAppSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 123)
        System.exit(0)
    }

    companion object {
        //读写权限
        private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //请求状态码
        private val REQUEST_PERMISSION_CODE = 1
    }
}
