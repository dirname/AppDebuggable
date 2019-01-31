package cn.forgiveher.appdebuggable.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import cn.forgiveher.appdebuggable.MainActivity
import cn.forgiveher.appdebuggable.R

class FragmentAbout : Fragment() {
    internal var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_about, container, false)

        val source_code = view!!.findViewById<TextView>(R.id.textView_github)
        source_code.setOnClickListener {
            val uri = Uri.parse("https://github.com/dirname/AppDebuggable")
            val it = Intent(Intent.ACTION_VIEW, uri)
            startActivity(it)
        }
        val github = view!!.findViewById<TextView>(R.id.textView_author)
        github.setOnClickListener {
            val uri = Uri.parse("https://github.com/dirname")
            val it = Intent(Intent.ACTION_VIEW, uri)
            startActivity(it)
        }
        setStatus(false)
        return view
    }

    private fun setStatus(status: Boolean) {
        val textView = view!!.findViewById<TextView>(R.id.textView_status)
        if (status) {
            textView.text = view!!.context.getText(R.string.module_is_active)
        } else {
            textView.text = view!!.context.getText(R.string.module_not_active)
        }
    }
}
