package com.example.webviewexample

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.webviewexample.PermissionCheck.setPermission
import com.gun0912.tedpermission.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mFileDownloadId = -1L
    private var downloadUrl = ""
    private var REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView.webViewClient = InAppWebViewClient()
        webView.webChromeClient = InAppChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.builtInZoomControls = true
        webView.settings.setSupportZoom(true)
        webView.settings.useWideViewPort = true
        webView.settings.userAgentString = webView.settings.userAgentString + "Ashe"
        webView.addJavascriptInterface(WebviewTest(), "WebviewTest")
        webView.loadUrl(Url.ACCESS_URL)
    }

    class InAppChromeClient : WebChromeClient() {

    }

    inner class InAppWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            if (request != null) {
                if (request.url.toString().startsWith("tel:")) {
                    val phoneNum = Uri.parse(request.url.toString())
                    val intent = Intent(Intent.ACTION_DIAL, phoneNum)
                    startActivity(intent)
                    return true

                } else if (request.url.toString().startsWith("sms:")) {
                    val smsUri = Uri.parse(request.url.toString())
                    val intent = Intent(Intent.ACTION_SENDTO, smsUri)
                    startActivity(intent)
                    return true
                } else if (request.url.toString().startsWith("ashe://outLink")) {
                    if (request.url.toString().contains("url=")) {
                        val tempUri = Uri.parse(request.url.toString())
                        val outLinkUri =
                            Uri.parse(tempUri.getQueryParameter("url"))
                        val intent = Intent(Intent.ACTION_VIEW, outLinkUri)
                        startActivity(intent)
                        return true
                    }
                } else if (request.url.toString().startsWith("ashe://openFile")) {
                    if (request.url.toString().contains("url=")) {
                        val tempUri = Uri.parse(request.url.toString())
                        downloadUrl = tempUri.getQueryParameter("url").toString()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            onDownloadStart()
                        } else {
                            onDownloadPermission()
                        }
                        return true
                    }
                } else if (request.url.toString().startsWith("ashe://reqUploadImage")) {
                    val tempUri = Uri.parse(request.url.toString())
                    val type = tempUri.getQueryParameter("type").toString()
                    when (type) {
                        "camera" -> {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                        }
                        "gallery" -> {
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.setType("image/*")
                            startActivity(intent)
                        }
                    }
                    Log.d("type", type)
                    return true
                } else if (request.url.toString().startsWith("ashe://reqAppInfo")) {
                    val appInfo = "App Version_Code : " + BuildConfig.VERSION_CODE
                    webView.loadUrl("javascript:getAppInfo('$appInfo')")
                    return true
                }
            }
            view?.loadUrl(request?.url.toString())
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val tempFile = File(cacheDir, "${timeStamp}.jpg")
            tempFile.createNewFile()
            val out = FileOutputStream(tempFile)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            Toast.makeText(this, "${tempFile.path}", Toast.LENGTH_SHORT).show()
            out.close()
            tempFile.delete()
        }
    }

    private fun onDownloadStart() {
        if (downloadUrl.isEmpty()) return

        try {
            var path: File? = null
            val mimeTypeMap = MimeTypeMap.getSingleton()
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(downloadUrl)
            val fileNameList = downloadUrl.split("/").toTypedArray()
            val fileName: String = try {
                fileNameList[fileNameList.size - 1]
            } catch (e: java.lang.Exception) {
                return
            }

            val fileExtension =
                fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
            val mimeType = mimeTypeMap.getMimeTypeFromExtension(fileExtension)
            val request = DownloadManager.Request(downloadUri)
            request.setTitle(fileName)
            request.setDescription(downloadUrl)
            request.setMimeType(mimeType)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .absoluteFile
                path.mkdirs()
                if (isDownloadFileExists(path.absolutePath + "/" + fileName)) {
                    deleteDownloadFile(path.absolutePath + "/" + fileName)
                }
            }

            val completeFilter =
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            val downloadReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    unregisterReceiver(this)
                    var uri: Uri? = null
                    if (!isDestroyed) {
                        val id: Long = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (id == mFileDownloadId) {
                            val manager =
                                context?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                            uri = manager.getUriForDownloadedFile(mFileDownloadId)
                        }

                        Toast.makeText(
                            this@MainActivity,
                            "File Download Succeed !",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            openFile(null, fileName, uri!!)
                        } else {
                            openFile(path!!.absolutePath, fileName, uri!!)
                        }
                    }
                }
            }
            registerReceiver(downloadReceiver, completeFilter)
            mFileDownloadId = downloadManager.enqueue(request)
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "File Download Failed !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFile(path: String?, fileName: String, uri: Uri?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)

            val file = File("$path/$fileName")
            val map = MimeTypeMap.getSingleton()
            val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)
            var type = map.getMimeTypeFromExtension(ext)
            if (type == null) type = "*/*"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                intent.setDataAndType(uri, type)
            } else {
                val data = FileProvider.getUriForFile(
                    this,
                    this.applicationContext.packageName + ".com.example.webviewexample.provider",
                    file
                )
                intent.setDataAndType(data, type)
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)

        } catch (e: Exception) {
            return
        }


    }

    private fun onDownloadPermission() {
        if (PermissionCheck.IsPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {
            onDownloadStart()
        } else {
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            setPermission(introPermissionListener, permission, applicationContext)
        }
    }

    private val introPermissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            onDownloadStart()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {}
    }

    private fun isDownloadFileExists(pathName: String): Boolean {
        val folder = File(pathName)
        return folder.exists()
    }

    private fun deleteDownloadFile(pathName: String): Boolean {
        val folder = File(pathName)
        return folder.delete()
    }

    inner class WebviewTest {
        @JavascriptInterface
        fun callMethod(arg: String) {
            Toast.makeText(this@MainActivity, arg, Toast.LENGTH_SHORT).show()
        }
    }

    var backpressedTime = 0L

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else if (webView.url.toString().contains("app/webviewTest1.html")) {
            if (System.currentTimeMillis() > backpressedTime + 2000) {
                backpressedTime = System.currentTimeMillis()
                Toast.makeText(this, "\'뒤로\' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            }else if(System.currentTimeMillis() <= backpressedTime + 2000){
                finish()
            }
        }
    }
}

