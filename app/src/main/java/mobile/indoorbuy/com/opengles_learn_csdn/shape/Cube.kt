package mobile.indoorbuy.com.opengles_learn_csdn.shape

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
class Cube(private val context:Context){

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
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f
    )

    private var programObjectId: Int = 0
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

    fun onDraw() {
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


    fun created() {
        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.isosceles_triangle_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.isosceles_triangle_fragment_shader)
        val vertexShader  = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader,fragmentShader)
    }

}