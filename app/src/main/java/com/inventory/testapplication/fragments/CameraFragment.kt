package com.inventory.testapplication.fragments

import android.Manifest
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
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
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.inventory.testapplication.New.CameraControllerListener
import com.inventory.testapplication.New.saveImageNewWay
import com.inventory.testapplication.New.saveImageOldWay
import com.inventory.testapplication.R
import com.inventory.testapplication.databinding.FragmentCameraBinding


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

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1001)
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

            val texture = binding.textureView.surfaceTexture


            var width = binding.textureView.width
            var height = binding.textureView.height

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

            texture!!.setDefaultBufferSize(newWidth, newHeight)

            binding.textureView.layoutParams.apply {
                width = newWidth
                height = newHeight
            }

            requireActivity().runOnUiThread {
                binding.textureView.requestLayout()

            }


            val previewSurface = Surface(texture)

            setupImageReader(aspectRatio)
            val imageSurface = imageReader.surface



            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(previewSurface)

            //TODO Add SharedPreference
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)

            cameraDevice.createCaptureSession(
                listOf(previewSurface, imageSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraCaptureSession = session

                            updatePreview()
                            // Update your UI elements here if necessary


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





}




