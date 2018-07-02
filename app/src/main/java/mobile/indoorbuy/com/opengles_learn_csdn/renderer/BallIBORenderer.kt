package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.common.ShaderHelper
import mobile.indoorbuy.com.opengles_learn_csdn.common.TextResourceReader
import mobile.indoorbuy.com.opengles_learn_csdn.common.VertexArrayHelper
import mobile.indoorbuy.com.opengles_learn_csdn.shape.Ball
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by BMW on 2018/6/12.
 * 立方体
 */
class BallIBORenderer(private val context:Context):GLSurfaceView.Renderer{

    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private var mMVPMatrix = FloatArray(16)

    private var ball:Ball? = null

    private var angle = 0.1f
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        Matrix.rotateM(mMVPMatrix,0,angle,0f,0f,1f)
        ball!!.setMatrix(mMVPMatrix)
        ball!!.onDraw()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1f,1f,3f,20f)
        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f,0.0f,5f,      //眼睛位置可以直接设置为（5,5,15），从斜面去看正方体，这样就不用旋转了
                0f,0f,0f,
                0f,1f,0f)
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0)

        //我们眼睛在（0,0,15），是从z轴直线看过出，所以只能看到一个面，旋转一下，更有立体感
//        Matrix.rotateM(mMVPMatrix,0,-45f,0f,1f,0f) //绕y轴逆时针旋转45度
        Matrix.rotateM(mMVPMatrix,0,-45f,1f,0f,0f)

        ball!!.setMatrix(mMVPMatrix)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        ball = Ball(context)
        ball!!.create()
    }

}