package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.common.MatrixUtils
import mobile.indoorbuy.com.opengles_learn_csdn.common.ShaderHelper
import mobile.indoorbuy.com.opengles_learn_csdn.common.TextResourceReader
import mobile.indoorbuy.com.opengles_learn_csdn.common.VertexArrayHelper
import mobile.indoorbuy.com.opengles_learn_csdn.shape.Cube
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by BMW on 2018/6/12.
 * 立方体
 */
class CubesRenderer(private val context:Context):GLSurfaceView.Renderer{


    private val matrix = MatrixUtils()
    private val cube1 = Cube(context)
    private val cube2 = Cube(context)

    var i = 0f

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        //保存设置好的透视矩阵和相机点状态
        matrix.saveMatrix()
        matrix.translate(-1f,0f,0f)
        cube1.setMatrix(matrix.getFinalMatrix())
        cube1.onDraw()
        matrix.backMatrix()

        matrix.saveMatrix()
        matrix.translate(2f,1f,0f)
        matrix.ratate(i++,0f,1f,0f)
        cube2.setMatrix(matrix.getFinalMatrix())
        cube2.onDraw()
        matrix.backMatrix()
    }



    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)
        val ratio = width.toFloat() / height.toFloat()

        matrix.frustum(-ratio,ratio,-1f,1f,3f,20f)
        matrix.setCamera(5f,5f,15f,0f,0f,0f,0f,1f,0f)

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClearColor(0.5f,0.5f,0.5f,1f)

        cube1.create()
        cube2.create()
    }
}