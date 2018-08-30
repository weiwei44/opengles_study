package mobile.indoorbuy.com.opengles_learn_csdn.common

import android.opengl.GLES20



/**
 * Created by BMW on 2018/7/13.
 */
class FrameBufferFBO{
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var frameBufferId: Int = 0
    private var textureId: Int = 0
    fun FrameBuffer() {
        mWidth = 0
        mHeight = 0
        frameBufferId = 0
        textureId = 0
    }

    fun getTextureId(): Int {
        return textureId
    }

    fun isInstantiation(): Boolean {
        return mWidth != 0 || mHeight != 0
    }

    fun setup(width: Int, height: Int): Boolean {
        this.mWidth = width
        this.mHeight = height

        //创建FBO
        val frameBuffers = IntArray(1)
        GLES20.glGenFramebuffers(1, frameBuffers, 0)
        if (frameBuffers[0] == 0) {
            val i = GLES20.glGetError()
            throw RuntimeException("Could not create a new frame buffer object, glErrorString : " + GLES20.glGetString(i))
        }
        frameBufferId = frameBuffers[0]
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        return true
    }

    fun begin(): Boolean {
        if (textureId == 0) {
            textureId = createFBOTexture(mWidth, mHeight, GLES20.GL_RGBA)
        }
        //绑定fbo进行操作
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId)
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, //说明FBO挂接操作
                GLES20.GL_COLOR_ATTACHMENT0,  //指定挂接区是单元0
                GLES20.GL_TEXTURE_2D,  //说明挂接的是纹理对象
                textureId,   //具体挂接的纹理对象
                0)
        return true
    }

    /**
     * //创建指定format的纹理对象
     */
    private fun createFBOTexture(width: Int, height: Int, format: Int): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            val i = GLES20.glGetError()
            throw RuntimeException("Could not create a new texture buffer object, glErrorString : " + GLES20.glGetString(i))
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height,
                0, format, GLES20.GL_UNSIGNED_BYTE, null)
        return textureIds[0]
    }

    //解绑FBO
    fun end() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    fun release() {
        mWidth = 0
        mHeight = 0
        GLES20.glDeleteFramebuffers(1, intArrayOf(frameBufferId), 0)
        GLES20.glDeleteTextures(1, intArrayOf(textureId), 0)
        frameBufferId = 0
        textureId = 0
    }

}