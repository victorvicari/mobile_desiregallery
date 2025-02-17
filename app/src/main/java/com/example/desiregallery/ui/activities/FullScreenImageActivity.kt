package com.example.desiregallery.ui.activities

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.desiregallery.R
import com.example.desiregallery.Utils
import kotlinx.android.synthetic.main.activity_full_screen_image.*
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.content.Intent
import android.os.Environment
import java.io.FileOutputStream
import java.io.File
import java.io.IOException
import android.net.Uri
import com.example.desiregallery.MainApplication
import com.example.desiregallery.logging.DGLogger


class FullScreenImageActivity : AppCompatActivity() {
    companion object {
        private const val WRITE_REQUEST_CODE = 101
        private const val SHARING_REQUEST_CODE = 201
        private val TAG = FullScreenImageActivity::class.java.simpleName

        const val EXTRA_IMAGE = "bytesImage"
        const val EXTRA_POST_ID = "postId"
    }

    private lateinit var toolbar: Toolbar

    private lateinit var image: Bitmap
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_full_screen_image)

        toolbar = findViewById(R.id.image_screen_toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        image = Utils.bytesToBitmap(intent.getByteArrayExtra(EXTRA_IMAGE))
        postId = intent.getStringExtra(EXTRA_POST_ID)
        image_view_full_screen.setImageBitmap(image)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_screen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.image_download -> {
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions, WRITE_REQUEST_CODE)
                true
            }
            R.id.image_share -> {
                shareImage()
                true
            }
            else -> false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            WRITE_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.downloadBitmap(image, this)
            } else {
                DGLogger.logWarning(TAG, "There is no permission to write to external storage")
                Toast.makeText(this, R.string.no_access_to_storage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SHARING_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK)
                MainApplication.analyticsTracker.trackSharePhoto(postId)
        }
    }

    private fun shareImage() {
        val bmpUri = getLocalBitmapUri(image)
        bmpUri?.let {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, it)
            shareIntent.type = "image/*"
            startActivityForResult(Intent.createChooser(shareIntent, getString(R.string.share_image)), SHARING_REQUEST_CODE)
        }
    }

    /**
     * Method returns the URI path to given bitmap
     *
     * @param bmp Bitmap to save temporarily in storage and get URI
     * @return URI path of given bitmap
    * */
    private fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }
}
