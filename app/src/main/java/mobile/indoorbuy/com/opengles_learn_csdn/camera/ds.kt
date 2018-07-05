package mobile.indoorbuy.com.opengles_learn_csdn.camera

import android.app.Activity
import android.os.Handler
import android.os.Message

import java.lang.ref.WeakReference

/**
 * Created by BMW on 2018/7/5.
 */

class ds {
    private class MainHandler internal constructor(activity: Activity) : Handler() {
        private val mWeakActivity: WeakReference<Activity>

        init {
            mWeakActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mWeakActivity.get() ?: return
            when (msg.what) {
                MSG_FRAME_AVAILABLE -> {
                }
                else -> super.handleMessage(msg)
            }
        }

        companion object {
            val MSG_FRAME_AVAILABLE = 1
        }
    }

    fun ssd(activity: Activity) {

        val m = WeakReference(activity)
    }
}
