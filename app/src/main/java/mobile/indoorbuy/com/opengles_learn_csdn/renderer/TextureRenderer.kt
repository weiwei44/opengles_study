package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
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
 * 纹理
 */
class TextureRenderer(private val context: Context, private val oval:Float = 0f):GLSurfaceView.Renderer{
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private var programObjectId: Int = 0
    private val vertexCoords = floatArrayOf(
            -1.0f,1.0f,    //左上角
            -1.0f,-1.0f,   //左下角
            1.0f,1.0f,     //右上角
            1.0f,-1.0f     //右下角
    )
    private val textureCoords = floatArrayOf(   //纹理坐标范围（0.0 , 1.0）
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f
    )

    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private var mMVPMatrix = FloatArray(16)

    private val bitmap = BitmapFactory.decodeResource(context.resources,R.mipmap.test)


    private var textureId: Int = 0

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(programObjectId)

        val vMatrix = GLES20.glGetUniformLocation(programObjectId,"vMatrix")
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mMVPMatrix,0)

        val vPosition = GLES20.glGetAttribLocation(programObjectId,"vPosition")
        GLES20.glEnableVertexAttribArray(vPosition)
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,0,vertexBuffer)

        val vCoordinate = GLES20.glGetAttribLocation(programObjectId,"vCoordinate")
        GLES20.glEnableVertexAttribArray(vCoordinate)
        GLES20.glVertexAttribPointer(vCoordinate,2,GLES20.GL_FLOAT,false,0,textureBuffer)


        val vTexture = GLES20.glGetUniformLocation(programObjectId,"vTexture")
        GLES20.glUniform1i(vTexture,0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(vPosition)
        GLES20.glDisableVertexAttribArray(vCoordinate)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)

        val imgWidth = bitmap.width
        val imgHeight = bitmap.height
        val imgRatio = imgWidth.toFloat() / imgHeight.toFloat()
        val ratio = width.toFloat() / height.toFloat()

        if(width > height){
            if(imgRatio > ratio){
                Matrix.orthoM(mProjectMatrix,0,
                        -ratio*imgRatio,ratio*imgRatio,-1f,1f,3f,7f)
            }else{
                Matrix.orthoM(mProjectMatrix,0,
                        -ratio/imgRatio,ratio/imgRatio,-1f,1f,3f,7f)
            }
        }else{
            if(imgRatio > ratio){
                Matrix.orthoM(mProjectMatrix,0,
                        -1f,1f,-1/ratio*imgRatio,1/ratio*imgRatio,3f,7f)
            }else{
                Matrix.orthoM(mProjectMatrix,0,
                        -1f,1f,-imgRatio/ratio,imgRatio/ratio,3f,7f)
            }
        }

        Matrix.setLookAtM(mViewMatrix,0,
                0f,0f,7f,    //相机坐标,只要在平截头体内部就OK，离近平面越近，越大
                0f,0f,0f,   //目标坐标
                0f,1.0f,0f)   //相机正上方向量,像y轴看，（1,0,0）像x轴看
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0)


    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f,0.5f,0.5f,1f)

        vertexBuffer = VertexArrayHelper.readVertexBuffer(vertexCoords)
        vertexBuffer.position(0)

        textureBuffer = VertexArrayHelper.readVertexBuffer(textureCoords)
        textureBuffer.position(0)

        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.texture_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.texture_fragment_shader)

        val vertexShader  = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader,fragmentShader)
        textureId = ShaderHelper.compileTexture(bitmap)
    }

}