package mobile.indoorbuy.com.opengles_learn_csdn.egl

import android.opengl.GLES20
import android.util.Log


/**
 * Created by BMW on 2018/7/5.
 */
object GlUtil{
    fun checkGlError(op: String) {
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            val msg = op + ": glError 0x" + Integer.toHexString(error)
            Log.e("weiwei", msg)
            throw RuntimeException(msg)
        }
    }
}