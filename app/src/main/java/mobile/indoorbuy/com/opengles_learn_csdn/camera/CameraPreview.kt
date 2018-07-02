package mobile.indoorbuy.com.opengles_learn_csdn.camera

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.widget.FrameLayout
import android.view.SurfaceView



/**
 * Created by BMW on 2018/6/25.
 */
class CameraPreview : FrameLayout,SurfaceHolder.Callback{

    constructor(context: Context) : super(context) {
        addPreview()

        //CameraLollipop，根据系统版本使用Camera或者Camera2
        camera = CameraKitKat(surfaceView)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        addPreview()

        //CameraLollipop，根据系统版本使用Camera或者Camera2
        camera = CameraKitKat(surfaceView)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        addPreview()

        //CameraLollipop，根据系统版本使用Camera或者Camera2
        camera = CameraKitKat(surfaceView)
    }

    private val surfaceView = SurfaceView(context)
    private val camera: ACamera
    private var supportCameraBack = false // 是否支持后置


    //大多数手机：前摄像头预览数据旋转了90度，并且左右镜像了,后摄像头旋转了270度
    override fun surfaceCreated(holder: SurfaceHolder?) {
        if(supportCameraBack){
            camera.open(1)  //支持后置
        }else{
            camera.open(0)  //不支持后置
        }

        Log.e("tag","${camera.cameraInfo}")
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        keepScreenOn = false
        camera.close()
    }

    private fun addPreview() {
        surfaceView.holder.addCallback(this)
        this.addView(surfaceView)
    }

    fun takePicture(){
        camera.takePicture()
    }
}