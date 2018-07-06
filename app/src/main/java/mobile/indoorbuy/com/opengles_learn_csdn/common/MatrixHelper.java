/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package mobile.indoorbuy.com.opengles_learn_csdn.common;

import android.opengl.Matrix;
import android.util.Log;

public class MatrixHelper {
    /**
     * 生成透视矩阵
     * @param m
     * @param yFovInDegrees  焦距
     * @param aspect   宽高比
     * @param n
     * @param f
     */
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect,
        float n, float f) {
        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);
        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));

        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;
        
        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;        
    }

    //通过传入图片宽高和预览宽高，计算变换矩阵，得到的变换矩阵是预览类似ImageView的centerCrop效果
    public static float[] getShowMatrix(int imgWidth,int imgHeight,int viewWidth,int viewHeight){
        float[] projection=new float[16];
        float[] camera=new float[16];
        float[] matrix=new float[16];

        float sWhImg=(float)imgWidth/imgHeight;
        float displayScale = (float)viewHeight/viewWidth;
        int height = 0;   //实际的surfaceview高
        int width = 0;

        //就算出图片到屏幕需要的实际宽高
        if (sWhImg > displayScale) {
            height = (int) (sWhImg * viewWidth);
            width = viewWidth;
        } else {
            width = (int) (viewHeight / sWhImg);
            height = viewHeight;
        }

        Log.e("weiwei","width---"+width+",height---"+height);

        if(width < height){
            Matrix.orthoM(projection,0,-(float) width/height,(float)width/height,-1,1,1,3);
        }else{
            Matrix.orthoM(projection,0,-1,1,-(float)height/width,(float)height/width,1,3);
        }

//        if(sWhImg>sWhView){
//            Matrix.orthoM(projection,0,-sWhView/sWhImg,sWhView/sWhImg,-1,1,1,3);
//        }else{
//            Matrix.orthoM(projection,0,-1,1,-sWhImg/sWhView,sWhImg/sWhView,1,3);
//        }


        Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
        Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        return matrix;
    }

    //旋转
    public static float[] rotate(float[] m,float angle){
        Matrix.rotateM(m,0,angle,0,0,1);
        return m;
    }

    //镜像
    public static float[] flip(float[] m,boolean x,boolean y){
        if(x||y){
            Matrix.scaleM(m,0,x?-1:1,y?-1:1,1);
        }
        return m;
    }
}
