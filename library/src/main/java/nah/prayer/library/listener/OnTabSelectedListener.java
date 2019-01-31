package nah.prayer.library.listener;

import android.support.design.widget.TabLayout;

/**
 * @author hugeterry(http://hugeterry.cn)
 */

public interface OnTabSelectedListener {

    public void onTabSelected(TabLayout.Tab tab);

    public void onTabUnselected(TabLayout.Tab tab);

    public void onTabReselected(TabLayout.Tab tab);
}
