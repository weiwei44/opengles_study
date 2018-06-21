package mobile.indoorbuy.com.opengles_learn_csdn.common

import android.opengl.Matrix
import java.util.*

/**
 * Created by BMW on 2018/6/21.
 */

class MatrixUtils{

    private val mMatrixCamera = FloatArray(16)  //相机矩阵
    private val mMatrixProjection = FloatArray(16)  //投影矩阵
    private var mMatrixCurrent = floatArrayOf(  //当前矩阵,默认初始值为单位矩阵
            1f,0f,0f,0f,
            0f,1f,0f,0f,
            0f,0f,1f,0f,
            0f,0f,0f,1f
    )

    private val mStack = Stack<FloatArray>()   //变换矩阵的堆栈

    //缓存当前矩阵
    fun saveMatrix(){
        mStack.push(Arrays.copyOf(mMatrixCurrent,16))
    }

    //回退到上个矩阵
    fun backMatrix(){
        mMatrixCurrent = mStack.pop()
    }

    fun clear(){
        mStack.clear()
    }

    //平移变换
    fun translate(x:Float,y:Float,z:Float){
        Matrix.translateM(mMatrixCurrent,0,x,y,z)
    }

    //旋转变换,angle逆时针为正，顺时针为负
    fun ratate(angle:Float,x:Float,y:Float,z:Float){
        Matrix.rotateM(mMatrixCurrent,0,angle,x,y,z)
    }

    //缩放
    fun scale(x:Float,y:Float,z:Float){
        Matrix.scaleM(mMatrixCurrent,0,x,y,z)
    }

    //设置相机
    fun setCamera(eyex:Float,eyey: Float,eyez: Float,
                  centerx:Float,centery: Float,centerz: Float,
                  upx:Float,upy: Float,upz: Float){
        Matrix.setLookAtM(mMatrixCamera,0,eyex,eyey,eyez,centerx,centery,centerz,upx,upy,upz)
    }

    //设置平截头体,透视投影矩阵
    fun frustum(left:Float,right:Float,bottom:Float,top:Float,near:Float,far:Float){
        Matrix.frustumM(mMatrixProjection,0,left,right,bottom,top,near,far)
    }

    //设置正交投影矩阵
    fun ortho(left:Float,right:Float,bottom:Float,top:Float,near:Float,far:Float){
        Matrix.orthoM(mMatrixProjection,0,left,right,bottom,top,near,far)
    }

    fun getFinalMatrix():FloatArray{
        val result = FloatArray(16)
        Matrix.multiplyMM(result,0,mMatrixCamera,0,mMatrixCurrent,0)
        Matrix.multiplyMM(result,0,mMatrixProjection,0,result,0)
        return result
    }
}
