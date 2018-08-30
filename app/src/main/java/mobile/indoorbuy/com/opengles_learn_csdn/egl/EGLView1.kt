package mobile.indoorbuy.com.opengles_learn_csdn.egl

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.camera.ACamera
import mobile.indoorbuy.com.opengles_learn_csdn.camera.CameraKitKat
import mobile.indoorbuy.com.opengles_learn_csdn.codec.CameraRecordEncoder
import mobile.indoorbuy.com.opengles_learn_csdn.common.MatrixHelper
import mobile.indoorbuy.com.opengles_learn_csdn.common.ShaderHelper
import mobile.indoorbuy.com.opengles_learn_csdn.common.TextureHelper
import java.io.File
import java.lang.ref.WeakReference

/**
 * Created by BMW on 2018/7/5.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class EGLView1 :
        FrameLayout,SurfaceHolder.Callback{

    private val surfaceView = SurfaceView(context)
    private lateinit var signaFrame: SignaFrame
    private lateinit var frameRect: FrameRect
    private val mRecordEncoder = CameraRecordEncoder()
    private val outputFile = File(Environment.getExternalStorageDirectory().path, "/weiwei/lz.mp4")
    private var VIDEO_WIDTH = 1280
    private var VIDEO_HEIGHT = 720    // dimensions for 720p video

    private var recording: Boolean = false

    var mRequestRecord: Boolean = false

    fun setRecord(mRequestRecord: Boolean){
        this.mRequestRecord = mRequestRecord
    }

    constructor(context: Context):super(context){
        surfaceView.holder.setFormat(PixelFormat.RGBA_8888)  //申明支持透明，避免水印不透明
        surfaceView.holder.addCallback(this)
        addView(surfaceView)
        frameRect = FrameRect(context)
        signaFrame = SignaFrame(context)
    }

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs){
        surfaceView.holder.setFormat(PixelFormat.RGBA_8888)  //申明支持透明，避免水印不透明
        surfaceView.holder.addCallback(this)
        addView(surfaceView)
        frameRect = FrameRect(context)
        signaFrame = SignaFrame(context)

    }


    override fun surfaceCreated(p0: SurfaceHolder?) {

        eglCore = EGLCore()
        displaySurface = WindowSurface(eglCore,surfaceView.holder.surface,false)
        displaySurface.makeCurrent()

        frameRect.initProgram()
        signaFrame.initProgram()

        mTextureId  = ShaderHelper.createCameraTextureID()
        msignaTextureId = TextureHelper.loadTexture(context, R.mipmap.ic_launcher)

        recording = mRecordEncoder.isRecording
        mCameraTexture = SurfaceTexture(mTextureId)
        mCameraTexture.setOnFrameAvailableListener {
            mHandler.sendEmptyMessage(MainHandler.MSG_FRAME_AVAILABLE)
        }

    }

    val cameraId = 0
    private val bitmap = BitmapFactory.decodeResource(context.resources,R.mipmap.ic_launcher)
    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, width: Int, height: Int) {
        camera.openTexture(cameraId,mCameraTexture,width,height)
        calculateMatrix(width,height)

        mMVPSignaMatrix = MatrixHelper.getSignaMatrix(bitmap.width,bitmap.height,width,height)
        Matrix.rotateM(mMVPSignaMatrix,0,-180f,0f,0f,1f)
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        displaySurface.release()
        eglCore.release()
        camera.close()
    }

    private lateinit var eglCore: EGLCore
    private lateinit var displaySurface: WindowSurface
    private var mTextureId: Int = 0
    private var msignaTextureId: Int = 0
    private lateinit var mCameraTexture:SurfaceTexture
    private val mHandler = MainHandler(this)
    private val camera: ACamera = CameraKitKat(surfaceView)

    private class MainHandler internal constructor(view: EGLView1) : Handler() {
        private val mView: WeakReference<EGLView1> = WeakReference(view)
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
        GLES20.glEnable(GLES20.GL_BLEND)  //打开混合功能
        GLES20.glBlendFunc(GLES20.GL_ONE,GLES20.GL_ONE_MINUS_SRC_ALPHA) //指定混合模式
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        mCameraTexture.updateTexImage() // // 获取预览帧
        mCameraTexture.getTransformMatrix(mTmpMatrix)  // 获取预览帧的变换矩阵

        GLES20.glViewport(0, 0,width , height)
        frameRect.drawFrame(mTextureId,mTmpMatrix,mMVPMatrix)

        GLES20.glViewport(0, 0,100 , 150)
        signaFrame.drawFrame(msignaTextureId,mMVPSignaMatrix)

        displaySurface.swapBuffers() //交换读写的渲染介质


        //水印录制 状态设置
        if(mRequestRecord){
            if(!recording){
                Log.e("weiwei","开始录制")
                mRecordEncoder.startRecording(CameraRecordEncoder.EncoderConfig(
                        outputFile,width,height,1000000,
                        EGL14.eglGetCurrentContext(),context
                ))
                mRecordEncoder.setTextureId(mTextureId,mMVPMatrix,mMVPSignaMatrix)
                recording = mRecordEncoder.isRecording
            }

            mRecordEncoder.frameAvailable(mCameraTexture)
        }else{
            if(recording){
                mRecordEncoder.stopRecording()
                recording = false
            }
        }
    }

    lateinit var mMVPMatrix:FloatArray
    lateinit var mMVPSignaMatrix:FloatArray
    private fun calculateMatrix(viewWidth:Int,viewHeight:Int) {
        VIDEO_WIDTH = camera.cameraInfo.preSizeWidth
        VIDEO_HEIGHT = camera.cameraInfo.preSizeHeight
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