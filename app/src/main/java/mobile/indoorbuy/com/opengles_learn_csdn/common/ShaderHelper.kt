package mobile.indoorbuy.com.opengles_learn_csdn.common

import android.opengl.GLES20
import android.util.Log
import mobile.indoorbuy.com.opengles_learn_csdn.BuildConfig
import mobile.indoorbuy.com.opengles_learn_csdn.common.Content.TAG

/**
 * Created by BMW on 2018/6/7.
 */
object ShaderHelper{

    fun compileVertexShader(shaderCode:String):Int{
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)
    }

    fun compileFragmentShader(shaderCode: String):Int{
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)
    }

    fun compileShader(type:Int,shaderCode: String ):Int{
        val shaderObjectId = GLES20.glCreateShader(type)
                .apply {
                    if(this == 0){
                        if(BuildConfig.DEBUG){
                            Log.e(TAG,"Could not create new shader.")
                        }
                        return 0
                    }
                }

        GLES20.glShaderSource(shaderObjectId,shaderCode)
        GLES20.glCompileShader(shaderObjectId)


        with(IntArray(1)){
            GLES20.glGetShaderiv(shaderObjectId,GLES20.GL_COMPILE_STATUS,
                    this,0)
            if(this[0] == 0){
                GLES20.glDeleteShader(shaderObjectId)
                if(BuildConfig.DEBUG){
                    Log.e(TAG,"Compilation of shader failed.")
                }
                return 0
            }
        }

        return shaderObjectId

    }

    fun linkProgram(vertexShaderId:Int, fragmentShaderId: Int):Int{
        val programObjectId = GLES20.glCreateProgram()
                .apply {
                    if(this == 0){
                        if(BuildConfig.DEBUG){
                            Log.e(TAG,"Could not create new program.")
                        }
                        return 0
                    }
                }

        GLES20.glAttachShader(programObjectId,vertexShaderId)
        GLES20.glAttachShader(programObjectId,fragmentShaderId)
        GLES20.glLinkProgram(programObjectId)

        with(IntArray(1)){
            GLES20.glGetProgramiv(programObjectId,GLES20.GL_LINK_STATUS,
                    this,0)
            if(this[0] == 0){
                GLES20.glDeleteProgram(programObjectId)
                if(BuildConfig.DEBUG){
                    Log.e(TAG,"Linking of program failed.")
                }
                return 0
            }
        }
        return programObjectId
    }
}