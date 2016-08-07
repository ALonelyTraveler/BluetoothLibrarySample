package com.bandou.bluetooth.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.bdkj.ble.util.BluetoothUtils;

/**
 * @ClassName: MenuActivity
 * @Description: say something
 * @author: chenwei
 * @version: V1.0
 * @Date: 16/8/7 上午10:55
 */
public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViewById(R.id.btnClassic).setOnClickListener(this);
        findViewById(R.id.btnBle).setOnClickListener(this);
        if (!BluetoothUtils.isEnableBT()) {
            boolean enable = BluetoothUtils.autoEnableBluetooth();
            if (!enable) {
                Toast.makeText(this, "蓝牙无法启动", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "蓝牙正在启动", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClassic: {
                Intent intent = new Intent(this, ClassicActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btnBle: {
                Intent intent = new Intent(this, BLEActivity.class);
                startActivity(intent);
            }
            break;

        }
    }
}
