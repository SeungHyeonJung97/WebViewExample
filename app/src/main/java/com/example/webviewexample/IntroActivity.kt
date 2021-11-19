package com.example.webviewexample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.dialog_debug_popup.*

class IntroActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        showPopUp()
    }

    private fun showPopUp() {
        var debugPopup = DebugPopup.createDialog(this, "개발 모드", false)
        debugPopup.onRightClick("접속", View.OnClickListener {
            when {
                debugPopup.ck1.isChecked -> {
                    if (debugPopup.et_url1.text.toString().isEmpty()) {
                        Url.ACCESS_URL = debugPopup.et_url1.text.toString()
                    }
                }
                debugPopup.ck2.isChecked -> {
                    if (debugPopup.et_url2.text.toString().isEmpty()) {
                        Url.ACCESS_URL = debugPopup.et_url2.text.toString()
                    }
                }
                debugPopup.ck3.isChecked -> {
                    if (debugPopup.et_url3.text.toString().isEmpty()) {
                        Url.ACCESS_URL = debugPopup.et_url3.text.toString()
                    }
                }
            }

            Utils.setPrefString(this, "url1", debugPopup.et_url1.text.toString())
            Utils.setPrefString(this, "url2", debugPopup.et_url2.text.toString())
            Utils.setPrefString(this, "url3", debugPopup.et_url3.text.toString())

            debugPopup.dismiss()
        })
        debugPopup.show()
    }
}