package com.inventory.testapplication.fragments

import android.Manifest
import android.animation.AnimatorListenerAdapter
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.SurfaceTexture
import android.graphics.drawable.ColorDrawable
import android.hardware.SensorManager.getOrientation
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.media.AudioAttributes
import android.media.Image
import android.media.ImageReader
import android.media.MediaScannerConnection
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.inventory.testapplication.New.CameraControllerListener
import com.inventory.testapplication.New.saveImageNewWay
import com.inventory.testapplication.New.saveImageOldWay
import com.inventory.testapplication.R
import com.inventory.testapplication.databinding.FragmentCameraBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer


class CameraFragment() : Fragment(), CameraControllerListener {


    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var handlerThread: HandlerThread
    private lateinit var backgroundHandler: Handler
    private lateinit var cameraId: String

    private lateinit var soundPool: SoundPool
    private var shutterSoundId: Int = 0

    private lateinit var imageReader: ImageReader

    private val imageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        val image: Image? = reader.acquireLatestImage()
        // Process the image
        image?.let {

            saveImage(it)
            it.close()
        }
    }


    private var isFrontCamera = false

    private val textureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        innitSoundPool()
        cameraManager = requireContext().getSystemService(CameraManager::class.java)

        binding.textureView.surfaceTextureListener = textureListener

        innit()

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1001)
        }
    }

    private fun innit() {
        loadImageFromAssets(requireContext(), binding.horizontalScrollViewMainCrossIcon, "horizontal_scroll_view_main_cross_icon.png")
        loadImageFromAssets(requireContext(), binding.autoEffectIcon, "auto_effect_icon.png")
        loadImageFromAssets(requireContext(), binding.manualEffectIcon, "manual_effect_icon.png")
        loadImageFromAssets(requireContext(), binding.incandescentEffectIcon, "incandescent_effect_icon.png")
        loadImageFromAssets(requireContext(), binding.flourescentEffectIcon, "flourescent_effect_icon.png")
        loadImageFromAssets(requireContext(), binding.warmEffectIcon, "warm_effect_icon.png")
        loadImageFromAssets(requireContext(), binding.dayLightEffectIcon, "day_light_effect_icon.png")
        loadImageFromAssets(requireContext(), binding.cloudyEffectIcon, "cloudy_effect_icon.png")
        loadImageFromAssets(requireContext(), binding.dayLightEffectIcon2, "day_light_effect_icon_2.png")
        loadImageFromAssets(requireContext(), binding.shadeEffectIcon, "shade_effect_icon.png")





        binding.autoEffectIcon.setOnClickListener {

            binding.autoEffectIcon.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
           binding. manualEffectIcon.colorFilter = null
            binding.incandescentEffectIcon.colorFilter = null
            binding.flourescentEffectIcon.colorFilter = null

            binding.warmEffectIcon.colorFilter = null
            binding.dayLightEffectIcon.colorFilter = null
            binding.cloudyEffectIcon.colorFilter = null
            binding.dayLightEffectIcon2.colorFilter = null
            binding.shadeEffectIcon.colorFilter = null

            // Remove any color effect from the preview view
            binding.textureView.foreground = null
            // Save state
            saveEffectState("autoEffectIcon", Color.RED, null)

        }

        binding.manualEffectIcon.setOnClickListener {

            binding.manualEffectIcon.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
            binding.autoEffectIcon.colorFilter = null
            binding.incandescentEffectIcon.colorFilter = null
            binding.flourescentEffectIcon.colorFilter = null
            binding.warmEffectIcon.colorFilter = null
           binding. dayLightEffectIcon.colorFilter = null
           binding. cloudyEffectIcon.colorFilter = null
           binding. dayLightEffectIcon2.colorFilter = null
           binding. shadeEffectIcon.colorFilter = null

            // Apply grayscale effect to the preview view
            val overlayColor = ColorDrawable(Color.parseColor("#A0000020"))
            binding.textureView.foreground = overlayColor

            // Save state
            saveEffectState("manualEffectIcon", Color.RED, Color.parseColor("#A0000020"))

        }

        binding.incandescentEffectIcon.setOnClickListener {

           binding. incandescentEffectIcon.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
           binding. autoEffectIcon.colorFilter = null
           binding. manualEffectIcon.colorFilter = null
           binding. flourescentEffectIcon.colorFilter = null
           binding. warmEffectIcon.colorFilter = null
           binding. dayLightEffectIcon.colorFilter = null
          binding.  cloudyEffectIcon.colorFilter = null
           binding. dayLightEffectIcon2.colorFilter = null
           binding. shadeEffectIcon.colorFilter = null

            val overlayColor = ColorDrawable(Color.parseColor("#90608080"))
          binding.  textureView.foreground = overlayColor

            saveEffectState("incandescentEffectIcon", Color.RED, Color.parseColor("#90608080"))
        }

        binding.flourescentEffectIcon.setOnClickListener {
            binding.flourescentEffectIcon.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
            binding.incandescentEffectIcon.colorFilter = null
            binding.autoEffectIcon.colorFilter = null
            binding.manualEffectIcon.colorFilter = null
            binding.warmEffectIcon.colorFilter = null
            binding.dayLightEffectIcon.colorFilter = null
            binding.cloudyEffectIcon.colorFilter = null
            binding.dayLightEffectIcon2.colorFilter = null
            binding.shadeEffectIcon.colorFilter = null

            val overlayColor = ColorDrawable(Color.parseColor("#40000000"))
            binding.textureView.foreground = overlayColor



        }

        binding.warmEffectIcon.setOnClickListener {

            binding.warmEffectIcon.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
           binding. autoEffectIcon.colorFilter = null
           binding. manualEffectIcon.colorFilter = null
           binding. flourescentEffectIcon.colorFilter = null
            binding.dayLightEffectIcon.colorFilter = null
           binding. cloudyEffectIcon.colorFilter = null
            binding.dayLightEffectIcon2.colorFilter = null
           binding. shadeEffectIcon.colorFilter = null
           binding. incandescentEffectIcon.colorFilter = null

            val overlayColor = ColorDrawable(Color.parseColor("#90608080"))
           binding. textureView.foreground = overlayColor

            saveEffectState("warmEffectIcon", Color.RED, Color.parseColor("#90608080"))

        }

        binding.dayLightEffectIcon.setOnClickListener {

           binding. dayLightEffectIcon.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
           binding. incandescentEffectIcon.colorFilter = null
           binding. autoEffectIcon.colorFilter = null
           binding. manualEffectIcon.colorFilter = null
           binding. flourescentEffectIcon.colorFilter = null
           binding. cloudyEffectIcon.colorFilter = null
           binding. dayLightEffectIcon2.colorFilter = null
           binding. shadeEffectIcon.colorFilter = null
           binding. warmEffectIcon.colorFilter = null

           binding. textureView.foreground = null

            saveEffectState("dayLightEffectIcon", Color.RED, null)

        }

        binding.cloudyEffectIcon.setOnClickListener {

           binding. cloudyEffectIcon.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
            binding.incandescentEffectIcon.colorFilter = null
            binding.autoEffectIcon.colorFilter = null
            binding.manualEffectIcon.colorFilter = null
            binding.flourescentEffectIcon.colorFilter = null
            binding.dayLightEffectIcon.colorFilter = null
            binding.dayLightEffectIcon2.colorFilter = null
            binding.shadeEffectIcon.colorFilter = null
            binding.warmEffectIcon.colorFilter = null

            val overlayColor = ColorDrawable(Color.parseColor("#40000000"))
            binding.textureView.foreground = overlayColor

            saveEffectState("cloudyEffectIcon", Color.RED, Color.parseColor("#40000000"))

        }

        binding.dayLightEffectIcon2.setOnClickListener {

          binding.  dayLightEffectIcon2.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
          binding.  incandescentEffectIcon.colorFilter = null
          binding.  autoEffectIcon.colorFilter = null
          binding.  manualEffectIcon.colorFilter = null
          binding.  flourescentEffectIcon.colorFilter = null
          binding.  dayLightEffectIcon.colorFilter = null
          binding.  cloudyEffectIcon.colorFilter = null
          binding.  shadeEffectIcon.colorFilter = null
          binding.  warmEffectIcon.colorFilter = null

            val overlayColor = ColorDrawable(Color.parseColor("#40000030"))
          binding.  textureView.foreground = overlayColor

            saveEffectState("twiiLightEffectIcon", Color.RED, Color.parseColor("#40000030"))

        }

        binding.shadeEffectIcon.setOnClickListener {

         binding.   shadeEffectIcon.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
        binding.    incandescentEffectIcon.colorFilter = null
        binding.    dayLightEffectIcon2.colorFilter = null
        binding.    autoEffectIcon.colorFilter = null
        binding.    manualEffectIcon.colorFilter = null
        binding.    flourescentEffectIcon.colorFilter = null
        binding.    dayLightEffectIcon.colorFilter = null
        binding.    cloudyEffectIcon.colorFilter = null
        binding.    warmEffectIcon.colorFilter = null

            val overlayColor = ColorDrawable(Color.parseColor("#40000000"))
         binding.   textureView.foreground = overlayColor

            saveEffectState("shadeEffectIcon", Color.RED, Color.parseColor("#40000000"))

        }
    }

    fun loadImageFromAssets(context: Context, imageView: ImageView, fileName: String) {
        try {
            // Access the asset manager
            val assetManager = context.assets

            // Open the image file from assets
            val inputStream = assetManager.open(fileName)

            // Convert the input stream to a Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Set the bitmap to the ImageView
            imageView.setImageBitmap(bitmap)

            // Close the input stream after use
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("AssetsImageLoader", "Error loading image from assets: ${e.message}")
        }
    }

    private fun openCamera() {
        try {
            cameraId = cameraManager.cameraIdList[0]
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            cameraManager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            startPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }
    }

    private fun startPreview(aspectRatio: String = "1:1") {
        try {
            // Get the SurfaceTexture
            val texture = binding.textureView.surfaceTexture
            if (texture == null) {
                Log.e("CameraFragment", "SurfaceTexture is null")
                return
            }

            // Get TextureView width and height
            var width = binding.textureView.width
            var height = binding.textureView.height
            Log.d("CameraFragment", "TextureView size: width=$width, height=$height")

            // Calculate new width and height based on the selected aspect ratio
            val (newWidth, newHeight) = when (aspectRatio) {
                "1:1" -> {
                    val minSize = minOf(width, height)
                    Pair(minSize, minSize)
                }
                "4:3" -> {
                    val newHeight = (width * 3) / 4
                    Pair(width, newHeight)
                }
                "16:9" -> {
                    val newHeight = (width * 9) / 16
                    Pair(width, newHeight)
                }
                else -> Pair(width, height) // Full screen (default)
            }

            // Set new buffer size for the SurfaceTexture
            texture.setDefaultBufferSize(newWidth, newHeight)

            // Update layout parameters for TextureView
            binding.textureView.layoutParams.apply {
                this.width = newWidth
                this.height = newHeight
            }

            // Ensure the layout updates happen on the UI thread
            requireActivity().runOnUiThread {
                binding.textureView.requestLayout()
            }

            // Create a surface for the preview
            val previewSurface = Surface(texture)

            // Initialize the ImageReader
            setupImageReader(aspectRatio)
            val imageSurface = imageReader?.surface ?: run {
                Log.e("CameraFragment", "ImageReader surface is null")
                return
            }

            // Create the capture request for the camera preview
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(previewSurface)

            // Set autofocus mode
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)

            // Create the capture session
            cameraDevice.createCaptureSession(
                listOf(previewSurface, imageSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraCaptureSession = session
                        updatePreview()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("CameraFragment", "Failed to configure capture session")
                    }
                }, backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    private fun updatePreview() {
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        try {
            cameraCaptureSession.setRepeatingRequest(
                captureRequestBuilder.build(),
                null,
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (binding.textureView.isAvailable) {
            openCamera()
        } else {
            binding.textureView.surfaceTextureListener = textureListener
        }
    }

    override fun onPause() {
        stopBackgroundThread()
        super.onPause()
    }

    private fun startBackgroundThread() {
        handlerThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(handlerThread.looper)
    }

    private fun stopBackgroundThread() {
        handlerThread.quitSafely()
        try {
            handlerThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        }
    }

    override fun onFlipCamera() {
        Log.d("CameraFlipRequesr", "onFlipCamera: ")
        // flipCamera()


        //TODO SharedPreference method come for timer
        binding.threeToOneAnimationView.visibility = View.VISIBLE
        binding.threeToOneAnimationView.playAnimation()
        binding. threeToOneAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                super.onAnimationEnd(animation)
                captureImage()
                binding.threeToOneAnimationView.visibility = View.INVISIBLE
            }
        })

    }

    private fun flipCamera() {
        isFrontCamera = !isFrontCamera
        cameraId = if (isFrontCamera) {
            getFrontCameraId(cameraManager)
        } else {
            getBackCameraId(cameraManager)
        }


        cameraCaptureSession.close()
        cameraDevice.close()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        cameraManager.openCamera(cameraId, stateCallback, null)

    }


    private fun getFrontCameraId(cameraManager: CameraManager): String {
        for (id in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                return id
            }
        }
        return "0"
    }

    private fun getBackCameraId(cameraManager: CameraManager): String {
        for (id in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                return id
            }
        }
        return "0"
    }

    private fun isFlashSupported(): Boolean {
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        return characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
    }

    private fun captureImage() {
        try {

            val captureBuilder =
                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            val imageSurface = imageReader.surface

            captureBuilder.addTarget(imageSurface)
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)


            val rotation = requireActivity().windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation))

            setFlashModeForCapture(captureBuilder)


            //TODO SharedPreference Auto Focus
            captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START)

            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)


            cameraCaptureSession.capture(
                captureBuilder.build(),
                object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult
                    ) {
                        super.onCaptureCompleted(session, request, result)
                        Log.d("CameraCapture", "Image captured")

                       saveImageWithTag(binding.textureView,binding.CardViewWithMap)



                        playShutterSound()  //TODO SharedPreference
                        startPreview()
                    }
                },
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.d("CameraFlipRequesr", "captureImage:  ${e.printStackTrace()}")
            e.printStackTrace()
        }
    }

    private fun getOrientation(rotation: Int): Int {
        return when (rotation) {
            Surface.ROTATION_0 -> 90
            Surface.ROTATION_90 -> 0
            Surface.ROTATION_180 -> 270
            Surface.ROTATION_270 -> 180
            else -> 0
        }
    }


    private fun saveImage(image: Image) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageNewWay(image, requireContext())
        } else {
            Log.d("ImageSaveTag", "saveImage: ")
            saveImageOldWay(image)
        }
    }

    private fun setupImageReader(aspectRatio: String) {
        val (width, height) = when (aspectRatio) {
            "1:1" -> {

                Pair(1080, 1080)
            }
            "4:3" -> {

                Pair(1440, 1080)
            }
            "16:9" -> {

                Pair(1920, 1080) // Full HD
            }
            else -> {

                Pair(1920, 1080)
            }
        }
        imageReader =
            ImageReader.newInstance(width, height, ImageFormat.JPEG, 1) // Maximum 2 images
        imageReader.setOnImageAvailableListener(imageAvailableListener, backgroundHandler)
    }


    private fun setFlashModeForCapture(captureBuilder: CaptureRequest.Builder) {
        if (isFlashSupported()) {
            when (2) {  // here preference value come   //TODO SharedPreference
                1 -> {
                    captureBuilder.set(
                        CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON
                    )
                    captureBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF)
                }
                2 -> {
                    captureBuilder.set(
                        CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH
                    )
                }
                3 -> {
                    captureBuilder.set(
                        CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                    )
                }
                4 -> {
                    captureBuilder.set(
                        CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON
                    )
                    captureBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH)
                }
            }
        }
    }




    private fun playShutterSound() {
        if (shutterSoundId != 0) {
            soundPool.play(shutterSoundId, 1f, 1f, 1, 0, 1f) // Play the sound at full volume
        }
    }


    private fun innitSoundPool()
    {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        shutterSoundId = soundPool.load(requireContext(), R.raw.camera_shutter_6305, 1)
    }


    private fun saveEffectState(iconId: String, color: Int?, overlayColor: Int?) {
        val sharedPreferences = requireContext().getSharedPreferences("EffectState", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("$iconId-color", color ?: -1)
        editor.putInt("$iconId-overlayColor", overlayColor ?: -1)
        editor.apply()
    }

    private fun restoreEffectState(iconId: String) {
        val sharedPreferences = requireContext().getSharedPreferences("EffectState", Context.MODE_PRIVATE)
        val color = sharedPreferences.getInt("$iconId-color", -1)
        val overlayColor = sharedPreferences.getInt("$iconId-overlayColor", -1)

        when (iconId) {
            "autoEffectIcon" -> {
              binding.autoEffectIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                if (overlayColor != -1) {
                    val overlay = ColorDrawable(overlayColor)
                    binding.textureView.foreground = overlay
                }
            }

            "manualEffectIcon" -> {
              binding. manualEffectIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                if (overlayColor != -1) {
                    val overlay = ColorDrawable(overlayColor)
                    binding.textureView.foreground = overlay
                }

            }

            "incandescentEffectIcon" -> {
                binding.  incandescentEffectIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                if (overlayColor != -1) {
                    val overlay = ColorDrawable(overlayColor)
                    binding.textureView.foreground = overlay
                }
            }

            "florescentEffectIcon" -> {
                binding.    flourescentEffectIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                if (overlayColor != -1) {
                    val overlay = ColorDrawable(overlayColor)
                    binding.textureView.foreground = overlay
                }

            }

            "warmEffectIcon" -> {
                binding.  warmEffectIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                if (overlayColor != -1) {
                    val overlay = ColorDrawable(overlayColor)
                    binding.textureView.foreground = overlay
                }
            }

            "dayLightEffectIcon" -> {
                binding.   dayLightEffectIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                if (overlayColor != -1) {
                    val overlay = ColorDrawable(overlayColor)
                    binding.textureView.foreground = overlay
                }
            }

            "cloudyEffectIcon" -> {
                binding.  cloudyEffectIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                if (overlayColor != -1) {
                    val overlay = ColorDrawable(overlayColor)
                    binding.textureView.foreground = overlay
                }
            }

            "twiiLightEffectIcon" -> {
                binding.   dayLightEffectIcon2.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                if (overlayColor != -1) {
                    val overlay = ColorDrawable(overlayColor)
                    binding.textureView.foreground = overlay
                }
            }

            "shadeEffectIcon" -> {
                binding.  shadeEffectIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                if (overlayColor != -1) {
                    val overlay = ColorDrawable(overlayColor)
                    binding.textureView.foreground = overlay
                }
            }

        }
    }

    /*fun saveImageWithTag(textureView: TextureView, tagView: View) {
        // Capture the bitmap from the TextureView
        val capturedBitmap = captureBitmapFromTextureView(textureView)
        capturedBitmap?.let { bitmap ->
            // Create a mutable bitmap to draw the tag
            val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(resultBitmap)

            // Capture the tag view as bitmap
            val tagBitmap = captureBitmapFromView(tagView)

            // Calculate position to draw the tag on the original image
            val tagPositionX = tagView.left.toFloat()
            val tagPositionY = tagView.top.toFloat()

            saveBitmapToFile(tagBitmap)
            // Draw the tag on the canvas (i.e., over the original image)
            canvas.drawBitmap(tagBitmap, tagPositionX, tagPositionY, null)

            // Save the result (bitmap with tag)
            saveBitmapToFile(resultBitmap)
        }
    }*/


    fun saveImageWithTag(textureView: TextureView, tagView: View) {
        // Capture the bitmap from the TextureView
        val capturedBitmap = captureBitmapFromTextureView(textureView)
        capturedBitmap?.let { bitmap ->
            // Create a mutable bitmap to draw the tag
            val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(resultBitmap)

            // Capture the tag view as bitmap
            val tagBitmap = captureBitmapFromView(tagView)

            if (tagBitmap != null && tagBitmap.width > 0 && tagBitmap.height > 0) {
                // Calculate position to draw the tag on the original image
                val tagPositionX = tagView.x  // Use x for the absolute position
                val tagPositionY = tagView.y  // Use y for the absolute position

                // Draw the tag on the canvas (i.e., over the original image)
                canvas.drawBitmap(tagBitmap, tagPositionX, tagPositionY, null)

                // Save the result (bitmap with tag)
                saveBitmapToFile(resultBitmap)
            } else {
                Log.e("ImageTag", "Tag bitmap is invalid or empty.")
            }
        }
    }


    private fun captureBitmapFromView(view: View): Bitmap? {
        // Measure the view to ensure it has the correct size
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val width = view.measuredWidth
        val height = view.measuredHeight

        if (width <= 0 || height <= 0) {
            Log.e("BitmapCapture", "View width or height is invalid: $width x $height")
            return null
        }

        // Create a bitmap with the specified width and height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw the view onto the canvas
        view.draw(canvas)

        return bitmap
    }


    /*Work for Android below 10 */
   /* // Capture the view (tag overlay) as a bitmap
    fun captureBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }*/

    // Save the bitmap to file
    /*fun saveBitmapToFile(bitmap: Bitmap) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "tagged_image_${System.currentTimeMillis()}.jpg")
        try {
            val outputStream = FileOutputStream(file)
            outputStream.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
            }
            Log.d("ImageSave", "Image saved to: ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("ImageSave", "Failed to save image: ${e.message}")
        }
    }*/


    /*Android 10 and ABOVE Implementation*/

    private fun saveBitmapToFile(bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "tagged_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // For Android 10+
        }

        // Insert the image into the MediaStore
        val uri = context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                // Open an output stream to the MediaStore URI
                context?.contentResolver?.openOutputStream(it).use { outputStream ->
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    }
                }
                Log.d("ImageSave", "Image saved to: $it")

                // Notify the gallery about the new image
                MediaScannerConnection.scanFile(context, arrayOf(it.toString()), null, null)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("ImageSave", "Failed to save image: ${e.message}")
            }
        } ?: run {
            Log.e("ImageSave", "Failed to insert image into MediaStore.")
        }
    }


    fun captureBitmapFromTextureView(textureView: TextureView): Bitmap? {
        return if (textureView.isAvailable) {
            textureView.bitmap // Capture the bitmap from the TextureView
        } else {
            null
        }
    }










}




