package mobile.indoorbuy.com.opengles_learn_csdn.common

import android.opengl.GLES20
import android.util.Log
import mobile.indoorbuy.com.opengles_learn_csdn.common.Content.BYTES_PER_SHORT
import mobile.indoorbuy.com.opengles_learn_csdn.common.Content.TAG

/**
 * Created by BMW on 2018/7/2.
 *
 * IBO IndexBufferObject 索引缓冲区
 */
object IndexBufferHelper{
    fun readVertexBuffer(vertexDatas:ShortArray):Int {
        //向OpenGL服务端申请创建缓冲区
        val buffers = IntArray(1)
        GLES20.glGenBuffers(buffers.size,buffers,0)
        if(buffers[0] == 0){
            val error = GLES20.glGetError()
            Log.e(TAG,"申请缓冲区错误")
            return 0
        }
        // 保存申请返回的缓冲区标示
        var bufferId = buffers[0]
        // 绑定缓冲区 为 数组缓存,唯一的区别就是这里换成了GL_ELEMENT_ARRAY_BUFFER
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,buffers[0])
        // 把java数据放转至到native
        val vertexArray = VertexArrayHelper.readVertexShortBuffer(vertexDatas)
        vertexArray.position(0)
        // 把native的数据绑定保存到缓存区，注意长度为字节单位。用途是为GL_STATIC_DRAW
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,vertexArray.capacity()* BYTES_PER_SHORT,
                vertexArray,GLES20.GL_STATIC_DRAW)
        // 告诉OpenGL 解绑缓冲区的操作。
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,0)
        return bufferId
    }

    fun readVertexBuffer(vertexDatas:MutableList<Short>):Int {
        //向OpenGL服务端申请创建缓冲区
        val buffers = IntArray(1)
        GLES20.glGenBuffers(buffers.size,buffers,0)
        if(buffers[0] == 0){
            val error = GLES20.glGetError()
            Log.e(TAG,"申请缓冲区错误")
            return 0
        }
        // 保存申请返回的缓冲区标示
        var bufferId = buffers[0]
        // 绑定缓冲区 为 数组缓存,唯一的区别就是这里换成了GL_ELEMENT_ARRAY_BUFFER
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,buffers[0])
        // 把java数据放转至到native
        val vertexArray = VertexArrayHelper.readVertexShortBuffer(vertexDatas)
        vertexArray.position(0)
        // 把native的数据绑定保存到缓存区，注意长度为字节单位。用途是为GL_STATIC_DRAW
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,vertexArray.capacity()* BYTES_PER_SHORT,
                vertexArray,GLES20.GL_STATIC_DRAW)
        // 告诉OpenGL 解绑缓冲区的操作。
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,0)
        return bufferId
    }

    fun setVertexAttributePointer(bufferId:Int,attributeLocation:Int,componentCount:Int,
                                  stride:Int, dataOffset:Int){
        // 先绑定标示缓冲区，通知OpenGL要使用指定的缓冲区了。
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferId)
        // 调用接口，设置着色器程序顶点属性指针
        GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT,
                false, stride, dataOffset)
        // 告诉OpenGL 解绑缓冲区的操作。
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}