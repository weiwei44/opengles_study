package mobile.indoorbuy.com.opengles_learn_csdn.camera

import android.hardware.Camera

/**
 * Created by BMW on 2018/6/26.
 */
data class CameInfo(var preSizeWidth:Int = 0,var preSizeHeight:Int = 0,
                    var picSizeWidth:Int = 0,var picSizeHeight:Int = 0){

    override fun toString(): String {
        return "preSizeWidth = $preSizeWidth,preSizeHeight=$preSizeHeight,picSizeWidth=$picSizeWidth,picSizeHeight=$picSizeHeight"
    }
}