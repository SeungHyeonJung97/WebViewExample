package com.ashe.webviewexample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ashe.webviewexample.PermisionCheck.rootCheck
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_debug_popup.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class IntroActivity : AppCompatActivity() {

    private var startTime: Long = 0
    private lateinit var hash: String
    private lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        startTime = System.currentTimeMillis()

        if (rootCheck() || Utils.isRooted) {
            alert("루팅된 단말기에서는 이용하실 수 없습니다.","루팅 기기"){
                yesButton { finishAffinity() }
            }.show()
            return
        }

        if(BuildConfig.isRelease){
            ForgeryCheck()
        }else{
            showPopUp()
        }
    }

    private fun ForgeryCheck(){
        key = Utils.getKey(this).toString().uppercase()
        val mDatabase = FirebaseDatabase.getInstance().getReference("Hash")
        mDatabase.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hash = dataSnapshot.getValue().toString()
                Log.d("Hash",hash)
                Log.d("getKey", Utils.getKey(this@IntroActivity).toString())

                if(hash != key){
                    alert("비정상적으로 다운받은 앱입니다.","위변조 된 앱"){
                        yesButton { finishAffinity() }
                    }.show()
                }else{
                    showPopUp()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })


    }

    private fun showPopUp() {
        when (BuildConfig.isRelease) {
            false -> {
                var debugPopup = DebugPopup.createDialog(this, "개발 모드", false)
                debugPopup.onRightClick("접속", View.OnClickListener {
                    when {
                        debugPopup.ck1.isChecked -> {
                            if (debugPopup.et_url1.text.toString().isNotEmpty()) {
                                Url.ACCESS_URL = debugPopup.et_url1.text.toString()
                            }
                        }
                        debugPopup.ck2.isChecked -> {
                            if (debugPopup.et_url2.text.toString().isNotEmpty()) {
                                Url.ACCESS_URL = debugPopup.et_url2.text.toString()
                            }
                        }
                        debugPopup.ck3.isChecked -> {
                            if (debugPopup.et_url3.text.toString().isNotEmpty()) {
                                Url.ACCESS_URL = debugPopup.et_url3.text.toString()
                            }
                        }
                    }

                    Utils.setPrefString(this, "url1", debugPopup.et_url1.text.toString())
                    Utils.setPrefString(this, "url2", debugPopup.et_url2.text.toString())
                    Utils.setPrefString(this, "url3", debugPopup.et_url3.text.toString())

                    debugPopup.dismiss()
                    delayHandler.sendEmptyMessage(0)
                })
                debugPopup.show()
            }

            true ->
                delayHandler.sendEmptyMessage(0)

        }

    }

    var delayHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (startTime < System.currentTimeMillis() - 2000) {
                val intent = Intent(this@IntroActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                sendEmptyMessageDelayed(0, 100)
            }
        }
    }
}