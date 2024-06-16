package com.example.ganvas3

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.graphics.drawable.BitmapDrawable
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.ganvas3.ImageStyleTransferHelper
import com.example.ganvas3.R
import java.io.IOException


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var `select-button`: Button? = null
    private var `transform-button`: Button? = null
    private var `capture-button`: Button? = null
    private var imageView: ImageView? = null
    private var result: ImageView? = null
    private var bitmap: Bitmap? = null
    private lateinit var imageStyleTransferHelper: ImageStyleTransferHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // home page layout - looking okay
        setContentView(R.layout.activity_main)

        //image transfer
        imageStyleTransferHelper = ImageStyleTransferHelper(this)

        // transform-button
        // needs AI MODEL implemented
        `transform-button` = findViewById(R.id.transformBtn)
        `transform-button`?.setOnClickListener {
            val image = (imageView?.drawable as? BitmapDrawable)?.bitmap
            if (image != null) {
                val copiedImage = image.copy(image.config, true)
                result?.setImageBitmap(copiedImage)
            }
        }

        // capture-button // working on emulator
        // please make work on physical device
        // todo change function to use camera not just the gallery
        `capture-button` = findViewById(R.id.captureBtn)
        `capture-button`?.run {
            setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 20)
                // save image to gallery or transform right away
            }
        }

        // working
        imageView = findViewById(R.id.imageview)

        // working
        result = findViewById(R.id.result)

        // select-button
        // working as intended - open gallery and select image
        `select-button` = findViewById(R.id.selectBtn)
        `select-button`?.run {
            setOnClickListener {
                val intent = Intent()
                intent.setAction(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                startActivityForResult(intent, 10)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                // select image request code
                // working properly
                10 -> {
                    val uri = data?.data
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        imageView?.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                // capture image request code
                // may need update for physical device
                20 -> {
                    val extras = data?.extras
                    if (extras != null && extras.containsKey("data")) {
                        val image = extras["data"] as Bitmap?
                        imageView?.setImageBitmap(image)
                    }
                }
                // edit this for transform
                // will need update for AI Model
                //30 -> {
                //    val extras = data?.extras
                //    if (extras != null && extras.containsKey("data")) {
                //        val image = extras["data"] as Bitmap?
                //        result?.setImageBitmap(image)
                //    }
                }
            }
        }
    }
//}