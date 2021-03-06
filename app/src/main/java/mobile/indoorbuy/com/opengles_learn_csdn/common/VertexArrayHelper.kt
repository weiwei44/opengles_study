package mobile.indoorbuy.com.opengles_learn_csdn.common

import mobile.indoorbuy.com.opengles_learn_csdn.common.Content.BYTES_PER_FLOAT
import mobile.indoorbuy.com.opengles_learn_csdn.common.Content.BYTES_PER_SHORT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Created by BMW on 2018/6/7.
 *
 * VAO是放在客户端的顶点数据
 */
object VertexArrayHelper{

    fun readVertexBuffer(vertexDatas:FloatArray):FloatBuffer =
        ByteBuffer
                .allocateDirect(BYTES_PER_FLOAT * vertexDatas.size)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexDatas)

    fun readVertexBuffer(vertexDatas:MutableList<Float>):FloatBuffer =
            ByteBuffer
                    .allocateDirect(BYTES_PER_FLOAT * vertexDatas.size)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .apply {
                        for(data in vertexDatas){
                            put(data)
                        }
                    }


    fun readVertexShortBuffer(vertexDatas:ShortArray):ShortBuffer =
            ByteBuffer
                    .allocateDirect(BYTES_PER_SHORT * vertexDatas.size)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer()
                    .put(vertexDatas)

    fun readVertexShortBuffer(vertexDatas:MutableList<Short>):ShortBuffer =
            ByteBuffer
                    .allocateDirect(BYTES_PER_SHORT * vertexDatas.size)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer()
                    .apply {
                        for(data in vertexDatas){
                            put(data)
                        }
                    }
}