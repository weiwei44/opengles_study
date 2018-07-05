package mobile.indoorbuy.com.opengles_learn_csdn.egl

import android.graphics.SurfaceTexture
import android.view.Surface

/**
 * Created by BMW on 2018/7/5.
 */
class WindowSurface:EglSurfaceBase{

    private var surface: Surface? = null
    private var isReleaseSurface: Boolean = true

    //将native的surface 与 EGL关联起来
    public constructor(eglCore: EGLCore,surface:Surface,isReleaseSurface:Boolean):super(eglCore){
        this.surface = surface
        this.isReleaseSurface = isReleaseSurface
        createWindowSurface(surface)
    }

    //将SurfaceTexture 与 EGL关联起来
    protected constructor(eglCore: EGLCore,surfaceTexture: SurfaceTexture):super(eglCore){
        createWindowSurface(surfaceTexture)
    }

    //释放当前EGL上下文 关联 的 surface
    fun release() {
        releaseEglSurface()
        if (surface != null && isReleaseSurface) {
            surface!!.release()
            surface = null
        }
    }
}