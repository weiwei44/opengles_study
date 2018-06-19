package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.common.*
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by BMW on 2018/6/11.
 * 图像处理
 */
class TextureHandleRenderer(private val context: Context, private val oval:Float = 0f):GLSurfaceView.Renderer{
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

        /*
        NONE(0,new float[]{0.0f,0.0f,0.0f}),    //无任何处理
        GRAY(1,new float[]{0.299f,0.587f,0.114f}),  //灰度处理
        COOL(2,new float[]{0.0f,0.0f,0.1f}),     //冷色调,增加蓝色分量
        WARM(2,new float[]{0.1f,0.1f,0.0f}),     //热色调,增加r,g分量
        BLUR(3,new float[]{0.006f,0.004f,0.002f}),  //模糊
        MAGN(4,new float[]{0.0f,0.0f,0.4f});     //放大镜
         */


        val vChangeType = GLES20.glGetUniformLocation(programObjectId,"vChangeType")
        GLES20.glUniform1i(vChangeType,4)

        val vChangeColor = GLES20.glGetUniformLocation(programObjectId,"vChangeColor")
        GLES20.glUniform3f(vChangeColor,0.0f,0.0f,0.4f)

        val uXY = GLES20.glGetUniformLocation(programObjectId,"uXY")
        GLES20.glUniform1f(uXY,ratio)

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

        //使用纹理单元0，并绑定单元纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(vPosition)
        GLES20.glDisableVertexAttribArray(vCoordinate)
    }

    private var ratio: Float = 0.0f

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)

        val imgWidth = bitmap.width
        val imgHeight = bitmap.height
        val imgRatio = imgWidth.toFloat() / imgHeight.toFloat()
        ratio = width.toFloat() / height.toFloat()

        //铺满屏幕
//        Matrix.orthoM(mProjectMatrix,0,
//                -ratio,ratio,-1f,1f,3f,7f)

        //等比缩放图片
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

        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.texture_handle_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.texture_handle_fragment_shader)

        val vertexShader  = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader,fragmentShader)
        textureId = TextureHelper.loadTexture(context,R.mipmap.test)
    }

}