package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
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
 * 三角形
 */
class TriangnleRenderer(private val context: Context):GLSurfaceView.Renderer{

    private val triangleCoords = floatArrayOf(0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    )
    private val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f) //白色

    private lateinit var vertexBuffer: FloatBuffer
    private var programObjectId: Int = 0

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(programObjectId)

        val vPosition = GLES20.glGetAttribLocation(programObjectId,"vPosition")
        GLES20.glEnableVertexAttribArray(vPosition)
        GLES20.glVertexAttribPointer(vPosition,3,GLES20.GL_FLOAT,false,3 * BYTES_PER_FLOAT,vertexBuffer)

        //设置绘制三角形的颜色
        val vColor = GLES20.glGetUniformLocation(programObjectId,"vColor")
        GLES20.glUniform4fv(vColor,1,color,0)


        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, triangleCoords.size/3)
        GLES20.glDisableVertexAttribArray(vPosition)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f,0.5f,0.5f,1.0f)

        vertexBuffer = VertexArrayHelper.readVertexBuffer(triangleCoords)
        vertexBuffer.position(0)

        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.triangle_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.triangle_fragment_shader)

        val vertexShader  = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader,fragmentShader)

    }

}