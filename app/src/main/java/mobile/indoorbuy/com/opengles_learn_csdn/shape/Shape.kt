package mobile.indoorbuy.com.opengles_learn_csdn.shape

/**
 * Created by BMW on 2018/7/2.
 */
abstract class Shape{
    protected var mMVPMatrix = FloatArray(16)
    protected var programObjectId: Int = 0

    abstract fun create()
    abstract fun onDraw()
    fun setMatrix(matrix:FloatArray){
        mMVPMatrix = matrix
    }
}