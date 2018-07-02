package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.nfc.Tag
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.camera.ACamera
import mobile.indoorbuy.com.opengles_learn_csdn.camera.CameraKitKat
import mobile.indoorbuy.com.opengles_learn_csdn.common.*
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by BMW on 2018/6/11.
 * 纹理
 */
class CameraRenderer(private val context: Context,private val glSurfaceView: GLSurfaceView):GLSurfaceView.Renderer {
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private var programObjectId: Int = 0

    private lateinit var surfaceTexture: SurfaceTexture

    private val vertexCoords = floatArrayOf(
            -1.0f, 1.0f,    //左上角
            -1.0f, -1.0f,   //左下角
            1.0f, 1.0f,     //右上角
            1.0f, -1.0f     //右下角
    )
    private val textureCoords = floatArrayOf(   //纹理坐标范围（0.0 , 1.0）
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    )

    private var mTextureMatrix = FloatArray(16)
    private var mMVPMatrix = FloatArray(16)
    private var textureId: Int = 0
    private val camera: ACamera = CameraKitKat(glSurfaceView)

    private var preWidth = 0
    private var preHeight = 0
    private var viewWidth = 0
    private var viewHeight = 0

    override fun onDrawFrame(gl: GL10?) {
        surfaceTexture.updateTexImage()

        surfaceTexture.getTransformMatrix(mTextureMatrix)

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(programObjectId)

        val vCoordMatrix = GLES20.glGetUniformLocation(programObjectId, "vCoordMatrix")
        GLES20.glUniformMatrix4fv(vCoordMatrix, 1, false, mTextureMatrix, 0)

        val vMatrix = GLES20.glGetUniformLocation(programObjectId, "vMatrix")
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mMVPMatrix, 0)

        val vTexture = GLES20.glGetUniformLocation(programObjectId, "vTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0) //激活纹理单元0
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId) //绑定外部纹理到纹理单元0
        //指定一个当前的vTexture对象为一个全局的uniform 变量
        GLES20.glUniform1i(vTexture, 0)

        val vPosition = GLES20.glGetAttribLocation(programObjectId, "vPosition")
        GLES20.glEnableVertexAttribArray(vPosition)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val vCoordinate = GLES20.glGetAttribLocation(programObjectId, "vCoordinate")
        GLES20.glEnableVertexAttribArray(vCoordinate)
        GLES20.glVertexAttribPointer(vCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(vPosition)
        GLES20.glDisableVertexAttribArray(vCoordinate)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        //针对前后摄像头，我们可以计算我们需要的变换矩阵

        camera.openTexture(0, surfaceTexture, width, height)  //不支持后置
        val cameraInfo = camera.cameraInfo
        preWidth = cameraInfo.preSizeWidth
        preHeight = cameraInfo.preSizeHeight

        viewWidth = width
        viewHeight = height
        Log.e("tag", "preWidth =$preWidth,preHeight =$preHeight,viewWidth =$viewWidth,viewHeight =$viewHeight,")
        calculateMatrix()

        //Matrix.setIdentityM(mTextureMatrix,0)

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1f)

        vertexBuffer = VertexArrayHelper.readVertexBuffer(vertexCoords)
        vertexBuffer.position(0)

        textureBuffer = VertexArrayHelper.readVertexBuffer(textureCoords)
        textureBuffer.position(0)

        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.camera_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.camera_fragment_shader)

        val vertexShader = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader, fragmentShader)
        textureId = ShaderHelper.createCameraTextureID()

        //根据纹理id，创建SurfaceTexture
        surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture.setOnFrameAvailableListener {
            glSurfaceView.requestRender()
        }

    }

    private fun calculateMatrix() {
        mMVPMatrix = MatrixHelper.getShowMatrix(preWidth, preHeight, viewWidth, viewHeight)

        if (camera.cameraId == 1) {
            //支持后置
            MatrixHelper.flip(mMVPMatrix, true, false)
            MatrixHelper.rotate(mMVPMatrix, 90f)
        } else if (camera.cameraId == 0) {
            //不支持后置
            MatrixHelper.flip(mMVPMatrix, true, false)
            MatrixHelper.rotate(mMVPMatrix, -90f)
        }
    }

}