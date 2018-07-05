package mobile.indoorbuy.com.opengles_learn_csdn.egl

import android.annotation.TargetApi
import android.opengl.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import java.lang.RuntimeException
import android.opengl.EGLExt.EGL_RECORDABLE_ANDROID
import android.opengl.EGL14
import mobile.indoorbuy.com.opengles_learn_csdn.R.id.surface
import android.graphics.SurfaceTexture
import android.view.Surface


/**
 * Created by BMW on 2018/7/5.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class EGLCore(var sharedContext: EGLContext? = null, val flags: Int = FLAG_TRY_GLES2) {
    companion object {
        val FLAG_TRY_GLES2: Int = 0x02
        val FLAG_TRY_GLES3: Int = 0x04
        val FLAG_RECORDABLE = 0x01
    }

    private val TAG = "EGLCore"

    private var mEGLDisplay = EGL14.EGL_NO_DISPLAY
    private var mEGLContext = EGL14.EGL_NO_CONTEXT
    private var mEGLConfig: EGLConfig? = null
    private var mGlVersion = -1


    init {
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("EGL already set up")
        }
        if (sharedContext == null) {
            sharedContext = EGL14.EGL_NO_CONTEXT
        }
        // 1、获取EGLDisplay对象
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("unable to get EGL14 display")
        }
        // 2、初始化与EGLDisplay之间的关联。
        val version = IntArray(2)
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            mEGLDisplay = null
            throw RuntimeException("unable to initialize EGL14")
        }

        // 3、获取EGLConfig对象,创建EGLContext 实例
        if ((flags and FLAG_TRY_GLES3) != 0) {
            val config = getConfig(flags, 3)
            if (config != null) {
                //传入版本号的信息，然后以单独一个EGL_NONE为结束符标志
                val attrib3_list = intArrayOf(
                        EGL14.EGL_CONTEXT_CLIENT_VERSION,
                        3,
                        EGL14.EGL_NONE)
                val context = EGL14.eglCreateContext(mEGLDisplay, config, sharedContext, attrib3_list, 0)
                if (EGL14.eglGetError() == EGL14.EGL_SUCCESS) {
                    Log.e(TAG, "获取 GLES 3 config")
                    mEGLConfig = config
                    mEGLContext = context
                    mGlVersion = 3
                }
            }
        }

        //如果只要求GLES版本2  又或者GLES3失败了。
        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
            Log.e(TAG, "Trying GLES 2")
            val config = getConfig(flags, 2)
            val attrib2_list = intArrayOf(
                    EGL14.EGL_CONTEXT_CLIENT_VERSION,
                    2,
                    EGL14.EGL_NONE)
            val context = EGL14.eglCreateContext(mEGLDisplay, config, sharedContext, attrib2_list, 0)
            if (EGL14.eglGetError() == EGL14.EGL_SUCCESS) {
                Log.e(TAG, "获取 GLES 2 config")
                mEGLConfig = config
                mEGLContext = context
                mGlVersion = 2
            }
        }
    }

    fun getGlVersion(): Int {
        return mGlVersion
    }

    /**
     * 从本地设备中寻找合适的 EGLConfig.
     */
    private fun getConfig(flags: Int, version: Int): EGLConfig? {
        var renderableType = EGL14.EGL_OPENGL_ES2_BIT  //渲染模式
        if (version >= 3) {
            renderableType = renderableType or EGLExt.EGL_OPENGL_ES3_BIT_KHR
        }

        val attribList = intArrayOf(
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                //EGL14.EGL_DEPTH_SIZE, 16,
                //EGL14.EGL_STENCIL_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, renderableType,
                EGL14.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL14.EGL_NONE
        )
        if (flags and FLAG_RECORDABLE !== 0) { //判断flags是否附带FLAG_RECORDABLE
            attribList[attribList.size - 3] = EGLExt.EGL_RECORDABLE_ANDROID
            // EGLExt.EGL_RECORDABLE_ANDROID;0x3142(required android sdk 26)
            // 如果说希望保留自己的最低版本SDK，我们可以自己定义一个EGL_RECORDABLE_ANDROID=0x3142;
            attribList[attribList.size - 2] = 1 //把占位填充成EGLExt.EGL_RECORDABLE_ANDROID = 1；声明当前EGL是可录屏的
        }
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        if (!EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.size, numConfigs, 0)) {
            Log.e(TAG, "unable to find RGBA8888 / $version EGLConfig")
            return null
        }
        return configs[0]
    }

    /**
     * 5.创建一个 EGL+Surface
     * @param surface
     * @return
     */
    fun createWindowSurface(surface: Any): EGLSurface {
        if ((surface !is Surface) && (surface !is SurfaceTexture)) {
            throw RuntimeException("invalid surface: $surface")
        }
        // 创建EGLSurface, 绑定传入进来的surface
        val surfaceAttribs = intArrayOf(EGL14.EGL_NONE)

        val eglSurface = EGL14.eglCreateWindowSurface(mEGLDisplay,mEGLConfig,surface,surfaceAttribs,0)
                ?: throw RuntimeException("surface was null")

        GlUtil.checkGlError("eglCreateWindowSurface")
        return eglSurface
    }

    // 查询当前surface的状态值。
    fun querySurface(eglSurface: EGLSurface, what: Int): Int {
        val value = IntArray(1)
        EGL14.eglQuerySurface(mEGLDisplay, eglSurface, what, value, 0)
        return value[0]
    }

    fun makeCurrent(eglSurface: EGLSurface) {
        if (mEGLDisplay === EGL14.EGL_NO_DISPLAY) {
            Log.e(TAG, "NOTE: makeCurrent w/r display")
        }
        if (!EGL14.eglMakeCurrent(mEGLDisplay, eglSurface, eglSurface, mEGLContext)) {
            throw RuntimeException("eglMakeCurrent failed")
        }
    }

    fun makeCurrent(drawSurface: EGLSurface, readSurface: EGLSurface) {
        if (mEGLDisplay === EGL14.EGL_NO_DISPLAY) {
            Log.e(TAG, "NOTE: makeCurrent w/o display")
        }
        if (!EGL14.eglMakeCurrent(mEGLDisplay, drawSurface, readSurface, mEGLContext)) {
            throw RuntimeException("eglMakeCurrent(draw,read) failed")
        }
    }

    /**
     * penGL是使用双缓冲机制的渲染面的，所以我们makeCurrent之后，
     * 还需要每帧的交替（swapBuffers）读写的surface
     */
    fun swapBuffers(eglSurface: EGLSurface): Boolean {
        return EGL14.eglSwapBuffers(mEGLDisplay, eglSurface)
    }


    /**
     * 断开并释放与EGLSurface关联的EGLContext对象
     */
    fun makeNothingCurrent() {
        if (!EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_CONTEXT)) {
            throw RuntimeException("eglMakeCurrent To EGL_NO_SURFACE failed")
        }
    }

    /**
     * 删除EGLSurface对象
     */
    fun releaseSurface(eglSurface: EGLSurface) {
        EGL14.eglDestroySurface(mEGLDisplay, eglSurface)
    }

    // 释放EGL资源
    fun release() {
        if (mEGLDisplay !== EGL14.EGL_NO_DISPLAY) {
            // Android 使用一个引用计数EGLDisplay。
            // 因此，对于每个eglInitialize，我们需要一个eglTerminate。
            EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT) // 确保EglSurface和EGLContext已经分离
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext)
            EGL14.eglReleaseThread()
            EGL14.eglTerminate(mEGLDisplay)
        }
        mEGLDisplay = EGL14.EGL_NO_DISPLAY
        mEGLContext = EGL14.EGL_NO_CONTEXT
        mEGLConfig = null
    }
}