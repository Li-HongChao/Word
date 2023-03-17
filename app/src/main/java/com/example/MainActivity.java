package com.example;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entity.Word;
import com.example.unit.SavePic;
import com.example.web.PicGet;
import com.example.web.WordGet;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    // 要申请的权限
    private final String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private final static int GET_PICTURE = 1;
    private final static int GET_WORD = 2;

    private ImageView mainMore;
    private Button mainGetPic;
    private Button mainGetText;
    private TextView mainTime;
    private TextView mainDate;
    private RelativeLayout mainTextArea;
    private RelativeLayout mainBtArea;
    private RelativeLayout mainTop;
    private TextView mainContent;
    private TextView mainOrigin;
    private TextView content;
    private ImageView mainBg;
    private TextView loading;

    private boolean pageStatus;
    private Bitmap bitmap;
    private Word word;
    private DrawerLayout drawer_layout;
    private NavigationView nav_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initUI();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getUser();
    }

    @SuppressLint("CutPasteId")
    private void initUI() throws InterruptedException {
        mainBg = findViewById(R.id.Main_Bg);
        content = findViewById(R.id.Main_Content);
        mainMore = findViewById(R.id.Main_more);
        mainGetPic = findViewById(R.id.Main_GetPic);
        mainGetText = findViewById(R.id.Main_GetText);
        mainTime = findViewById(R.id.Main_Time);
        mainDate = findViewById(R.id.Main_Date);
        mainTextArea = findViewById(R.id.Main_TextArea);
        mainContent = findViewById(R.id.Main_Content);
        mainOrigin = findViewById(R.id.Main_Origin);
        mainBtArea = findViewById(R.id.Main_BtArea);
        loading = findViewById(R.id.Main_Loading);
        mainTop = findViewById(R.id.Main_Top);

        //设置侧滑栏的监听
        drawer_layout = findViewById(R.id.drawer_layout);
        drawer_layout.setOnClickListener(this);
        //nva菜单的Item点击事件钮监听
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);


        content.setOnClickListener(this);
        mainMore.setOnClickListener(this);
        mainGetPic.setOnClickListener(this);
        mainGetText.setOnClickListener(this);
        mainTime.setOnClickListener(this);
        mainDate.setOnClickListener(this);
        mainTextArea.setOnClickListener(this);
        mainContent.setOnClickListener(this);
        mainOrigin.setOnClickListener(this);
        mainBtArea.setOnClickListener(this);

        //设置顶部状态栏为透明
//        int height = getActionBar().getHeight();
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        //设置字体
        Typeface fontType = Typeface.createFromAsset(getAssets(), "font.ttf");
        content.setTypeface(fontType);

        //设置背景
        mainBtArea.setVisibility(View.INVISIBLE);
        mainTop.setVisibility(View.INVISIBLE);
        getData(GET_PICTURE);

        Log.d(TAG, "initUI: 已经执行");

        //设置文字长按效果
        content.setOnLongClickListener(view -> {
            Log.d(TAG, "onLongClick: 长按触发");
            Toast.makeText(MainActivity.this, "已复制！", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.Main_GetText:
                    getData(GET_WORD);
                    break;
                case R.id.Main_GetPic:
                    if (bitmap != null) {
                        boolean savaImage = SavePic.SavaImage(this, bitmap, (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)).toString() + "/文案图片");
                        if (savaImage) {
                            Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "保存失败，是否开启权限?", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "现在咩有图片哇", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.Main_more:
                    Log.d(TAG, "onClick: 对你没点错");
                    drawer_layout.openDrawer(GravityCompat.START);
                    break;
                default:
                    //判断是否隐藏文字控件
                    if (pageStatus) {
                        cartoon(mainTextArea, "translationY", 0, 10000f, 1000);
                        cartoon(mainBtArea, "alpha", 0, 1, 200);
                        pageStatus = false;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setText() {
        //设置日期
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate time = LocalDate.now();
            int year = time.getYear();
            int month = time.getMonth().getValue();
            int day = time.getDayOfMonth();
            int week = time.getDayOfWeek().getValue();
            LocalDateTime dateTime  = LocalDateTime.now();
            mainTime.setText(dateTime.getHour()+":"+dateTime.getMinute());
            mainDate.setText(year+"年"+month+"月"+day+"日  |  星期 "+week);
        }

        //设置文字
        cartoon(mainBtArea, "alpha", 1, 0, 200);
        content.setText(word.getContent());
        mainOrigin.setText("——\t\t" + word.getAuthor() + "\t《" + word.getOrigin() + "》");
        content.setText(word.getContent());
        pageStatus = true;
        mainTextArea.setVisibility(View.VISIBLE);
        Log.d(TAG, "setText: "+word.toString());
        cartoon(mainTextArea, "translationY", 700, 0f, 1000);
    }

    /**
     * 动画实现
     *
     * @param direction 动画效果（translationX,Y平移动画--alpha透明动画）
     * @param start     开始点
     * @param end       结束点
     * @param time      持续时间
     * @param view      动画对象
     */
    private void cartoon(View view, String direction, int start, float end, long time) {
        ObjectAnimator.ofFloat(view, direction, start, end).setDuration(time).start();
    }

    /**
     * 判断权限
     */
    private void getUser() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }
    }

    /**
     * 用户权限 申请 的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //如果没有获取权限，那么可以提示用户去设置界面--->应用权限开启权限
                    new AlertDialog.Builder(this)
                            .setTitle("存储权限不可用")
                            .setMessage("请在-应用设置-权限-中，允许应用使用存储权限来保存用户数据")
                            .setPositiveButton("立即开启", (dialog1, which) -> {
                                // 跳转到应用设置界面
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 123);
                            })
                            .setNegativeButton("取消", (dialog12, which) -> {
                            }).setCancelable(false).show();
                }
            }
        }
    }


    public Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    mainBtArea.setVisibility(View.VISIBLE);
                    mainTop.setVisibility(View.VISIBLE);
                    cartoon(loading, "alpha", 1, 0, 1000);
                    cartoon(mainBg, "alpha", 0, 1, 1000);
                    if (bitmap != null) {
                        mainBg.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(MainActivity.this, "网络请求错误！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    if (word != null) {
                        setText();
                    } else {
                        Toast.makeText(MainActivity.this, "网络请求错误！", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };

    public void getData(int getCode) {
        Log.d(TAG, "getData: 已经执行");
        new Thread() {
            @Override
            public void run() {
                try {
                    if (getCode == GET_PICTURE) {
                        bitmap = new PicGet().getPic();
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    } else if (getCode == GET_WORD) {
                        word = new WordGet().getData();
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    bitmap = null;
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.one:
                Toast.makeText(this, "点击有效", Toast.LENGTH_SHORT).show();
                break;
            case R.id.two:
                Toast.makeText(this, "点击有效", Toast.LENGTH_SHORT).show();
                break;
            case R.id.three:
                Toast.makeText(this, "点击有效", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }
}