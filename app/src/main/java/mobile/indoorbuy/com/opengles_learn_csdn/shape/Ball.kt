package mobile.indoorbuy.com.opengles_learn_csdn.shape

import android.content.Context
import android.opengl.GLES20
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.common.*

/**
 * Created by BMW on 2018/7/2.
 *
 * VBO-IBO 球体
 */
class Ball(private val context: Context) :Shape(){
    private var numElements: Int  //记录多少个索引点

    private val vertexBuffer: Int  //顶点buffer缓冲区
    private val textureBuffer: Int  //纹理buffer缓冲区
    private val indexBuffer:Int    //索引buffer缓冲区

    private var textureId: Int = 0 //纹理id

    init {
        val angleSpan = 5// 将球进行单位切分的角度，此数值越小划分矩形越多，球面越趋近平滑
        val radius = 1.0f// 球体半径
        var offset: Short = 0// 索引偏移
        var vAngle = 0
        val vertexList = mutableListOf<Float>() // 使用list存放顶点数据
        val textureList = mutableListOf<Float>() // 使用list存放纹理数据
        val indexList = mutableListOf<Short>()// 顶点索引数组
        while (vAngle < 180) {
            var hAngle = 0
            while (hAngle <= 360) {
                // st纹理坐标
                val s0 = hAngle / 360.0f //左上角 s
                val t0 = vAngle / 180.0f //左上角 t
                val s1 = (hAngle + angleSpan) / 360.0f //右下角s
                val t1 = (vAngle + angleSpan) / 180.0f //右下角t

                // 左上角 0
                val x0 = (radius.toDouble() * Math.sin(Math.toRadians(vAngle.toDouble())) * Math.cos(Math
                        .toRadians(hAngle.toDouble()))).toFloat()
                val y0 = (radius.toDouble() * Math.sin(Math.toRadians(vAngle.toDouble())) * Math.sin(Math
                        .toRadians(hAngle.toDouble()))).toFloat()
                val z0 = (radius * Math.cos(Math.toRadians(vAngle.toDouble()))).toFloat()
                vertexList.add(x0)
                vertexList.add(y0)
                vertexList.add(z0)
                textureList.add(s0)
                textureList.add(t0)
                // 右上角 1
                val x1 = (radius.toDouble() * Math.sin(Math.toRadians(vAngle.toDouble())) * Math.cos(Math
                        .toRadians((hAngle + angleSpan).toDouble()))).toFloat()
                val y1 = (radius.toDouble() * Math.sin(Math.toRadians(vAngle.toDouble())) * Math.sin(Math
                        .toRadians((hAngle + angleSpan).toDouble()))).toFloat()
                val z1 = (radius * Math.cos(Math.toRadians(vAngle.toDouble()))).toFloat()
                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
                textureList.add(s1)
                textureList.add(t0)
                // 右下角 2
                val x2 = (radius.toDouble() * Math.sin(Math.toRadians((vAngle + angleSpan).toDouble())) * Math
                        .cos(Math.toRadians((hAngle + angleSpan).toDouble()))).toFloat()
                val y2 = (radius.toDouble() * Math.sin(Math.toRadians((vAngle + angleSpan).toDouble())) * Math
                        .sin(Math.toRadians((hAngle + angleSpan).toDouble()))).toFloat()
                val z2 = (radius * Math.cos(Math.toRadians((vAngle + angleSpan).toDouble()))).toFloat()
                vertexList.add(x2)
                vertexList.add(y2)
                vertexList.add(z2)
                textureList.add(s1)
                textureList.add(t1)
                // 左下角 3
                val x3 = (radius.toDouble() * Math.sin(Math.toRadians((vAngle + angleSpan).toDouble())) * Math
                        .cos(Math.toRadians(hAngle.toDouble()))).toFloat()
                val y3 = (radius.toDouble() * Math.sin(Math.toRadians((vAngle + angleSpan).toDouble())) * Math
                        .sin(Math.toRadians(hAngle.toDouble()))).toFloat()
                val z3 = (radius * Math.cos(Math.toRadians((vAngle + angleSpan).toDouble()))).toFloat()
                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                textureList.add(s0)
                textureList.add(t1)

                indexList.add((offset + 0).toShort())
                indexList.add((offset + 3).toShort())
                indexList.add((offset + 2).toShort())
                indexList.add((offset + 0).toShort())
                indexList.add((offset + 2).toShort())
                indexList.add((offset + 1).toShort())
                offset = (offset + 4).toShort() // 4个顶点的偏移
                hAngle += angleSpan
            }
            vAngle += angleSpan
        }

        numElements = indexList.size
        vertexBuffer = VertexBufferHelper.readVertexBuffer(vertexList)
        textureBuffer = VertexBufferHelper.readVertexBuffer(textureList)
        indexBuffer = IndexBufferHelper.readVertexBuffer(indexList)
    }



    override fun create(){
        val vertexCode = TextResourceReader.readTextFileFromResource(context, R.raw.ball_ibo_vertex_shader)
        val fragmentCode = TextResourceReader.readTextFileFromResource(context, R.raw.ball_ibo_fragment_shader)
        val vertexShader  = ShaderHelper.compileVertexShader(vertexCode)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentCode)
        programObjectId = ShaderHelper.linkProgram(vertexShader,fragmentShader)
        textureId = TextureHelper.loadTexture(context,R.mipmap.map)
    }

    override fun onDraw() {
        GLES20.glUseProgram(programObjectId)

        val vMatrix = GLES20.glGetUniformLocation(programObjectId, "vMatrix")
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mMVPMatrix,0)

        val u_TextureUnit = GLES20.glGetUniformLocation(programObjectId,"u_TextureUnit")
        // 激活纹理单元0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // 绑定纹理对象ID
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        // 告诉shaderProgram sampler2D纹理采集器 使用纹理单元0的纹理对象
        GLES20.glUniform1i(u_TextureUnit,0)

        //纹理坐标
        val a_TextureCoordinates = GLES20.glGetAttribLocation(programObjectId,"a_TextureCoordinates")
        GLES20.glEnableVertexAttribArray(a_TextureCoordinates)
        VertexBufferHelper.setVertexAttributePointer(textureBuffer,a_TextureCoordinates,2,0,0)

        //顶点坐标
        val vPosition = GLES20.glGetAttribLocation(programObjectId, "vPosition")
        GLES20.glEnableVertexAttribArray(vPosition)
        VertexBufferHelper.setVertexAttributePointer(vertexBuffer,vPosition,3,0,0)

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,indexBuffer)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,numElements, GLES20.GL_UNSIGNED_SHORT,0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,0)

        GLES20.glDisableVertexAttribArray(vPosition)
    }
}