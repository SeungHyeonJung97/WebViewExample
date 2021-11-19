package com.example.webviewexample

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

object PermissionCheck {
    fun setPermission(
        listener: PermissionListener?,
        permissions: String?,
        context: Context
    ){
        TedPermission.create()
            .setPermissionListener(listener)
            .setDeniedMessage(R.string.permission_request)
            .setPermissions(permissions)
            .setGotoSettingButton(true)
            .setGotoSettingButtonText(R.string.setting_move)
            .check()

    }

    fun IsPermissionGranted(permission: String, context: MainActivity): Boolean {
        val _permission = permission
        val _ExternalPermission = ContextCompat.checkSelfPermission(context, _permission)
        return _ExternalPermission != PackageManager.PERMISSION_DENIED
    }
}