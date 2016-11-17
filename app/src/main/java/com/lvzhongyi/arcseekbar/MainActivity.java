package com.lvzhongyi.arcseekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lvzhongyi.arclib.ArcSeekbarView;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private ArcSeekbarView asvTime;
    private boolean b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        asvTime = (ArcSeekbarView) findViewById(R.id.asvTime);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (b) {
                    b = false;
                } else {
                    b = true;
                }
                asvTime.onOrOff(b);
            }
        });
    }
}
