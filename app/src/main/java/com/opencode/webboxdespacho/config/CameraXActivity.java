package com.opencode.webboxdespacho.config;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.media.Image;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.opencode.webboxdespacho.MainActivity;
import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.fragments.PedidosFragment;
import com.opencode.webboxdespacho.sqlite.data.ViajesData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraXActivity extends AppCompatActivity {

    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS=1001; // , rotation=0
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private AlertDialog alertDialog;
    private PreviewView previewView;
    private Button btnCaptureImage, btnBackNav;
    private OrientationEventListener myOrientationEventListener;
    private ProgressDialog progressDialog;
    private TextView txtOrientation, viewShootCount;
    private CameraSelector cameraSelector;
    private ImageAnalysis imageAnalysis;
    private ImageCapture imageCapture;
    private Preview preview;
    private Camera camera;
    private int rotation = 0, fotos=0;
    private Bundle extras;
    private SessionDatos sessionDatos;
    private ViajesData viajesData;
    private String numpedido;
    private Handler handler = new Handler();
    private Runnable runnable;

    CameraControl cameraControl;
    CameraInfo cameraInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_xactivity);
        getSupportActionBar().hide();
        viajesData = new ViajesData(CameraXActivity.this);
        sessionDatos = new SessionDatos(CameraXActivity.this);
        progressDialog = new ProgressDialog(CameraXActivity.this);
        previewView = findViewById(R.id.previewView);
        btnCaptureImage = findViewById(R.id.btn_captura_foto);
        viewShootCount = findViewById(R.id.view_shoot_count);
        btnBackNav = findViewById(R.id.btn_back_nav_cam);
        btnBackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizaCamara();
            }
        });

        alertDialog = new AlertDialog.Builder(CameraXActivity.this).create();
        startCamera();
        /***/
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        numpedido = bundle.getString("NUMPEDIDO");
        //
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                viewShootCount.setText(String.valueOf(fotos));
            }
        };
        handler.postDelayed(runnable, 0);

    }

    private void startCamera(){
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(CameraXActivity.this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(CameraXActivity.this));
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private Bitmap imageToBitmap(Image image){
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        return bitmap;
    }

    @SuppressLint("RestrictedApi")
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider){
        //
        //extras = getIntent().getExtras();
        btnCaptureImage.setVisibility(View.VISIBLE);
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        if(hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)){
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        imageAnalysis = new ImageAnalysis.Builder().build();
        preview = new Preview.Builder().build();
        imageCapture = new ImageCapture.Builder()
                .setMaxResolution(new Size(800, 600))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        //int rotation = 0;
        OrientationEventListener orientationEventListener = new OrientationEventListener(CameraXActivity.this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                // Monitors orientation values to determine the target rotation value
                //int rotation = 0;
                if (orientation >= 45 && orientation < 135) {
                    //rotation = Surface.ROTATION_270;
                    rotation = 180;
                } else if (orientation >= 135 && orientation < 270) {
                    //rotation = Surface.ROTATION_180; //posicion izq
                    rotation = 360;
                } else if (orientation >= 225 && orientation < 370) {
                    //rotation = Surface.ROTATION_90;
                    rotation = 90;
                } else {
                    //rotation = Surface.ROTATION_0;
                    rotation = 0;
                }
            }
        };
        orientationEventListener.enable();
        camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);
        preview.setSurfaceProvider(previewView.createSurfaceProvider());
        /** CAPTURA IMAGEN CAMARA TRASERA*/
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File folder= new File(getFilesDir() + "/fotos");
                if(!folder.exists())
                    folder.mkdirs();
                String nomFoto = UUID.randomUUID().toString()+".jpg";
                final File file2 = new File (folder+"/"+nomFoto);
                imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        shootSound();
                        @SuppressLint("UnsafeOptInUsageError")
                        Bitmap imageBitmap = rotateImage(imageToBitmap(Objects.requireNonNull(image.getImage())), rotation);
                        try {
                            OutputStream fos = new FileOutputStream(file2);
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            Objects.requireNonNull(fos).close();
                            viajesData.insertRutaFoto(numpedido, file2.toString());
                            fotos++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        super.onCaptureSuccess(image);
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finalizaCamara();
    }

    void finalizaCamara(){
        if(fotos > 0){
            Long date=System.currentTimeMillis();
            SimpleDateFormat dateF =new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeF =new SimpleDateFormat("HH:mm", Locale.getDefault());
            String fechaStr = dateF.format(date);
            String horaStr = timeF.format(date);
            try {
                viajesData.updatePedidoEntrega(numpedido, fechaStr, horaStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(CameraXActivity.this, MainActivity.class);
            intent.putExtra("RET_CAM", "1");
            startActivity(intent);
            handler.removeCallbacks(runnable);
        }else{
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setTitle("Alerta");
            alertDialog.setMessage("Debe tomar al menos una (1) foto para salir..");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    public void shootSound() {
        MediaActionSound sound = new MediaActionSound();
        sound.play(MediaActionSound.SHUTTER_CLICK);
    }
}