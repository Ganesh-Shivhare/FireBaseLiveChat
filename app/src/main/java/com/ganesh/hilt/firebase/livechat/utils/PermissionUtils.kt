package com.ganesh.hilt.firebase.livechat.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.ui.dialog.CommonDialog

class PermissionUtils(var context: BaseActivity) {
    private var permissionList = ArrayList<String>()
    private var permissionToBeAsk = ArrayList<String>()
    private var showDialog = false
    private var checkPermissions: CheckPermissions? = null
    private var requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>
    private var preferencesClass = PreferenceClass(context)

    init {

        requestMultiplePermissionsLauncher = context.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissionToBeAsk = ArrayList()
            var showPermissionDialog = false
            for (permission in permissions.keys) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionToBeAsk.add(permission)
                    if (context.shouldShowRequestPermissionRationale(permission)) {
                        preferencesClass.setPrefValue(permission, true)
                    } else {
                        if (preferencesClass.getPrefValue(permission, false) as Boolean) {
                            showPermissionDialog = true
                            Log.d("TAG_value", "onCreate:22 $permission")
                        }
                    }
                }
            }
            checkPermissions!!.allowedPermissions(permissionToBeAsk.size == 0)
            if (showPermissionDialog) {
                showPermissionDialog()
            }
        }
    }

    private fun showPermissionDialog() {
        if (!showDialog) {
            return
        }
        val commonDialog = CommonDialog(
            context,
        ).apply {
            title = "Notification Permission"
            positiveBtnText = "Allow Now"
            message = "Required notification permission to show notifications"
        }

        commonDialog.result = {
            if (it) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.setData(uri)
                context.startActivity(intent)
            }
        }

        commonDialog.show()
    }

    fun askForPermission(
        permissionList: ArrayList<String>, showDialog: Boolean, checkPermissions: CheckPermissions?
    ) {
        this.permissionList = permissionList
        this.showDialog = showDialog
        this.checkPermissions = checkPermissions
        checkPermissions()
    }

    fun justCheckPermission(permissionList: ArrayList<String>, checkPermissions: CheckPermissions) {
        permissionToBeAsk = ArrayList()
        for (i in permissionList.indices) {
            if (context.checkSelfPermission(permissionList[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionToBeAsk.add(permissionList[i])
            }
        }
        checkPermissions.allowedPermissions(permissionToBeAsk.size == 0)
    }

    private fun checkPermissions() {
        permissionToBeAsk = ArrayList()
        for (i in permissionList.indices) {
            if (context.checkSelfPermission(permissionList[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionToBeAsk.add(permissionList[i])
            }
        }
        if (permissionToBeAsk.size == 0) {
            checkPermissions!!.allowedPermissions(true)
        } else {
            requestMultiplePermissionsLauncher.launch(permissionList.toTypedArray<String>())
        }
    }
}

interface CheckPermissions {
    fun allowedPermissions(allowed: Boolean)
}
