package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import mobile.indoorbuy.com.opengles_learn_csdn.R
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
class ConeRenderer(private val context: Context):GLSurfaceView.Renderer{

    private var programObjectId: Int = 0
    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    private lateinit var vertexBuffer: FloatBuffer
    private var vertexSize = 0

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glUseProgram(programObjectId)

        val vMatrix = GLES20.glGetUniformLocation(programObjectId,"vMatrix")
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mMVPMatrix,0)

        val vPosition = GLES20.glGetAttribLocation(programObjectId, "vPosition")
        GLES20.glEnableVertexAttribArray(vPosition)
        GLES20.glVertexAttribPointer(vPosition,3,GLES20.GL_FLOAT,false,0,vertexBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,vertexSize/3)
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,1,vertexSize)

        GLES20.glDisableVertexAttribArray(vPosition)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)
        val ratio = width.toFloat() / height.toFloat()
        // near近平面到眼睛的距离，far远平面到眼睛的距离
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1f,1f,3f,10f)
        //物体坐标在z轴最大为（0,0,2）最小（0,0,0），眼睛坐标（0,0,11），近平面到眼睛距离3，
        // 所以近平面坐标（0,0,8），同理远平面坐标（0,0,1），
        // 所以，物体有一半在平截头体外面，漏在外面的物体被裁剪了，看不到咯
        //如果eyeZ设置为12f,那么刚好看不到物体，设为11.9f，就只能看到圆锥顶点
        Matrix.setLookAtM(mViewMatrix,0,
                0f, 0.0f, 11.9f,
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
    }


    fun createConeVertex(){
        val height = 2f  //圆锥高度
        val radius = 1f  //圆锥半径
        val angDegSpan = 360f / 100   //将圆分成100份
        val pos = mutableListOf<Float>().apply {
            add(0f)
            add(0f)
            add(height)

            var i = 0f
            while (i < 360f+angDegSpan){

                add(radius*Math.sin(i * Math.PI/180f).toFloat())
                add(radius*Math.cos(i * Math.PI/180f).toFloat())
                add(0f)

                i += angDegSpan
            }
        }
        vertexSize = pos.size
//        val result = FloatArray(pos.size)
//        for (i in pos.indices ){
//            result[i] = pos[i]
//        }
        vertexBuffer = VertexArrayHelper.readVertexBuffer(pos)
        vertexBuffer.position(0)
    }
}
