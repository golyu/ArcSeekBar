package com.lvzhongyi.arcseekbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lvzhongyi.arclib.ArcSeekbarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tv_electricity)
    TextView tvElectricity;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.ll_k_switch)
    LinearLayout llKSwitch;
    @BindView(R.id.btn_intensity_sub)
    ImageButton btnIntensitySub;
    @BindView(R.id.asvIntensity)
    ArcSeekbarView asvIntensity;
    @BindView(R.id.btn_intensity_add)
    ImageButton btnIntensityAdd;
    @BindView(R.id.ll_intensity)
    LinearLayout llIntensity;
    @BindView(R.id.btn_time_sub)
    ImageButton btnTimeSub;
    @BindView(R.id.asvTime)
    ArcSeekbarView asvTime;
    @BindView(R.id.btn_time_add)
    ImageButton btnTimeAdd;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.switch_phone)
    TextView switchPhone;
    @BindView(R.id.switch_diu)
    TextView switchDiu;
    @BindView(R.id.ib_menu)
    ImageButton ibMenu;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    private boolean b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btnIntensityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asvIntensity.addCurrentLevel();
            }
        });
        btnIntensitySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asvIntensity.subCurrentLevel();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asvIntensity.onOrOff(b);
                if (b) b = false;
                else b = true;
            }
        });
    }


//    @OnClick({R.id.btn_intensity_sub, R.id.btn_intensity_add})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn_intensity_sub:
//                asvIntensity.subCurrentLevel();
//                break;
//            case R.id.btn_intensity_add:
//                asvIntensity.addCurrentLevel();
//                break;
//        }
//    }
}
