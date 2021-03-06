package com.manoelh.task.views.activity

//CAMERA

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.manoelh.task.R
import com.manoelh.task.repository.UserRepository
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.util.concurrent.Executors


// This is an arbitrary number we are using to keep track of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts.
private  const val GALLERY_REQUEST_CODE = 1
private const val REQUEST_CODE_PERMISSIONS = 10
private const val TYPE_OF_FILE = "image/*"
private val TYPE_OF_IMAGES = arrayOf("image/jpeg", "image/png")

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private val executor = Executors.newSingleThreadExecutor()
    private var mLensFacing = CameraX.LensFacing.FRONT
    private lateinit var mUserRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
        mUserRepository = UserRepository(this)
        setListeners()
    }

    private fun setListeners() {
        imageButtonSwitchCamera.setOnClickListener(this)
        imageButtonGalery.setOnClickListener(this)
        buttonChangeUserData.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.imageButtonSwitchCamera -> switchCamera()
            R.id.imageButtonGalery -> pickFromGallery()
            R.id.buttonChangeUserData -> startActivity(Intent(this, ChangeUserDataActivity::class.java))
        }
    }

    private fun switchCamera(){
        CameraX.unbindAll()
        mLensFacing = if (mLensFacing == CameraX.LensFacing.FRONT)
            CameraX.LensFacing.BACK
        else
            CameraX.LensFacing.FRONT

        startCamera()
    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(mLensFacing)
            setTargetResolution(Size(640, 480))
        }.build()

        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the Texture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        takePhoto(preview)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    private fun takePhoto(preview: Preview) {
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                setLensFacing(mLensFacing)
            }.build()

        val imageCapture = ImageCapture(imageCaptureConfig)
        imageButtonTakePhoto.setOnClickListener {
            val file = File(
                externalMediaDirs.first(),
                "${System.currentTimeMillis()}.jpg"
            )

            imageCapture.takePicture(file, executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        Log.e("CameraXApp", msg, exc)
                        viewFinder.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        Log.d("CameraXApp", msg)
                        viewFinder.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            mUserRepository.uploadPhoto(file, null)
                            finish()
                        }
                    }
                })
        }
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(this, this.getString(R.string.no_user_permission), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = TYPE_OF_FILE
        val mimeTypes = TYPE_OF_IMAGES
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                //data.getData return the content URI for the selected Image
                val selectedImage = data?.data
                mUserRepository.uploadPhoto(null, selectedImage)
                finish()
            }
        }

    }
}
