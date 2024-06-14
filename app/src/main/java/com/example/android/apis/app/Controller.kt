package com.example.android.apis.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.android.apis.R

/**
 *
 * @author: denghg
 * @date: 2024/6/14
 */


// ----------------------------------------------------------------------
/**
 *
 * Example of explicitly starting and stopping the remove service.
 * This demonstrates the implementation of a service that runs in a different
 * process than the rest of the application, which is explicitly started and stopped
 * as desired.
 *
 *
 * Note that this is implemented as an inner class only keep the sample
 * all together; typically this code would appear in some separate class.
 */
class Controller : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.remote_service_controller)

        // Watch for button clicks.
        var button = findViewById<View>(R.id.start) as Button
        button.setOnClickListener(mStartListener)
        button = findViewById<View>(R.id.stop) as Button
        button.setOnClickListener(mStopListener)
    }

    private val mStartListener =
        View.OnClickListener { // Make sure the service is started.  It will continue running
            // until someone calls stopService().
            // We use an action code here, instead of explictly supplying
            // the component name, so that other packages can replace
            // the service.
            startService(Intent(this@Controller, RemoteService::class.java))
        }
    private val mStopListener =
        View.OnClickListener { // Cancel a previous call to startService().  Note that the
            // service will not actually stop at this point if there are
            // still bound clients.
            stopService(Intent(this@Controller, RemoteService::class.java))
        }
}