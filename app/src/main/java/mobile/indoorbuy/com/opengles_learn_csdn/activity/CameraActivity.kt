package mobile.indoorbuy.com.opengles_learn_csdn.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import mobile.indoorbuy.com.opengles_learn_csdn.R

/**
 * Created by BMW on 2018/6/25.
 */
class CameraActivity: AppCompatActivity(){

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()

        sample.setOnClickListener {
            //cp.takePicture()
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
}