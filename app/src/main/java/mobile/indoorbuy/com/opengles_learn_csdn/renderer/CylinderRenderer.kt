package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.common.Content.BYTES_PER_FLOAT
import mobile.indoorbuy.com.opengles_learn_csdn.common.ShaderHelper
import mobile.indoorbuy.com.opengles_learn_csdn.common.TextResourceReader
import mobile.indoorbuy.com.opengles_learn_csdn.common.VertexArrayHelper
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by BMW on 2018/6/13.
 * 圆锥
 */
class CylinderRenderer(private val context: Context):GLSurfaceView.Renderer{

    private var programObjectId: Int = 0
    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    private lateinit var vertexBuffer: FloatBuffer
    private var vertexSize = 0

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        ovalTop.setMatrix(mMVPMatrix)
        ovalTop.onDrawFrame(gl)

        ovalBottom.setMatrix(mMVPMatrix)
        ovalBottom.onDrawFrame(gl)

        GLES20.glUseProgram(programObjectId)
        val vMatrix = GLES20.glGetUniformLocation(programObjectId,"vMatrix")
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mMVPMatrix,0)

        val vPosition = GLES20.glGetAttribLocation(programObjectId, "vPosition")
        GLES20.glEnableVertexAttribArray(vPosition)
        GLES20.glVertexAttribPointer(vPosition,3,GLES20.GL_FLOAT,false,0,vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,vertexSize/3)   //绘制圆柱
        GLES20.glDisableVertexAttribArray(vPosition)


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)
        val ratio = width.toFloat() / height.toFloat()

        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1f,1f,3f,20f)
        Matrix.setLookAtM(mViewMatrix,0,
                1f, -10.0f, -4f,
                0f,0f,0f,
                0f,1f,0f)
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f,0.5f,0.5f,1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        createConeVertex()

        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.cone_triangle_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.cone_triangle_fragment_shader)
        val vertexShader  = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader,fragmentShader)

        ovalTop.onSurfaceCreated(gl,config)
        ovalBottom.onSurfaceCreated(gl,config)
    }


    private lateinit var ovalTop: CircleRenderer
    private lateinit var ovalBottom: CircleRenderer

    fun createConeVertex(){
        val height = 2f  //圆锥高度
        val radius = 1f  //圆锥半径
        val angDegSpan = 360f / 100   //将圆分成100份

        ovalTop = CircleRenderer(context,height)
        ovalBottom = CircleRenderer(context,0f)

        val pos = mutableListOf<Float>().apply {
            var i = 0f
            while (i < 360f+angDegSpan){
                add(radius*Math.sin(i * Math.PI/180f).toFloat())
                add(radius*Math.cos(i * Math.PI/180f).toFloat())
                add(height)
                add(radius*Math.sin(i * Math.PI/180f).toFloat())
                add(radius*Math.cos(i * Math.PI/180f).toFloat())
                add(0f)

                i += angDegSpan
            }
        }
        vertexSize = pos.size

        vertexBuffer = VertexArrayHelper.readVertexBuffer(pos)
        vertexBuffer.position(0)
    }
}
