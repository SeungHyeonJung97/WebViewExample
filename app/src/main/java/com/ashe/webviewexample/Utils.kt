package com.ashe.webviewexample

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Utils {
    var isRooted = true

    var rootString = arrayOf(
        "su",
        "busybox",
        "/system /xbin/which"
    )


    var binaryPaths = arrayOf(
        "/data/local/",
        "/data/local/bin/",
        "/data/local/xbin/",
        "/sbin/",
        "/su/bin/",
        "/system/bin/",
        "/system/bin/.ext/",
        "/system/bin/failsafe/",
        "/system/sd/xbin/",
        "/system/usr/we-need-root/",
        "/system/xbin/",
        "/system/app/Superuser.apk",
        "/cache",
        "/data",
        "/dev"
    )

//    fun ankoAlert(
//        Cancelable: Boolean = false,
//        title: String = "알림",
//        message: String = "메세지",
//        positiveButtonText: String = "확인",
//        negativeButton: String = "취소",
//        onPositive: (() -> Unit)? = null,
//        onNegative: (() -> Unit)? = null
//    ) {
//        alert(title = title, message = message) {
//            isCancelable = Cancelable
//            onPositive?.let {
//                positiveButton(positiveButtonText) { it() }
//                onNegative?.let {
//                    negativeButton(negativeButton) { it() }
//                }
//            }
//        }.show()
//    }
    fun getKey(context: Context): String? {
        try {
            val pm = context.packageManager
            val info =
                pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val digest = md.digest()
                val toRet = StringBuilder()
                for (i in digest.indices) {
                    if (i != 0) toRet.append(":")
                    val b: Int = digest[i].toInt() and 0xff
                    val hex = Integer.toHexString(b)
                    if (hex.length == 1) toRet.append("0")
                    toRet.append(hex)
                }
                return toRet.toString()
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
        return ""
    }

    fun setPrefString(context: Context, key: String, value: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(
            Url.PREFRENCE,
            Context.MODE_PRIVATE
        )
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getPrefString(context: Context, key: String): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(
            Url.PREFRENCE,
            Context.MODE_PRIVATE
        )
        return prefs.getString(key, "")
    }
}