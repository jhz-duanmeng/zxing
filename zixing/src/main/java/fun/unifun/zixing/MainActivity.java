package fun.unifun.zixing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fun.unifun.zixing.android.CaptureActivity;
import fun.unifun.zixing.bean.ZxingConfig;
import fun.unifun.zixing.common.Constant;
import fun.unifun.zixing.encode.CodeCreator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button scanBtn;
    private TextView result;
    private EditText contentEt;
    private Button encodeBtn;
    private ImageView contentIv;
    private int REQUEST_CODE_SCAN = 111;
    /**
     * 生成带logo的二维码
     */
    private Button encodeBtnWithLogo;
    private ImageView contentIvWithLogo;
    private String contentEtString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        initView();
    }

//    /**
//     * 申请相机权限
//     *
//     * @param context
//     * @param photoFromCamera  拍照保存图片路径
//     *
//     *
//     * @see {https://github.com/yanzhenjie/AndPermission}
//     * */
//
//    public static void requestCameraPermission(final Context context, final String photoFromCamera){
//        //API >=23
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//
//            AndPermission.with(context)
//                    .requestCode(PERMISSION_MEDIA_REQUEST_CODE)
//                    .permission(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
//                    .rationale(new RationaleListener() {
//                        @Override
//                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
//                            // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
//                            AndPermission.rationaleDialog(context, rationale).show();
//                        }
//                    })
//                    .callback(new PermissionListener() {
//                        @Override
//                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
//                            // 权限申请成功回调。
//                            if(requestCode == PERMISSION_MEDIA_REQUEST_CODE) {
//                                UIRouter.JumpToCameraActivity(context,photoFromCamera);
//                            }
//                        }
//
//                        @Override
//                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
//                            // 权限申请失败回调。
//                            if(requestCode == PERMISSION_MEDIA_REQUEST_CODE) {
//                                ToastView.showToast(context,"拒绝授权");
//                            }
//                        }
//                    })
//                    .start();
//        }
//    }


    private void initView() {
        /*扫描按钮*/
        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);
        /*扫描结果*/
        result = findViewById(R.id.result);

        /*要生成二维码的输入框*/
        contentEt = findViewById(R.id.contentEt);
        /*生成按钮*/
        encodeBtn = findViewById(R.id.encodeBtn);
        encodeBtn.setOnClickListener(this);
        /*生成的图片*/
        contentIv = findViewById(R.id.contentIv);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        result = (TextView) findViewById(R.id.result);
        scanBtn = (Button) findViewById(R.id.scanBtn);
        contentEt = (EditText) findViewById(R.id.contentEt);
        encodeBtnWithLogo = (Button) findViewById(R.id.encodeBtnWithLogo);
        encodeBtnWithLogo.setOnClickListener(this);
        contentIvWithLogo = (ImageView) findViewById(R.id.contentIvWithLogo);
        encodeBtn = (Button) findViewById(R.id.encodeBtn);
        contentIv = (ImageView) findViewById(R.id.contentIv);
    }

    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 1);

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {


        Bitmap bitmap = null;
        switch (v.getId()) {
            case R.id.scanBtn:
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                /*ZxingConfig是配置类
                 *可以设置是否显示底部布局，闪光灯，相册，
                 * 是否播放提示音  震动
                 * 设置扫描框颜色等
                 * 也可以不传这个参数
                 * */
                ZxingConfig config = new ZxingConfig();
                // config.setPlayBeep(false);//是否播放扫描声音 默认为true
                //  config.setShake(false);//是否震动  默认为true
                // config.setDecodeBarCode(false);//是否扫描条形码 默认为true
//                                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
//                                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
//                                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
//                AndPermission.with(this)
//                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
//                        .onGranted(new Action() {
//                            @Override
//                            public void onAction(List<String> permissions) {
//
//                            }
//                        })
//                        .onDenied(new Action() {
//                            @Override
//                            public void onAction(List<String> permissions) {
//                                Uri packageURI = Uri.parse("package:" + getPackageName());
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                                startActivity(intent);
//
//                                Toast.makeText(MainActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
//                            }
//                        }).start();
                break;
            case R.id.encodeBtn:
                contentEtString = contentEt.getText().toString().trim();
                if (TextUtils.isEmpty(contentEtString)) {
                    Toast.makeText(this, "请输入要生成二维码图片的字符串", Toast.LENGTH_SHORT).show();
                    return;
                }

                bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, null);
                if (bitmap != null) {
                    contentIv.setImageBitmap(bitmap);
                }

                break;

            case R.id.encodeBtnWithLogo:

                contentEtString = contentEt.getText().toString().trim();
                if (TextUtils.isEmpty(contentEtString)) {
                    Toast.makeText(this, "请输入要生成二维码图片的字符串", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, logo);

                if (bitmap != null) {
                    contentIvWithLogo.setImageBitmap(bitmap);
                }

                break;

            default:
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                result.setText("扫描结果为：" + content);
            }
        }
    }

}
