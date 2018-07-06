package mobile.indoorbuy.com.opengles_learn_csdn.egl

import android.app.Activity
import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import mobile.indoorbuy.com.opengles_learn_csdn.camera.ACamera
import mobile.indoorbuy.com.opengles_learn_csdn.camera.CameraKitKat
import mobile.indoorbuy.com.opengles_learn_csdn.common.MatrixHelper
import mobile.indoorbuy.com.opengles_learn_csdn.common.ShaderHelper
import java.lang.ref.WeakReference

/**
 * Created by BMW on 2018/7/5.
 */
class EGLView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        FrameLayout(context, attrs, defStyleAttr),SurfaceHolder.Callback{

    private val surfaceView = SurfaceView(context)
    private lateinit var frameRect: FrameRect

    constructor(context: Context, attrs: AttributeSet? = null) :
            this(context, null, 0){

        surfaceView.holder.addCallback(this)
        addView(surfaceView)
        frameRect = FrameRect(context)
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {

        eglCore = EGLCore()
        displaySurface = WindowSurface(eglCore,surfaceView.holder.surface,false)
        displaySurface.makeCurrent()

        frameRect.initProgram()
        mTextureId  = ShaderHelper.createCameraTextureID()
        mCameraTexture = SurfaceTexture(mTextureId)
        mCameraTexture.setOnFrameAvailableListener {
            mHandler.sendEmptyMessage(MainHandler.MSG_FRAME_AVAILABLE)
        }

    }

    val cameraId = 0
    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, width: Int, height: Int) {
        GLES20.glViewport(0, 0,width , height)
        camera.openTexture(cameraId,mCameraTexture,width,height)
        calculateMatrix(width,height)
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        displaySurface.release()
        eglCore.release()
        camera.close()
    }

    private lateinit var eglCore: EGLCore
    private lateinit var displaySurface: WindowSurface
    private var mTextureId: Int = 0
    private lateinit var mCameraTexture:SurfaceTexture
    private val mHandler = MainHandler(this)
    private val camera: ACamera = CameraKitKat(surfaceView)

    private class MainHandler internal constructor(view: EGLView) : Handler() {
        private val mView: WeakReference<EGLView> = WeakReference(view)
        override fun handleMessage(msg: Message) {
            val eglView = mView.get() ?: return
            when (msg.what) {
                MSG_FRAME_AVAILABLE -> eglView.drawFrame()
                else -> super.handleMessage(msg)
            }
        }

        companion object {
            const val MSG_FRAME_AVAILABLE = 1
        }
    }

    private val mTmpMatrix = FloatArray(16)
    fun drawFrame() {
        displaySurface.makeCurrent()  //锁定渲染介质
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(1f, 0f, 0f, 1f)
        mCameraTexture.updateTexImage() // // 获取预览帧
        mCameraTexture.getTransformMatrix(mTmpMatrix)  // 获取预览帧的变换矩阵

        frameRect.drawFrame(mTextureId,mTmpMatrix,mMVPMatrix)

        displaySurface.swapBuffers() //交换读写的渲染介质
    }

    lateinit var mMVPMatrix:FloatArray
    private fun calculateMatrix(viewWidth:Int,viewHeight:Int) {
        mMVPMatrix = MatrixHelper.getShowMatrix(camera.cameraInfo.preSizeWidth,
                camera.cameraInfo.preSizeHeight,
                viewWidth, viewHeight)

        if (camera.cameraId == 1) {
            //支持后置
            MatrixHelper.flip(mMVPMatrix, true, false)
            MatrixHelper.rotate(mMVPMatrix, 90f)
        } else if (camera.cameraId == 0) {
            //不支持后置
            //MatrixHelper.flip(mMVPMatrix, true, false)
            MatrixHelper.rotate(mMVPMatrix, -90f)
        }
    }
}