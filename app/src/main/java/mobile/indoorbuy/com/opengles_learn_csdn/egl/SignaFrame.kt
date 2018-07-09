package mobile.indoorbuy.com.opengles_learn_csdn.egl

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Log
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.common.ShaderHelper
import mobile.indoorbuy.com.opengles_learn_csdn.common.TextResourceReader
import mobile.indoorbuy.com.opengles_learn_csdn.common.VertexArrayHelper
import java.nio.FloatBuffer

/**
 * Created by BMW on 2018/7/6.
 */
class SignaFrame(val context:Context){
    private val FULL_RECTANGLE_COORDS = floatArrayOf(
            -1.0f, -1.0f, // 0 bottom left
            1.0f, -1.0f, // 1 bottom right
            -1.0f, 1.0f, // 2 top left
            1.0f, 1.0f)// 3 top right

    private val FULL_RECTANGLE_TEX_COORDS = floatArrayOf(
            0.0f, 0.0f, // 0 bottom left
            1.0f, 0.0f, // 1 bottom right
            0.0f, 1.0f, // 2 top left
            1.0f, 1.0f      // 3 top right
    )

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private var programObjectId: Int = 0



    init {
        vertexBuffer = VertexArrayHelper.readVertexBuffer(FULL_RECTANGLE_COORDS)
        vertexBuffer.position(0)

        textureBuffer = VertexArrayHelper.readVertexBuffer(FULL_RECTANGLE_TEX_COORDS)
        textureBuffer.position(0)
    }

    fun initProgram(){
        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.egl_signa_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.egl_signa_fragment_shader)

        val vertexShader = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader, fragmentShader)
    }

    private var uMVPMatrixLoc: Int = 0
    private var aPositionLoc: Int = 0
    private var aTextureCoordLoc: Int = 0
    private var sTexture: Int = 0

    fun setAttrOrUniform(){
        uMVPMatrixLoc = GLES20.glGetUniformLocation(programObjectId, "uMVPMatrix")
        aPositionLoc = GLES20.glGetAttribLocation(programObjectId, "aPosition")

        aTextureCoordLoc = GLES20.glGetAttribLocation(programObjectId, "aTextureCoord")

        sTexture = GLES20.glGetUniformLocation(programObjectId, "sTexture")
    }

    fun drawFrame(mTextureId:Int,mMvpMatrix:FloatArray){
        GLES20.glUseProgram(programObjectId)
        setAttrOrUniform()

        // 设置纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId)
        GLES20.glUniform1i(sTexture, 0)

        // 设置 model / view / projection 矩阵
        GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mMvpMatrix, 0)
        GlUtil.checkGlError("glUniformMatrix4fv uMVPMatrixLoc")
        // 使用简单的VAO 设置顶点坐标数据
        GLES20.glEnableVertexAttribArray(aPositionLoc)
        GLES20.glVertexAttribPointer(aPositionLoc, 2,
                GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GlUtil.checkGlError("VAO aPositionLoc")
        // 使用简单的VAO 设置纹理坐标数据
        GLES20.glEnableVertexAttribArray(aTextureCoordLoc)
        GLES20.glVertexAttribPointer(aTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, 0, textureBuffer)
        GlUtil.checkGlError("VAO aTextureCoordLoc")
        // GL_TRIANGLE_STRIP三角形带，这就为啥只需要指出4个坐标点，就能画出两个三角形了。
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        // Done -- 解绑everything
        GLES20.glDisableVertexAttribArray(aPositionLoc)
        GLES20.glDisableVertexAttribArray(aTextureCoordLoc)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glUseProgram(0)
    }
}