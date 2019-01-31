package cn.forgiveher.appdebuggable.Apps

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

import java.util.HashMap

import cn.forgiveher.appdebuggable.R

/**
 * @author: xp
 * @date: 2017/7/19
 */

class SortAdapter(private val mContext: Context, private var mData: List<SortModel>?) : RecyclerView.Adapter<SortAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private val map = HashMap<Int, Boolean>() //存放Switch的状态,防止错乱

    private var mOnItemClickListener: OnItemClickListener? = null


    init {
        mInflater = LayoutInflater.from(mContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortAdapter.ViewHolder {
        val view = mInflater.inflate(R.layout.item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.tvTag = view.findViewById<View>(R.id.tag) as TextView
        viewHolder.tvName = view.findViewById<View>(R.id.tv_appName) as TextView
        viewHolder.tvPackageName = view.findViewById(R.id.tv_packgeName)
        viewHolder.swApp = view.findViewById(R.id.switch_app)
        viewHolder.AppIcon = view.findViewById(R.id.imageView_app)
        return viewHolder
    }

    override fun onBindViewHolder(holder: SortAdapter.ViewHolder, position: Int) {
        val appsSettings = AppsSettings(mContext)
        val section = getSectionForPosition(position)
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.tvTag!!.visibility = View.VISIBLE
            holder.tvTag!!.text = mData!![position].letters
        } else {
            holder.tvTag!!.visibility = View.GONE
        }

        holder.tvPackageName!!.text = this.mData!![position].appPackageName
        holder.AppIcon!!.setImageDrawable(this.mData!![position].appIcon)
        if (appsSettings.getApp(this.mData!![position].appPackageName!!)) {
            map[position] = true
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener { mOnItemClickListener!!.onItemClick(holder.itemView, position) }

        }

        holder.tvName!!.text = this.mData!![position].name

        holder.tvName!!.setOnClickListener { Toast.makeText(mContext, mData!![position].name, Toast.LENGTH_SHORT).show() }

        //********** Switch 防止错乱 ***************
        holder.swApp!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                map[position] = true
                appsSettings.setApp(mData!![position].appPackageName!!)
            } else {
                map.remove(position)
                appsSettings.delApp(mData!![position].appPackageName!!)
            }
        }
        holder.swApp!!.isChecked = map != null && map.containsKey(position)
        //*****************************************

    }

    override fun getItemCount(): Int {
        return mData!!.size
    }

    //**********************itemClick************************
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener
    }
    //**************************************************************

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvTag: TextView? = null
        internal var tvName: TextView? = null
        internal var tvPackageName: TextView? = null
        internal var swApp: Switch? = null
        internal var AppIcon: ImageView? = null
    }

    /**
     * 提供给Activity刷新数据
     * @param list
     */
    fun updateList(list: List<SortModel>) {
        this.mData = list
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Any {
        return mData!![position]
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    fun getSectionForPosition(position: Int): Int {
        return mData!![position].letters!![0].toInt()
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    fun getPositionForSection(section: Int): Int {
        for (i in 0 until itemCount) {
            val sortStr = mData!![i].letters
            val firstChar = sortStr!!.toUpperCase().get(0)
            if (firstChar.toInt() == section) {
                return i
            }
        }
        return -1
    }

}
