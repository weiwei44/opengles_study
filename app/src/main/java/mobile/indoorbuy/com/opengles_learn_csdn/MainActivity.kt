package mobile.indoorbuy.com.opengles_learn_csdn

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import mobile.indoorbuy.com.opengles_learn_csdn.renderer.CircleRenderer
import mobile.indoorbuy.com.opengles_learn_csdn.renderer.CubeRenderer
import mobile.indoorbuy.com.opengles_learn_csdn.renderer.IsoscelesTriangnleRenderer
import mobile.indoorbuy.com.opengles_learn_csdn.renderer.TriangnleRenderer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val surface = GLSurfaceView(this)

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        with(activityManager.run {
            deviceConfigurationInfo.reqGlEsVersion >= 0x20000
        }){
            if(this)
                surface.setEGLContextClientVersion(2)
        }
        surface.setRenderer(CubeRenderer(this))
        setContentView(surface)
    }

    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}
