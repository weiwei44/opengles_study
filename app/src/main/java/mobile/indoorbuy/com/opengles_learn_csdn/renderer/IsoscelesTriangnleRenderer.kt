package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.common.Content.BYTES_PER_FLOAT
import mobile.indoorbuy.com.opengles_learn_csdn.common.ShaderHelper
import mobile.indoorbuy.com.opengles_learn_csdn.common.TextResourceReader
import mobile.indoorbuy.com.opengles_learn_csdn.common.VertexArrayHelper
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by BMW on 2018/6/7.
 * 等腰直角三角形
 */
class IsoscelesTriangnleRenderer(private val context: Context):GLSurfaceView.Renderer{

    private val triangleCoords = floatArrayOf(0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    )
    private val color = floatArrayOf(
            0.0f, 1.0f, 0.0f, 1.0f ,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f)

    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer
    private var programObjectId: Int = 0

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(programObjectId)

        val vMatrix = GLES20.glGetUniformLocation(programObjectId,"vMatrix")
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mMVPMatrix,0)
        val vPosition = GLES20.glGetAttribLocation(programObjectId,"vPosition")
        GLES20.glEnableVertexAttribArray(vPosition)
        GLES20.glVertexAttribPointer(vPosition,3,GLES20.GL_FLOAT,false,3 * BYTES_PER_FLOAT,vertexBuffer)

        //设置绘制三角形的颜色
        val aColor = GLES20.glGetAttribLocation(programObjectId,"aColor")
        GLES20.glEnableVertexAttribArray(aColor)
        GLES20.glVertexAttribPointer(aColor,4,GLES20.GL_FLOAT,false,4 * BYTES_PER_FLOAT,colorBuffer)


        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, triangleCoords.size/3)

        GLES20.glDisableVertexAttribArray(aColor)
        GLES20.glDisableVertexAttribArray(vPosition)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
       GLES20.glViewport(0,0,width,height)
        //计算宽高比
        val ratio = width.toFloat() / height.toFloat()
        //设置透视投影
        //MatrixHelper.perspectiveM(mProjectMatrix,60f,ratio,3f,7f)
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1f,1f,3f,7f)

        //设置相机位置
        Matrix.setLookAtM(mViewMatrix,0,
                0f,0f,7f,    //相机坐标,只要在平截头体内部就OK，离近平面越近，越大
                0f,0f,0f,   //目标坐标
                0f,1.0f,0f)   //相机正上方向量,像y轴看，（1,0,0）像x轴看

        //计算变换矩阵 mProjectMatrix*mViewMatrix
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0)
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f,0.5f,0.5f,1.0f)

        vertexBuffer = VertexArrayHelper.readVertexBuffer(triangleCoords)
        vertexBuffer.position(0)

        colorBuffer = VertexArrayHelper.readVertexBuffer(color)
        colorBuffer.position(0)

        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.isosceles_triangle_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.isosceles_triangle_fragment_shader)

        val vertexShader  = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader,fragmentShader)

    }

}