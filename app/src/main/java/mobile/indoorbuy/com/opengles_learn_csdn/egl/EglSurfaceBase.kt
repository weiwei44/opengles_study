package mobile.indoorbuy.com.opengles_learn_csdn.egl

import android.annotation.TargetApi
import android.opengl.EGL14
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log


/**
 * Created by BMW on 2018/7/5.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
open class EglSurfaceBase(val mEglCore:EGLCore){
    companion object {
        val TAG = "EglSurfaceBase"
    }

    private var mEGLSurface = EGL14.EGL_NO_SURFACE
    private var mWidth = -1
    private var mHeight = -1

    /**
     * 创建要使用的渲染表面EGLSurface
     * @param Surface or SurfaceTexture.
     */
    fun createWindowSurface(surface:Any){
        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw IllegalStateException("surface already created")
        }
        mEGLSurface = mEglCore.createWindowSurface(surface)
        // 不用急着在这里创建width/height, 因为surface的大小，不同情况下都会改变。
        //mWidth = mEglCore.querySurface(mEGLSurface, EGL14.EGL_WIDTH);
        //mHeight = mEglCore.querySurface(mEGLSurface, EGL14.EGL_HEIGHT);
    }

    /**
     * 返回surface的width长度, 单位是pixels.
     */
    fun getWidth():Int =
        if (mWidth < 0) {
            mEglCore.querySurface(mEGLSurface, EGL14.EGL_WIDTH)
        } else {
            mWidth
        }


    fun getHeight(): Int =
        if (mHeight < 0) {
            mEglCore.querySurface(mEGLSurface, EGL14.EGL_HEIGHT)
        } else {
            mHeight
        }

    // 连接 EGL context 和当前 eglsurface
    fun makeCurrent() {
        mEglCore.makeCurrent(mEGLSurface)
    }

    fun makeCurrentReadFrom(readSurface: EglSurfaceBase) {
        mEglCore.makeCurrent(mEGLSurface, readSurface.mEGLSurface)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun setPresentationTime(nsecs: Long) {
        mEglCore.setPresentationTime(mEGLSurface, nsecs)
    }

    /**
     * penGL是使用双缓冲机制的渲染面的，所以我们makeCurrent之后，
     * 还需要每帧的交替（swapBuffers）读写的surface
     */
    fun swapBuffers(): Boolean {
        val result = mEglCore.swapBuffers(mEGLSurface)
        if (!result) {
            Log.e(TAG, "WARNING: swapBuffers() failed")
        }
        return result
    }

    // 释放 EGL surface.
    fun releaseEglSurface() {
        mEglCore.makeNothingCurrent()
        mEglCore.releaseSurface(mEGLSurface)
        mEGLSurface = EGL14.EGL_NO_SURFACE
        mHeight = -1
        mWidth = mHeight
    }
}