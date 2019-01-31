package cn.forgiveher.appdebuggable

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import cn.forgiveher.appdebuggable.Common.SystemManager
import cn.forgiveher.appdebuggable.Fragment.FragmentAbout
import cn.forgiveher.appdebuggable.Fragment.FragmentApps
import cn.forgiveher.appdebuggable.Fragment.PagerAdapter
import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout

class MainActivity : AppCompatActivity() {
    private var mCoordinatorTabLayout: CoordinatorTabLayout? = null
    private var mViewPager: ViewPager? = null
    internal var fragmentAbout = FragmentAbout()
    internal var fragmentApps = FragmentApps(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        InitViewPager()
        fragmentApps.userSettings = getSharedPreferences("setting", Context.MODE_PRIVATE)
        fragmentApps.editor = fragmentApps.userSettings!!.edit()
        mCoordinatorTabLayout = findViewById(R.id.CoordinatorTabLayout)
        mCoordinatorTabLayout!!.setTranslucentStatusBar(this)
                .setTitle(this.getString(R.string.app_name))
                .setBackEnable(false)
                .setTabMode(TabLayout.MODE_FIXED)
                .setupWithViewPager(mViewPager)
    }

    private fun InitViewPager() {
        mViewPager = findViewById(R.id.vp)
        //mViewPager.setOffscreenPageLimit(2);
        val adapter = PagerAdapter(supportFragmentManager)

        adapter.addFragment(fragmentAbout, this.getString(R.string.tab_about))
        adapter.addFragment(fragmentApps, this.getString(R.string.tab_app))
        mViewPager!!.adapter = adapter
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val item = menu.findItem(R.id.action_systemApp)
        val isShowSystemApp = fragmentApps.userSettings!!.getBoolean("showSystemApp", false)
        item.isChecked = isShowSystemApp
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_systemApp) {
            if (item.isChecked) {
                item.isChecked = false
                fragmentApps.editor!!.putBoolean("showSystemApp", false)
            } else {
                item.isChecked = true
                fragmentApps.editor!!.putBoolean("showSystemApp", true)
            }
            fragmentApps.editor!!.commit()
            fragmentApps.filledData(item.isChecked)
            mViewPager!!.currentItem = 1
            return true
        } else if (id == R.id.action_reboot) {
            if (!SystemManager.RootCommand("reboot")) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(this.applicationContext.getString(R.string.reboot_failed))
                        .setMessage(this.applicationContext.getString(R.string.reboot_error))
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton(this.applicationContext.getString(R.string.OK)) { dialog, which -> }.setCancelable(false).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
