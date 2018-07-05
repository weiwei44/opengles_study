package mobile.indoorbuy.com.opengles_learn_csdn

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_mian_gl.*
import mobile.indoorbuy.com.opengles_learn_csdn.egl.EGLView
import mobile.indoorbuy.com.opengles_learn_csdn.renderer.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import mobile.indoorbuy.com.opengles_learn_csdn.activity.AlarmclockReceive
import java.util.*


class MainActivity : AppCompatActivity() {

    private var lastClickTime = 0

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_mian_gl)
        requestPermissions()
        val eglView = EGLView(this)
        setContentView(eglView)

//        setAlartTime()
//        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        with(activityManager.run {
//            deviceConfigurationInfo.reqGlEsVersion >= 0x20000
//        }){
//            if(this)
//                surface.setEGLContextClientVersion(2)
//        }
//        surface.setRenderer(CameraRenderer(this,surface))
//        surface.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY




//        val render = BallIBO2Renderer(this)
//        surface.setRenderer(render)
//        surface.isClickable = true
//        surface.setOnTouchListener(View.OnTouchListener { _, event ->
//            when {
//                event.action == MotionEvent.ACTION_DOWN -> {
//
//
//                    if (System.currentTimeMillis().toInt() - lastClickTime < 500) {
//                        lastClickTime = 0
//                        surface.queueEvent{ render.handleDoubleClick() }
//                    } else {
//                        lastClickTime = System.currentTimeMillis().toInt()
//                    }
//
//                    val x = event.x
//                    val y = event.y
//                    surface.queueEvent {
//                        render.handleTouchDown(x,y)
//                    }
//
//                }
//                event.action == MotionEvent.ACTION_MOVE -> {
//                    val x = event.x
//                    val y = event.y
//                    surface.queueEvent {
//                        render.handleTouchDrag(x,y)
//                    }
//                }
//                event.action == MotionEvent.ACTION_UP -> {
//                    val x = event.x
//                    val y = event.y
//                    surface.queueEvent {
//                        render.handleTouchUp(x,y)
//                    }
//                }
//                else -> return@OnTouchListener false
//            }
//            true
//        })
    }




    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun requestPermissions(){
        RxPermissions(this)
                .requestEach(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            // 用户已经同意该权限
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        }
                        else -> {
                            // 用户拒绝了该权限，并且选中『不再询问』
                        }
                    }
                }
    }

    private val INTERVAL = 1000 * 60 * 60 * 24// 24h
    private fun setAlartTime() {
        val alarmService = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val instance = Calendar.getInstance()
        //获取当前毫秒值
        val systemTime = System.currentTimeMillis()
        //是设置日历的时间，主要是让日历的年月日和当前同步
        instance.setTimeInMillis(systemTime)

        instance.setTimeZone(TimeZone.getTimeZone("GMT+8")) //  这里时区需要设置一下，不然会有8个小时的时间差
        //每天8点提示
        instance.set(Calendar.HOUR_OF_DAY, 10)
        instance.set(Calendar.MINUTE, 42)
        instance.set(Calendar.SECOND, 0)
        //获取上面设置的毫秒值
        val selectTime = instance.getTimeInMillis()
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始

        Log.e("weiwei","systemTime = ${systemTime},selectTime = ${selectTime}")
        if (systemTime > selectTime) {
            instance.add(Calendar.DAY_OF_MONTH, 1)
        }

        val alarmIntent = Intent(this, AlarmclockReceive::class.java)
        alarmIntent.putExtra("type", 1)
        val broadcast = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
        alarmService.setRepeating(AlarmManager.RTC_WAKEUP, instance.getTimeInMillis(), INTERVAL.toLong(), broadcast)
    }
}
