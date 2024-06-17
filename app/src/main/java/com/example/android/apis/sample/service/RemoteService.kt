package com.example.android.apis.sample.service


/**
 *
 * @author: denghg
 * @date: 2024/6/14
 */
/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

import android.annotation.SuppressLint

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service

import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Process
import android.os.RemoteCallbackList
import android.os.RemoteException
import android.util.Log

import android.widget.Toast

import com.example.android.apis.R
import com.example.android.apis.sample.IRemoteService
import com.example.android.apis.sample.IRemoteServiceCallback
import com.example.android.apis.sample.ISecondary


/**
 * This is an example of implementing an application service that runs in a
 * different process than the application.  Because it can be in another
 * process, we must use IPC to interact with it.  The
 * [Controller] and [Binding] classes
 * show how to interact with the service.
 *
 *
 * Note that most applications **do not** need to deal with
 * the complexity shown here.  If your application simply has a service
 * running in its own process, the [LocalService] sample shows a much
 * simpler way to interact with it.
 */
class RemoteService : Service() {
    /**
     * This is a list of callbacks that have been registered with the
     * service.  Note that this is package scoped (instead of private) so
     * that it can be accessed more efficiently from inner classes.
     */
    val mCallbacks: RemoteCallbackList<IRemoteServiceCallback> =
        RemoteCallbackList<IRemoteServiceCallback>()
    var mValue = 0
    var mNM: NotificationManager? = null
    override fun onCreate() {
        mNM = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Display a notification about us starting.
        showNotification()

        // While this service is running, it will continually increment a
        // number.  Send the first message that is used to perform the
        // increment.
        mHandler.sendEmptyMessage(REPORT_MSG)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("RemoteService", "Received start id $startId: $intent")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        // Cancel the persistent notification.
        mNM!!.cancel(R.string.remote_service_started)

        // Tell the user we stopped.
        Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show()

        // Unregister all callbacks.
        mCallbacks.kill()

        // Remove the next pending message to increment the counter, stopping
        // the increment loop.
        mHandler.removeMessages(REPORT_MSG)
    }

    // BEGIN_INCLUDE(exposing_a_service)
    override fun onBind(intent: Intent): IBinder? {
        // Select the interface to return.  If your service only implements
        // a single interface, you can just return it here without checking
        // the Intent.
        if (IRemoteService::class.java.getName() == intent.action) {
            return mBinder
        }
        return if (ISecondary::class.java.getName() == intent.action) {
            mSecondaryBinder
        } else null
    }

    /**
     * The IRemoteInterface is defined through IDL
     */
    private val mBinder: IRemoteService.Stub = object : IRemoteService.Stub() {
        override fun registerCallback(cb: IRemoteServiceCallback?) {
            if (cb != null) mCallbacks.register(cb)
        }

        override fun unregisterCallback(cb: IRemoteServiceCallback?) {
            if (cb != null) mCallbacks.unregister(cb)
        }
    }

    /**
     * A secondary interface to the service.
     */
    private val mSecondaryBinder: ISecondary.Stub = object : ISecondary.Stub() {


        override fun getPid(): Int {
            return Process.myPid()
        }

        override fun basicTypes(
            anInt: Int, aLong: Long, aBoolean: Boolean,
            aFloat: Float, aDouble: Double, aString: String?
        ) {
        }
    }

    // END_INCLUDE(exposing_a_service)
    override fun onTaskRemoved(rootIntent: Intent) {
        Toast.makeText(this, "Task removed: $rootIntent", Toast.LENGTH_LONG).show()
    }

    /**
     * Our Handler used to execute operations on the main thread.  This is used
     * to schedule increments of our value.
     */
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                REPORT_MSG -> {

                    // Up it goes.
                    val value = ++mValue

                    // Broadcast to all clients the new value.
                    val N = mCallbacks.beginBroadcast()
                    var i = 0
                    while (i < N) {
                        try {
                            mCallbacks.getBroadcastItem(i).valueChanged(value)
                        } catch (e: RemoteException) {
                            // The RemoteCallbackList will take care of removing
                            // the dead object for us.
                        }
                        i++
                    }
                    mCallbacks.finishBroadcast()

                    // Repeat every 1 second.
                    sendMessageDelayed(obtainMessage(REPORT_MSG), (1 * 1000).toLong())
                }

                else -> super.handleMessage(msg)
            }
        }
    }

    /**
     * Show a notification while this service is running.
     */
    private fun showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        val text = getText(R.string.remote_service_started)

        // The PendingIntent to launch our activity if the user selects this notification
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, Controller::class.java), PendingIntent.FLAG_IMMUTABLE)

        // Set the info for the views that show in the notification panel.
        val notification: Notification = Notification.Builder(this)
            .setSmallIcon(androidx.fragment.R.drawable.notification_template_icon_bg) // the status icon
            .setTicker(text) // the status text
            .setWhen(System.currentTimeMillis()) // the time stamp
            .setContentTitle("remote service label") // the label of the entry
            .setContentText(text) // the contents of the entry
            .setContentIntent(contentIntent) // The intent to send when the entry is clicked
            .build()

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        mNM!!.notify(R.string.remote_service_started, notification)
    }

    // END_INCLUDE(calling_a_service)
    // ----------------------------------------------------------------------


    companion object {
        private const val REPORT_MSG = 1
    }
}

