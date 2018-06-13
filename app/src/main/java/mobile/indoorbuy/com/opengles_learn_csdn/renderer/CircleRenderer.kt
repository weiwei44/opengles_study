package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.common.Content
import mobile.indoorbuy.com.opengles_learn_csdn.common.ShaderHelper
import mobile.indoorbuy.com.opengles_learn_csdn.common.TextResourceReader
import mobile.indoorbuy.com.opengles_learn_csdn.common.VertexArrayHelper
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by BMW on 2018/6/11.
 * 圆
 */
class CircleRenderer(private val context: Context):GLSurfaceView.Renderer{
    private val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f) //白色
    private lateinit var vertexBuffer: FloatBuffer
    private var programObjectId: Int = 0
    private lateinit var circleCoords: FloatArray

    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(programObjectId)

        val vMatrix = GLES20.glGetUniformLocation(programObjectId,"vMatrix")
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mMVPMatrix,0)
        val vPosition = GLES20.glGetAttribLocation(programObjectId,"vPosition")
        GLES20.glEnableVertexAttribArray(vPosition)
        GLES20.glVertexAttribPointer(vPosition,3,GLES20.GL_FLOAT,false,3 * Content.BYTES_PER_FLOAT,vertexBuffer)

        //设置绘制三角形的颜色
        val vColor = GLES20.glGetUniformLocation(programObjectId,"vColor")
        GLES20.glUniform4fv(vColor,1,color,0)


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, circleCoords.size/3)
        GLES20.glDisableVertexAttribArray(vPosition)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1f,1f,3f,7f)
        Matrix.setLookAtM(mViewMatrix,0,
                0f,0f,7f,    //相机坐标,只要在平截头体内部就OK，离近平面越近，越大
                0f,0f,0f,   //目标坐标
                0f,1.0f,0f)   //相机正上方向量,像y轴看，（1,0,0）像x轴看
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0)
    }



    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f,0f,0f,1f)
        circleCoords = createVertex()
        vertexBuffer = VertexArrayHelper.readVertexBuffer(circleCoords)
        vertexBuffer.position(0)

        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.isosceles_triangle_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.triangle_fragment_shader)

        val vertexShader  = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader,fragmentShader)
    }

    private fun createVertex():FloatArray{
        val angDegSpan = 360f/100
        val radius = 0.5f

        val data = mutableListOf<Float>()
                .apply {
                    add(0f)
                    add(0f)
                    add(0f)
                    var i  = 0f
                    while (i < 360+angDegSpan){
                        add((radius*Math.sin(i*Math.PI/180f)).toFloat())
                        add((radius*Math.cos(i*Math.PI/180f)).toFloat())
                        add(0f)
                        i+=angDegSpan
                    }

                }
        val result = FloatArray(data.size)
         for (i in data.indices ){
             result[i] = data[i]
         }
        return result
    }

}