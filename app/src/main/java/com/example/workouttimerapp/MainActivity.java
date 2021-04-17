package com.example.workouttimerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Chronometer chronometer;
    TextView textViewLastTime;
    EditText editTextWorkType;

    // 持久化
    String WORK_TYPE = "WORK_TYPE";
    String SPENT_TIME = "SPENT_TIME";
    String STATE = "STATE";
    String LAST_INFO = "LAST_INFO";
    String RECORD_TIME = "RECORD_TIME";
    String CURRENT_BASE = "CURRENT_BASE";
    String workType;
    String spentTime;
    String state;
    String last_info;
    long recordTime;
    long currentBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workType = "Noting";
        spentTime = "00:00";
        state = "OFF";// ON | PAUSE | OFF
        recordTime = 0;

        this.initView(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WORK_TYPE, workType);
        outState.putString(SPENT_TIME, spentTime);
        outState.putString(LAST_INFO, last_info);
        outState.putString(STATE, state);
        outState.putLong(RECORD_TIME, recordTime);

        // 旋转时刻的 Base
        currentBase = chronometer.getBase();
        outState.putLong(CURRENT_BASE, currentBase);
    }

    public void initView(Bundle savedInstanceState){
        textViewLastTime = findViewById(R.id.textViewLastTime);
        editTextWorkType = findViewById(R.id.editTextWorkType);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");

        if(savedInstanceState!=null){
            workType = savedInstanceState.getString(WORK_TYPE);
            spentTime = savedInstanceState.getString(SPENT_TIME);
            state = savedInstanceState.getString(STATE);
            last_info = savedInstanceState.getString(LAST_INFO);
            recordTime = savedInstanceState.getLong(RECORD_TIME);
            currentBase = savedInstanceState.getLong(CURRENT_BASE);

            switch (state){
                case "OFF":
                    break;
                case "ON":
                    chronometer.setBase(currentBase);
                    chronometer.start();
                    break;
                case "PAUSE":
                    chronometer.setBase(SystemClock.elapsedRealtime() - recordTime );
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + state);
            }
        }

        last_info = getString(R.string.last_info, spentTime, workType);
        textViewLastTime.setText(last_info);
    }

    public void onClickButton(View view) {
        switch (view.getId()){
            case R.id.imageButtonPlay:
                // * base 即计时器的起始点，
                // 新的 base = 当前真实时刻 - 已记录的时间
                chronometer.setBase(SystemClock.elapsedRealtime()-recordTime);
                chronometer.start();

                state = "ON";
                break;
            case R.id.imageButtonPause:
                chronometer.stop();
                // 已记录时长 = 暂停时刻 - base
                if(state == "ON"){
                    recordTime = SystemClock.elapsedRealtime()-chronometer.getBase();
                }

                state = "PAUSE";
                break;
            case R.id.imageButtonStop:
                // 获取 workType, 清除workType输入框, 修改记录行
                workType = editTextWorkType.getText().toString();
                editTextWorkType.setText("");
                spentTime = chronometer.getText().toString();
                last_info = getString(R.string.last_info, spentTime, workType);
                textViewLastTime.setText(last_info);

                // 重置计时器
                recordTime = 0;
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());

                state = "OFF";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }

    }
}