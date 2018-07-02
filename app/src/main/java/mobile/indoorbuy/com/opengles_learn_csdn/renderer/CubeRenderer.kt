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
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by BMW on 2018/6/12.
 * 立方体
 */
class CubeRenderer(private val context:Context):GLSurfaceView.Renderer{

    val cubePositions = floatArrayOf(
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f    //反面右上7
    )

    val index = shortArrayOf(
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2     //下面
    )

    val color = floatArrayOf(
            1f,0f,0f,1f,
            1f,0f,1f,1f,
            0f,0f,1f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,0f,1f,1f,
            1f,0f,1f,1f,
            1f,0f,0f,1f
    )

    private var programObjectId: Int = 0
    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private var mMVPMatrix = FloatArray(16)

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer
    private lateinit var vertexIndexBuffer: ShortBuffer

    init {
        vertexBuffer = VertexArrayHelper.readVertexBuffer(cubePositions)
        vertexBuffer.position(0)
        colorBuffer = VertexArrayHelper.readVertexBuffer(color)
        colorBuffer.position(0)
        vertexIndexBuffer = VertexArrayHelper.readVertexShortBuffer(index)
        vertexIndexBuffer.position(0)
    }

    fun setMatrix(matrix:FloatArray){
        mMVPMatrix = matrix
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(programObjectId)

        val vMatrix = GLES20.glGetUniformLocation(programObjectId, "vMatrix")
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mMVPMatrix,0)

        val vPosition = GLES20.glGetAttribLocation(programObjectId, "vPosition")
        GLES20.glEnableVertexAttribArray(vPosition)
        GLES20.glVertexAttribPointer(vPosition,3,GLES20.GL_FLOAT,false,0,vertexBuffer)

        val aColor = GLES20.glGetAttribLocation(programObjectId,"aColor")
        GLES20.glEnableVertexAttribArray(aColor)
        GLES20.glVertexAttribPointer(aColor,4,GLES20.GL_FLOAT,false,0,colorBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.size,GLES20.GL_UNSIGNED_SHORT,vertexIndexBuffer)

        GLES20.glDisableVertexAttribArray(vPosition)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1f,1f,3f,20f)
        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f,0.0f,15f,      //眼睛位置可以直接设置为（5,5,15），从斜面去看正方体，这样就不用旋转了
                0f,0f,0f,
                0f,1f,0f)
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0)

        //我们眼睛在（0,0,15），是从z轴直线看过出，所以只能看到一个面，旋转一下，更有立体感
        Matrix.rotateM(mMVPMatrix,0,-45f,0f,1f,0f) //绕y轴逆时针旋转45度
        Matrix.rotateM(mMVPMatrix,0,-45f,1f,0f,0f)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClearColor(0.5f,0.5f,0.5f,1f)

        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.isosceles_triangle_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.isosceles_triangle_fragment_shader)
        val vertexShader  = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader,fragmentShader)
    }

}