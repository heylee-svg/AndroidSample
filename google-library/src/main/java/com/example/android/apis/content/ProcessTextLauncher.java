package com.example.android.apis.content;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.example.android.apis.R;

public class ProcessTextLauncher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_text_send);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}
