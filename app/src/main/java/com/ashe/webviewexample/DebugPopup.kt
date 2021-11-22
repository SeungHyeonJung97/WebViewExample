package com.ashe.webviewexample

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView

class DebugPopup(context: Context) : Dialog(context) {

    companion object {
        lateinit var customPopup: DebugPopup


        fun createDialog(context: Context, title: String, cancelable: Boolean): DebugPopup {
            customPopup = DebugPopup(context)
            customPopup.setContentView(R.layout.dialog_debug_popup)


            var url1 = customPopup.findViewById<EditText>(R.id.et_url1)
            var url2 = customPopup.findViewById<EditText>(R.id.et_url2)
            var url3 = customPopup.findViewById<EditText>(R.id.et_url3)

            var ck1 = customPopup.findViewById<CheckBox>(R.id.ck1)
            var ck2 = customPopup.findViewById<CheckBox>(R.id.ck2)
            var ck3 = customPopup.findViewById<CheckBox>(R.id.ck3)

            var reset = customPopup.findViewById<TextView>(R.id.btn_reset)

            ck1.setOnClickListener{
                ck2.isChecked = false
                ck3.isChecked = false
            }

            ck2.setOnClickListener{
                ck1.isChecked = false
                ck3.isChecked = false
            }

            ck3.setOnClickListener{
                ck1.isChecked = false
                ck2.isChecked = false
            }

            reset.setOnClickListener{
                url1.setText(Url.WEBURL)
                url2.setText(Url.DEBUG_URL)
                url3.setText(Url.ETC_URL)
            }

            if(Utils.getPrefString(context, "url1") !=""){
                url1.setText(Utils.getPrefString(context, "url1"))
            }else{
                url1.setText(Url.WEBURL)
            }
            if(Utils.getPrefString(context, "url2") !=""){
                url2.setText(Utils.getPrefString(context, "url2"))
            }else{
                url2.setText(Url.DEBUG_URL)
            }
            if(Utils.getPrefString(context, "url3") !=""){
                url3.setText(Utils.getPrefString(context, "url3"))
            }else{
                url3.setText(Url.ETC_URL)
            }



            val titletxt = customPopup.findViewById<TextView>(R.id.tv_dialog_title)
            titletxt.text = title
            val button = customPopup.findViewById<Button>(R.id.btn_login)
            button.setOnClickListener {
                customPopup.dismiss()
            }
            customPopup.setCancelable(cancelable)
            return customPopup

        }
    }

    fun onRightClick(txt: String, clickListener: View.OnClickListener) {
        val button = customPopup.findViewById<Button>(R.id.btn_login)
        button.text = txt
        button.setOnClickListener(clickListener)
    }


    override fun onBackPressed() {
        if(customPopup.isShowing) customPopup.dismiss()
        super.onBackPressed()
    }

}