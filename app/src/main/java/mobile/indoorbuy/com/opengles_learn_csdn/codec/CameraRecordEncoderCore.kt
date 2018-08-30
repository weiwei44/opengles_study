package mobile.indoorbuy.com.opengles_learn_csdn.codec

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.Surface
import java.io.File


/**
 * Created by BMW on 2018/7/10.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraRecordEncoderCore(val width: Int,
                              val heght: Int,
                              val bitRate: Int,
                              val outFile: File) {

    private val FRAME_RATE = 30  //30fps
    private val I_FRAME_INTERVAL = 5   //I-frams 间隔5s

    private val mVideoEncoder: MediaCodec
    public val mInputSurface: Surface
    private val mMuxer: MediaMuxer

    private var mBufferInfo: MediaCodec.BufferInfo
    private var mTrackIndex: Int = -1
    private var mMuxerStarted: Boolean = false
    private val TIMEOUT_USEC = 10000
    init {
        // MediaFormat.MIMETYPE_VIDEO_AVC = "video/avc"; // H.264 Advanced Video Coding
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, heght)
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL)
        //设置输入源类型为原生Surface
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)

        mVideoEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mVideoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)


        //获取编码喂养数据的输入源surface
        mInputSurface = mVideoEncoder.createInputSurface()
        mVideoEncoder.start()
        // 4. 创建混合器，但我们不能在这里start，因为我们还没有编码后的视频数据

        if(outFile.exists())outFile.delete()

        mMuxer = MediaMuxer(outFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        mBufferInfo = MediaCodec.BufferInfo()
        mTrackIndex = -1
        mMuxerStarted = false
    }


    /**
     * endOfStream是代表是否编码结束的终结符
     * 如果是false就是正常请求输入数据去编码，按正常流程走这次编码操作
     * 如果是true我们需要告诉编码器编码工作结束了，发送一个EOS结束标志位到输入源
     * 然后等到我们在编码输出的数据发现EOS的时候，证明最后的一批编码数据已经编码成功了
     */
    fun encoder(endOfStream: Boolean) {
        if (endOfStream) {
            Log.e("weiwei", "sending ESO to encoder")
            mVideoEncoder.signalEndOfInputStream()
        }

        //获取编码输出队列
        var encoderOutputBuffers = mVideoEncoder.outputBuffers
        while (true) {
            // 从编码的输出队列中检索出各种状态，对应处理。
            // 参数一是MediaCodec.BufferInfo，主要是用来承载对应buffer的附加信息。
            // 参数二是超时时间，请注意单位是微秒，1毫秒=1000微秒，这里设置10毫秒
            val encoderStatus = mVideoEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC.toLong())
            Log.e("weiwei", "开始 == "+encoderStatus)
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // 暂时还没输出的数据能捕获
                if (!endOfStream) {
                    break
                } else {
                    Log.e("weiwei", "no output available, spinning to await EOS")
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                //这个状态说明输出队列对象改变了，请重新获取一遍
                encoderOutputBuffers = mVideoEncoder.outputBuffers
                Log.e("weiwei", "队列对象改变")
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // 当我们接收到编码后的输出数据，会通过格式已转变这个标志触发，而且只会发生一次格式转变
                // 因为不可能从设置指定的格式变成其他，难不成一个视频能有两种编码格式
                if (mMuxerStarted) {
                    throw RuntimeException("format changed twice")
                }
                val videoFormat = mVideoEncoder.outputFormat
                // 现在我们已经得到想要的编码数据了，让我们开始合成进mp4容器文件里面吧。
                mTrackIndex = mMuxer.addTrack(videoFormat)
                // 获取track轨道号，等下写入编码数据的时候需要用到
                mMuxer.start()
                mMuxerStarted = true

            } else if (encoderStatus < 0) {
                Log.e("weiwei", "unexpected result from encoder.dequeueOutputBuffer:$encoderStatus")
            } else {
                //各种状态处理之后，大于0的encoderStatus则是指出了编码数据是在编码队列的具体位置
                val encodedData = encoderOutputBuffers[encoderStatus]
                        ?: throw RuntimeException("encoderOutputBuffer $encoderStatus was null")

                if ((mBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // 这表明，标记为这样的缓冲器包含编解码器初始化/编解码器特定数据而不是媒体数据
                    Log.e("weiwei", "ignoring BUFFER_FLAG_CODEC_CONFIG")
                    mBufferInfo.size = 0
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw RuntimeException("muxer hasn't started")
                    }

                    encodedData.position(mBufferInfo.offset)
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size)
                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo)
                    Log.e("weiwei", "sent ${mBufferInfo.size}  bytes to muxer, ts=${mBufferInfo.presentationTimeUs}")
                }

                // 释放 编码器输出队列中 指定位置的buffer，第二个参数指定是否将其buffer渲染到解码Surface
                mVideoEncoder.releaseOutputBuffer(encoderStatus, false)

                if ((mBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.e("weiwei", "reached end of stream unexpectedly")
                    } else {
                        Log.e("weiwei", "end of stream reached")
                    }
                    break
                }
            }


        }
    }


    fun release() {
        mVideoEncoder.stop()
        mVideoEncoder.release()

        if(mTrackIndex != -1){
            mMuxer.stop()
            mMuxer.release()
        }
    }

}