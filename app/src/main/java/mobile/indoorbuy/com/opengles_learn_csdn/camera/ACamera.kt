package mobile.indoorbuy.com.opengles_learn_csdn.camera

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log

/**
 * Created by BMW on 2018/6/25.
 *
 */
abstract class ACamera{
    var config:Config = Config()
    var cameraInfo = CameInfo()
    var cameraId = 0
        protected set
    abstract fun openTexture(type: Int,surfaceTexture: SurfaceTexture,screenWidth:Int,screenHeight:Int)
    abstract fun open(type: Int)
    abstract fun close()

    open fun measureSize(width:Int, height:Int){

    }

    open fun takePicture(){

    }

    inner class Config{
        var rate: Float = 0.toFloat() //宽高比
        var minPreviewWidth: Int = 0
        var minPictureWidth: Int = 0
    }

    /**
     * 修改相机的预览尺寸，调用此方法就行
     *
     * @param camera     相机实例
     * @param viewWidth  预览的surfaceView的宽
     * @param viewHeight 预览的surfaceView的高
     */
    protected fun changePreviewSize(supportedPreviewSizes: List<Camera.Size>,screenWidth:Int,screenHeight:Int) :Camera.Size?{
        var closelySize: Camera.Size? = null//储存最合适的尺寸
        for (size in supportedPreviewSizes) { //先查找preview中是否存在与surfaceview相同宽高的尺寸
            if (size.width == screenWidth && size.height == screenHeight) {
                closelySize = size
            }
        }

        if (closelySize == null) {
            // 得到与传入的宽高比最接近的size
            val reqRatio = screenWidth.toFloat() / screenHeight.toFloat()
            var curRatio: Float
            var deltaRatio: Float
            var deltaRatioMin = java.lang.Float.MAX_VALUE
            for (size in supportedPreviewSizes) {
                if (size.width < 1024) continue//1024表示可接受的最小尺寸，否则图像会很模糊，可以随意修改
                curRatio = size.width.toFloat() / size.height.toFloat()
                deltaRatio = Math.abs(reqRatio - curRatio)
                if (deltaRatio < deltaRatioMin) {
                    deltaRatioMin = deltaRatio
                    closelySize = size
                }
            }
        }

        if (closelySize != null) {
            Log.e("tag", "尺寸修改为：" + closelySize.width + "*" + closelySize.height)
        }
        return closelySize
    }


    /**
     * 通过对比得到与宽高比最接近的尺寸（如果有相同尺寸，优先选择）
     *
     * @param surfaceWidth
     * 需要被进行对比的原宽
     * @param surfaceHeight
     * 需要被进行对比的原高
     * @param preSizeList
     * 需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    protected fun getCloselyPreSize(preSizeList: List<Camera.Size>,
                                    surfaceWidth: Int, surfaceHeight: Int): Camera.Size? {

        val ReqTmpWidth: Int
        val ReqTmpHeight: Int
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        ReqTmpWidth = surfaceHeight
        ReqTmpHeight = surfaceWidth
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (size in preSizeList) {
            if (size.width == ReqTmpWidth && size.height == ReqTmpHeight) {
                return size
            }
        }

        // 得到与传入的宽高比最接近的size
        val reqRatio = ReqTmpWidth.toFloat() / ReqTmpHeight
        var curRatio: Float
        var deltaRatio: Float
        var deltaRatioMin = java.lang.Float.MAX_VALUE
        var retSize: Camera.Size? = null
        for (size in preSizeList) {
            curRatio = size.width.toFloat() / size.height
            deltaRatio = Math.abs(reqRatio - curRatio)
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio
                retSize = size
            }
        }
        Log.e("tag", "尺寸修改为：" + retSize!!.width + "*" + retSize.height)
        return retSize
    }

}