package mobile.indoorbuy.com.opengles_learn_csdn.camera

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.support.annotation.UiThread
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import java.util.*
import android.view.Surface.ROTATION_180
import android.view.Surface.ROTATION_0
import java.io.IOException
import android.util.Log
import android.widget.FrameLayout
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mobile.indoorbuy.com.opengles_learn_csdn.common.Content.TAG
import android.R.attr.y
import android.graphics.ImageFormat
import mobile.indoorbuy.com.opengles_learn_csdn.common.ScreenUtils


/**
 * Created by BMW on 2018/6/25.
 * camera
 */
class CameraKitKat(val displayView: SurfaceView) : ACamera() {


    private val holder = displayView.holder
    private var displayScale = 0.0f

    private var camera: Camera? = null

    private val sizeComparator = Comparator<Camera.Size> { lhs, rhs ->
        when {
            lhs.height == rhs.height -> 0
            lhs.height > rhs.height -> 1
            else -> -1
        }
    }

    init {
        config.minPreviewWidth = 720
        config.minPictureWidth = 720
        config.rate = 1.778f
    }

    override fun openTexture(type: Int,surfaceTexture: SurfaceTexture,screenWidth:Int,screenHeight:Int) {
        cameraId = type
        if(!openCamera(type)) return
        setParameters(camera!!,screenWidth,screenHeight)

        camera!!.setPreviewTexture(surfaceTexture)
        camera!!.startPreview()
    }


    private fun getBestSize(supportedPreviewSizes: List<Camera.Size>,screenWidth:Int,screenHeight:Int): Camera.Size? {
        var bestSize:Camera.Size? = null
        var largestArea = screenWidth * screenHeight
        for (size in supportedPreviewSizes) {
            if (size.width <= screenWidth && size.height <= screenHeight) {
                if (bestSize == null) {
                    bestSize = size
                } else {
                    val resultArea = bestSize.width * bestSize.height
                    val newArea = size.width * size.height

                    if (newArea > resultArea) {
                        bestSize = size
                    }
                }
            }
        }
        return bestSize
    }


    override fun open(type: Int) {
        val rotation = (displayView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.rotation

        cameraId = type

        if(!openCamera(type)) return
        setParameters(camera!!,ScreenUtils.getAndroiodScreenWidth(displayView.context),ScreenUtils.getAndroiodScreenHeight(displayView.context))
        resizeDisplayView()
        setDisplayOrientation(camera!!, rotation)
        setPreviewDisplay(camera!!, holder)
        camera!!.setDisplayOrientation(90)
        camera!!.startPreview()

    }

    override fun close() {
        camera!!.stopPreview()
        camera!!.release()
    }

    //调整SurfaceView的大小
    private fun resizeDisplayView() {
        val parameters = camera!!.parameters
        val size = parameters.previewSize
        val p = displayView.layoutParams as FrameLayout.LayoutParams
        val scale = size.width / size.height.toFloat()
        displayScale = displayView.height.toFloat() / displayView.width.toFloat()
        if (scale > displayScale) {
            p.height = (scale * displayView.width).toInt()
            p.width = displayView.width
        } else {
            p.width = (displayView.height / scale).toInt()
            p.height = displayView.height
        }
        Log.e(TAG, "-->" + size.width + "/" + size.height)
        Log.e(TAG, "--<" + p.height + "/" + p.width)
        displayView.layoutParams = p
        displayView.invalidate()
    }

    /**
     * 检查是否支持相机
     * Camera.getNumberOfCameras()可以获得当前设备的Camera的个数 0.不支持相机 1.只有后置
     */
    private fun checkCameraId(cameraId: Int): Boolean =
            cameraId >= 0 && cameraId < Camera.getNumberOfCameras()

    /**
     * 打开相机，获取相机实例
     */
    private fun openCamera(cameraId: Int): Boolean {
        Log.e(TAG,"result == ${checkCameraId(cameraId)},${ Camera.getNumberOfCameras()}")
        if (!checkCameraId(cameraId)) return false
        camera = Camera.open()
        return true
    }

    /**
     * 设置相机实例参数
     */
    private fun setParameters(camera: Camera,screenWidth:Int,screenHeight:Int) {
        val parameters = camera.parameters

        //PreviewSize设置为设备支持的最高分辨率
        val previewSize = changePreviewSize(parameters.supportedPreviewSizes, screenWidth, screenHeight)

        parameters.setPreviewSize(previewSize!!.width, previewSize.height)

        //PictureSize设置为和预览大小最近的
        val pictureSize = changePreviewSize(parameters.supportedPictureSizes, screenWidth, screenHeight)

        parameters.setPictureSize(pictureSize!!.width, pictureSize.height)

        //如果相机支持自动聚焦，则设置相机自动聚焦，否则不设置
        if (parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }

        //设置颜色效果
//        parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);

        //设置拍照后存储的图片格式
    //    parameters.pictureFormat = ImageFormat.JPEG
        val mCameraPreviewThousandFps = chooseFixedPreviewFps(parameters, 15 * 1000)
        Log.e("weiwei","mCameraPreviewThousandFps = ${mCameraPreviewThousandFps/1000f}")
        // Give the camera a hint that we're recording video.
        // This can have a big impact on frame rate.
        parameters.setRecordingHint(true)

        camera.parameters = parameters


        //填充info
        cameraInfo.preSizeWidth = previewSize.width
        cameraInfo.preSizeHeight = previewSize.height
        cameraInfo.picSizeWidth = pictureSize.width
        cameraInfo.picSizeHeight = pictureSize.height
    }

    //相机使用第三步，设置相机预览方向
    private fun setDisplayOrientation(camera: Camera, rotation: Int) =
            if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                camera.setDisplayOrientation(90)
            } else {
                camera.setDisplayOrientation(0)
            }

    //相机使用第四步，设置相机预览载体SurfaceHolder
    private fun setPreviewDisplay(camera: Camera, holder: SurfaceHolder) {
        try {
            camera.setPreviewDisplay(holder)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun measureSize(width:Int, height:Int){
        super.measureSize(width, height)
    }

    override fun takePicture() {
        super.takePicture()

        camera!!.takePicture(
                { Log.e(TAG,"快门按下") },  //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
                { _, _ ->  Log.e(TAG,"源数据")},  // 拍摄的未压缩原数据的回调,可以为null
                { _, _ -> Log.e(TAG,"拍摄完成") }   //拍照完成的回调，这里可以保存图片,这里数据默认为yuv420sp
            )

    }

    fun chooseFixedPreviewFps(parms: Camera.Parameters, desiredThousandFps: Int): Int {
        val supported = parms.supportedPreviewFpsRange

        for (entry in supported) {
            //Log.d(TAG, "entry: " + entry[0] + " - " + entry[1]);
            if (entry[0] == entry[1] && entry[0] == desiredThousandFps) {
                parms.setPreviewFpsRange(entry[0], entry[1])
                return entry[0]
            }
        }

        val tmp = IntArray(2)
        parms.getPreviewFpsRange(tmp)
        val guess: Int
        if (tmp[0] == tmp[1]) {
            guess = tmp[0]
        } else {
            guess = tmp[1] / 2     // shrug
        }

        Log.d(TAG, "Couldn't find match for $desiredThousandFps, using $guess")
        return guess
    }
}