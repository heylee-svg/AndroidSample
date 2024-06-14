package com.example.android.apis.app

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.android.apis.IRemoteService
import com.example.android.apis.R

/**
 *
 * @author: denghg
 * @date: 2024/6/14
 */
/**
 * Examples of behavior of different bind flags.
 */
// BEGIN_INCLUDE(calling_a_service)
class BindingOptions : Activity() {
    var mCurConnection: ServiceConnection? = null
    var mCallbackText: TextView? = null
    var mBindIntent: Intent? = null

    internal inner class MyServiceConnection : ServiceConnection {
        val mUnbindOnDisconnect: Boolean

        constructor() {
            mUnbindOnDisconnect = false
        }

        constructor(unbindOnDisconnect: Boolean) {
            mUnbindOnDisconnect = unbindOnDisconnect
        }

        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            if (mCurConnection !== this) {
                return
            }
            mCallbackText!!.text = "Attached."
            Toast.makeText(
                this@BindingOptions, R.string.remote_service_connected,
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (mCurConnection !== this) {
                return
            }
            mCallbackText!!.text = "Disconnected."
            Toast.makeText(
                this@BindingOptions, R.string.remote_service_disconnected,
                Toast.LENGTH_SHORT
            ).show()
            if (mUnbindOnDisconnect) {
                unbindService(this)
                mCurConnection = null
                Toast.makeText(
                    this@BindingOptions, R.string.remote_service_unbind_disconn,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Standard initialization of this activity.  Set up the UI, then wait
     * for the user to poke it before doing anything.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.remote_binding_options)

        // Watch for button clicks.
        var button = findViewById<View>(R.id.bind_normal) as Button
        button.setOnClickListener(mBindNormalListener)
        button = findViewById<View>(R.id.bind_not_foreground) as Button
        button.setOnClickListener(mBindNotForegroundListener)
        button = findViewById<View>(R.id.bind_above_client) as Button
        button.setOnClickListener(mBindAboveClientListener)
        button = findViewById<View>(R.id.bind_allow_oom) as Button
        button.setOnClickListener(mBindAllowOomListener)
        button = findViewById<View>(R.id.bind_waive_priority) as Button
        button.setOnClickListener(mBindWaivePriorityListener)
        button = findViewById<View>(R.id.bind_important) as Button
        button.setOnClickListener(mBindImportantListener)
        button = findViewById<View>(R.id.bind_with_activity) as Button
        button.setOnClickListener(mBindWithActivityListener)
        button = findViewById<View>(R.id.unbind) as Button
        button.setOnClickListener(mUnbindListener)
        mCallbackText = findViewById<View>(R.id.callback) as TextView
        mCallbackText!!.text = "Not attached."
        mBindIntent = Intent(this, RemoteService::class.java)
        mBindIntent!!.setAction(IRemoteService::class.java.getName())
    }

    private val mBindNormalListener = View.OnClickListener {
        if (mCurConnection != null) {
            unbindService(mCurConnection!!)
            mCurConnection = null
        }
        val conn: ServiceConnection = MyServiceConnection()
        if (bindService(mBindIntent!!, conn, BIND_AUTO_CREATE)) {
            mCurConnection = conn
        }
    }
    private val mBindNotForegroundListener = View.OnClickListener {
        if (mCurConnection != null) {
            unbindService(mCurConnection!!)
            mCurConnection = null
        }
        val conn: ServiceConnection = MyServiceConnection()
        if (bindService(
                mBindIntent!!, conn,
                BIND_AUTO_CREATE or BIND_NOT_FOREGROUND
            )
        ) {
            mCurConnection = conn
        }
    }
    private val mBindAboveClientListener = View.OnClickListener {
        if (mCurConnection != null) {
            unbindService(mCurConnection!!)
            mCurConnection = null
        }
        val conn: ServiceConnection = MyServiceConnection()
        if (bindService(
                mBindIntent!!,
                conn, BIND_AUTO_CREATE or BIND_ABOVE_CLIENT
            )
        ) {
            mCurConnection = conn
        }
    }
    private val mBindAllowOomListener = View.OnClickListener {
        if (mCurConnection != null) {
            unbindService(mCurConnection!!)
            mCurConnection = null
        }
        val conn: ServiceConnection = MyServiceConnection()
        if (bindService(
                mBindIntent!!, conn,
                BIND_AUTO_CREATE or BIND_ALLOW_OOM_MANAGEMENT
            )
        ) {
            mCurConnection = conn
        }
    }
    private val mBindWaivePriorityListener = View.OnClickListener {
        if (mCurConnection != null) {
            unbindService(mCurConnection!!)
            mCurConnection = null
        }
        val conn: ServiceConnection = MyServiceConnection(true)
        if (bindService(
                mBindIntent!!, conn,
                BIND_AUTO_CREATE or BIND_WAIVE_PRIORITY
            )
        ) {
            mCurConnection = conn
        }
    }
    private val mBindImportantListener = View.OnClickListener {
        if (mCurConnection != null) {
            unbindService(mCurConnection!!)
            mCurConnection = null
        }
        val conn: ServiceConnection = MyServiceConnection()
        if (bindService(
                mBindIntent!!, conn,
                BIND_AUTO_CREATE or BIND_IMPORTANT
            )
        ) {
            mCurConnection = conn
        }
    }
    private val mBindWithActivityListener = View.OnClickListener {
        if (mCurConnection != null) {
            unbindService(mCurConnection!!)
            mCurConnection = null
        }
        val conn: ServiceConnection = MyServiceConnection()
        if (bindService(
                mBindIntent!!, conn,
                BIND_AUTO_CREATE or BIND_ADJUST_WITH_ACTIVITY
                        or BIND_WAIVE_PRIORITY
            )
        ) {
            mCurConnection = conn
        }
    }
    private val mUnbindListener = View.OnClickListener {
        if (mCurConnection != null) {
            unbindService(mCurConnection!!)
            mCurConnection = null
        }
    }
}