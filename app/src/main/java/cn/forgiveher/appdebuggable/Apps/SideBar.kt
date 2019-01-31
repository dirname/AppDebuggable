package cn.forgiveher.appdebuggable.Apps

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

import cn.forgiveher.appdebuggable.R

class SideBar : View {
    // 触摸事件
    private var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null
    private var choose = -1
    private val paint = Paint()

    private var mTextDialog: TextView? = null

    /**
     * 为SideBar设置显示字母的TextView
     * @param textDialog
     */
    fun setTextView(textDialog: TextView) {
        this.mTextDialog = textDialog
    }


    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val height = height
        val width = width
        val singleHeight = height / b.size// 获取每一个字母的高度

        for (i in b.indices) {
            paint.color = Color.rgb(33, 65, 98)
            // paint.setColor(Color.WHITE);
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.isAntiAlias = true
            paint.textSize = 30f
            if (i == choose) {// 选中的状态
                paint.color = Color.parseColor("#3399ff")
                paint.isFakeBoldText = true
            }
            // x坐标等于中间-字符串宽度的一半
            val xPos = width / 2 - paint.measureText(b[i]) / 2
            val yPos = (singleHeight * i + singleHeight).toFloat()
            canvas.drawText(b[i], xPos, yPos, paint)
            paint.reset()
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val y = event.y
        val oldChoose = choose
        val listener = onTouchingLetterChangedListener
        val c = (y / height * b.size).toInt()// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数

        when (action) {
            MotionEvent.ACTION_UP -> {
                background = ColorDrawable(0x00000000)
                choose = -1//
                invalidate()
                if (mTextDialog != null) {
                    mTextDialog!!.visibility = View.INVISIBLE
                }
            }

            else -> {
                setBackgroundResource(R.drawable.sidebar_background)
                if (oldChoose != c) {
                    if (c >= 0 && c < b.size) {
                        listener?.onTouchingLetterChanged(b[c])
                        if (mTextDialog != null) {
                            mTextDialog!!.text = b[c]
                            mTextDialog!!.visibility = View.VISIBLE
                        }

                        choose = c
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    /**
     * 触摸事件
     * @param onTouchingLetterChangedListener
     */
    fun setOnTouchingLetterChangedListener(
            onTouchingLetterChangedListener: OnTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener
    }

    /**
     * @author coder
     */
    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterChanged(s: String)
    }

    companion object {
        var b = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
    }

}