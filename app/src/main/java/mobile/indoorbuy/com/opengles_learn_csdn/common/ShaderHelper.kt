package mobile.indoorbuy.com.opengles_learn_csdn.common

import android.graphics.Bitmap
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Log
import mobile.indoorbuy.com.opengles_learn_csdn.BuildConfig
import mobile.indoorbuy.com.opengles_learn_csdn.common.Content.TAG
import android.opengl.GLUtils
import javax.microedition.khronos.opengles.GL10


/**
 * Created by BMW on 2018/6/7.
 */
object ShaderHelper {

    /**
     * 相机预览使用EXTERNAL_OES纹理
     */
    fun createCameraTextureID(): Int {
        val texture = IntArray(1)
        //生成一个纹理
        GLES20.glGenTextures(1, texture, 0)
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0])
        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE)
        //解除纹理绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        return texture[0]
    }

    fun compileTexture(mBitmap: Bitmap): Int {
        val texture = IntArray(1)
        if (!mBitmap.isRecycled) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0)
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0)
            return texture[0]
        }
        return 0
    }

    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)
    }

    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)
    }

    fun compileShader(type: Int, shaderCode: String): Int {
        val shaderObjectId = GLES20.glCreateShader(type)
                .apply {
                    if (this == 0) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Could not create new shader.")
                        }
                        return 0
                    }
                }

        GLES20.glShaderSource(shaderObjectId, shaderCode)
        GLES20.glCompileShader(shaderObjectId)


        with(IntArray(1)) {
            GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS,
                    this, 0)
            if (this[0] == 0) {
                GLES20.glDeleteShader(shaderObjectId)
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Compilation of shader failed.")
                }
                return 0
            }
        }

        return shaderObjectId

    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = GLES20.glCreateProgram()
                .apply {
                    if (this == 0) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Could not create new program.")
                        }
                        return 0
                    }
                }

        GLES20.glAttachShader(programObjectId, vertexShaderId)
        GLES20.glAttachShader(programObjectId, fragmentShaderId)
        GLES20.glLinkProgram(programObjectId)

        with(IntArray(1)) {
            GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS,
                    this, 0)
            if (this[0] == 0) {
                GLES20.glDeleteProgram(programObjectId)
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Linking of program failed.")
                }
                return 0
            }
        }
        return programObjectId
    }
}