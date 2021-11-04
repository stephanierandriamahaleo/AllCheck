package com.example.check_all.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.check_all.R;
import com.example.check_all.api.Api;
import com.example.check_all.api.JsonPlaceholderApi;
import com.example.check_all.constant.Constant;
import com.example.check_all.models.Badge;
import com.example.check_all.models.CoupeFile;
import com.example.check_all.models.QRRequest;
import com.example.check_all.models.Ticket;
import com.example.check_all.services.DisplayInformationService;
import com.example.check_all.services.Service;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import com.google.zxing.Result;
//import com.google.zxing.Result;

//import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CameraActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView ScannerView;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    private SharedPreferences sharedPreferences;
    boolean isEntrance = true;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);
        ScannerView = new ZXingScannerView(this);
        setContentView(this.ScannerView);

    }

    @Override
    public void handleResult(Result rawresult){
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);
        }
        ScannerView.stopCamera();

        JsonPlaceholderApi jsonPlaceHolderApi = Api.getApi(sharedPreferences.getString("url", "http://192.168.176.114:8000/")).create(JsonPlaceholderApi.class);
        QRRequest qrRequest;
        if (!isBadge()) {
            qrRequest= new QRRequest(rawresult.getText(), sharedPreferences.getInt("idUser", 0), sharedPreferences.getString("action", ""));
        } else {
            qrRequest= new QRRequest(rawresult.getText(), sharedPreferences.getInt("idUser", 0), sharedPreferences.getString("action", ""), isEntrance);
        }

        qrRequest.setEventId(getIntent().getIntExtra("idEvent",1));

        Call<ResponseBody> checkCall = jsonPlaceHolderApi.checkQR(qrRequest, sharedPreferences.getString("token", ""));

        checkCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                LayoutInflater inflater;
                View layout;
                String body;
                inflater = getLayoutInflater();
                layout = inflater.inflate(R.layout.result_popup, findViewById(R.id.result_popup));
                TextView evenement = layout.findViewById(R.id.evenement);
                TextView nom = layout.findViewById(R.id.nom);
                TextView telephone = layout.findViewById(R.id.telephone);
                TextView place = layout.findViewById(R.id.place);
                TextView section = layout.findViewById(R.id.section);
                View view = layout.findViewById(R.id.check_date_layout);
                View prePrintView = layout.findViewById(R.id.pre_print_check_date_layout);
                View placeView = layout.findViewById(R.id.place_layout);
                View sectionView = layout.findViewById(R.id.section_layout);
                View porteView = layout.findViewById(R.id.porte_layout);
                View niveauView = layout.findViewById(R.id.niveau_layout);
                View emailView = layout.findViewById(R.id.email_layout);
                View vehicleView = layout.findViewById(R.id.vehicle_number_view);
                TextView dateLabel = layout.findViewById(R.id.date_label);
                TextView checkDate = layout.findViewById(R.id.check_date);
                TextView prePrintCheckDate = layout.findViewById(R.id.pre_print_check_date);
                TextView email = layout.findViewById(R.id.email);
                TextView porte = layout.findViewById(R.id.porte);
                TextView niveau = layout.findViewById(R.id.niveau);
                TextView vehicleNumber = layout.findViewById(R.id.vehicle_number);

                switch (response.code()) {
                    case 200:
                        try {
                            view.setVisibility(View.GONE);
                            prePrintView.setVisibility(View.GONE);
                            body = response.body().string();
                            if (isTicket()) {
                                porteView.setVisibility(View.GONE);
                                niveauView.setVisibility(View.GONE);
                                emailView.setVisibility(View.GONE);
                                Ticket ticket = Service.getTicket(body);
                                DisplayInformationService.displayTicketPopup(
                                        CameraActivity.this,
                                        ToneGenerator.TONE_PROP_BEEP,
                                        "#007f00",
                                        layout,
                                        evenement,
                                        nom,
                                        telephone,
                                        place,
                                        section,
                                        null,
                                        null,
                                        ticket,
                                        ScannerView,
                                        null,
                                        null,
                                        sharedPreferences.getString("action", "")
                                );
                            } else if (isBadge()) {
                                placeView.setVisibility(View.GONE);
                                sectionView.setVisibility(View.GONE);
                                Badge badge = Service.getBadge(body);
                                DisplayInformationService.displayBadgePopup(
                                        CameraActivity.this,
                                        ToneGenerator.TONE_PROP_BEEP,
                                        "#007f00",
                                        layout,
                                        evenement,
                                        nom,
                                        telephone,
                                        email,
                                        null,
                                        porte,
                                        niveau,
                                        badge,
                                        ScannerView,
                                        null,
                                        null,
                                        sharedPreferences.getString("action", "")
                                );
                            } else {
                                niveauView.setVisibility(View.GONE);
                                porteView.setVisibility(View.GONE);
                                placeView.setVisibility(View.GONE);
                                sectionView.setVisibility(View.GONE);
                                emailView.setVisibility(View.GONE);
                                vehicleView.setVisibility(View.VISIBLE);
                                CoupeFile coupeFile = Service.getCoupeFile(body);
                                DisplayInformationService.displayCoupeFilePopup(
                                        CameraActivity.this,
                                        ToneGenerator.TONE_PROP_BEEP,
                                        "#007f00",
                                        layout,
                                        evenement,
                                        nom,
                                        telephone,
                                        vehicleNumber,
                                        null,
                                        porte,
                                        coupeFile,
                                        ScannerView,
                                        null,
                                        null
                                );
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 208:
                        try {
                            if (sharedPreferences.getString("action", "").compareTo("Vente Billet") == 0) {
                                prePrintView.setVisibility(View.VISIBLE);
                            } else {
                                view.setVisibility(View.VISIBLE);
                            }
                            body = response.body().string();
                            if (isTicket()) {
                                porteView.setVisibility(View.GONE);
                                niveauView.setVisibility(View.GONE);
                                emailView.setVisibility(View.GONE);
                                Ticket ticket = Service.getTicket(body);
                                DisplayInformationService.displayTicketPopup(
                                        CameraActivity.this,
                                        ToneGenerator.TONE_CDMA_ABBR_REORDER,
                                        "#D79334",
                                        layout,
                                        evenement,
                                        nom,
                                        telephone,
                                        place,
                                        section,
                                        checkDate,
                                        prePrintCheckDate,
                                        ticket,
                                        ScannerView,
                                        null,
                                        null,
                                        sharedPreferences.getString("action", "")
                                );
                            } else if (isBadge()) {
                                placeView.setVisibility(View.GONE);
                                sectionView.setVisibility(View.GONE);
                                Badge badge = Service.getBadge(body);
                                dateLabel.setText(isEntrance ? "Entrée le : " : "Sortie le: ");
                                DisplayInformationService.displayBadgePopup(
                                        CameraActivity.this,
                                        ToneGenerator.TONE_CDMA_ABBR_REORDER,
                                        "#D79334",
                                        layout,
                                        evenement,
                                        nom,
                                        telephone,
                                        email,
                                        checkDate,
                                        porte,
                                        niveau,
                                        badge,
                                        ScannerView,
                                        null,
                                        null,
                                        sharedPreferences.getString("action", "")
                                );
                            } else {
                                niveauView.setVisibility(View.GONE);
                                porteView.setVisibility(View.GONE);
                                placeView.setVisibility(View.GONE);
                                sectionView.setVisibility(View.GONE);
                                emailView.setVisibility(View.GONE);
                                vehicleView.setVisibility(View.VISIBLE);
                                CoupeFile coupeFile = Service.getCoupeFile(body);
                                DisplayInformationService.displayCoupeFilePopup(
                                        CameraActivity.this,
                                        ToneGenerator.TONE_CDMA_ABBR_REORDER,
                                        "#D79334",
                                        layout,
                                        evenement,
                                        nom,
                                        telephone,
                                        vehicleNumber,
                                        checkDate,
                                        porte,
                                        coupeFile,
                                        ScannerView,
                                        null,
                                        null
                                );
                            }

                        } catch (IOException e) {
                            StartCamera();
                            e.printStackTrace();
                        }

                        break;
                    case 500:
                        String html;
                        if (isTicket()) {
                            html = "<font color='#FFFFFF'>Billet invalide</font>";
                        } else if (isBadge()) {
                            html = "<font color='#FFFFFF'>Badge invalide</font>";
                        } else {
                            html = "<font color='#FFFFFF'>Coupe-file invalide</font>";
                        }
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                        toneGen1.startTone(ToneGenerator.TONE_SUP_ERROR,1000);
                        Timer mTimer = new Timer();
                        AlertDialog dialog = new AlertDialog.Builder(CameraActivity.this, R.style.MyDialogTheme)
                                .setTitle("")
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        mTimer.cancel();
                                        StartCamera();
                                    }
                                })
                                .setMessage(Html.fromHtml(html))
                                .show();

                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                dialog.cancel();
                            }
                        }, 10000, 1);

                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Failure", t.toString());
                StartCamera();
            }
        });

        ScannerView.resumeCameraPreview(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScannerView = new ZXingScannerView(this);
        ScannerView.setResultHandler(this);

        if (hasPermission(Manifest.permission.CAMERA)) {
            StartCamera();
        }

    }

    private void StartCamera() {
        final AudioManager mode = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mode.setStreamVolume(AudioManager.STREAM_MUSIC,  mode.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        ScannerView.startCamera();
    }

    private Boolean hasPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "User hasn't granted permission.");

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            ScannerView.startCamera();
            setContentView(ScannerView);
            Log.d("TAG", "User already granted permission.");
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1123:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ScannerView.startCamera();
                    setContentView(ScannerView);
                    Log.d("TAG", "Permission by user...");
                } else {
                    Log.d("TAG", "Permission denied by user...");
                }
                break;
        }
    }

    public boolean isTicket() {
        return sharedPreferences.getString("action", "").compareTo("Pre-Check Billet") == 0
                || sharedPreferences.getString("action", "").compareTo("Check Billet") == 0
                || sharedPreferences.getString("action", "").compareTo("Vente Billet") == 0
                || sharedPreferences.getString("action", "").compareTo("Check + vente") == 0;
    }

    public boolean isBadge() {
        return sharedPreferences.getString("action", "").compareTo("Check Badge") == 0
                || sharedPreferences.getString("action", "").compareTo("Pre-Check Badge") == 0;
    }

    public boolean isCoupeFile() {
        return sharedPreferences.getString("action", "").compareTo("Pre-Check Coupe file") == 0
                || sharedPreferences.getString("action", "").compareTo("Check Coupe file") == 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        if (getIntent().getStringExtra("action").compareTo("Check Badge") == 0) {
            getMenuInflater().inflate(R.menu.toggle_menu, menu);
            MenuItem itemswitch = menu.findItem(R.id.switch_action_bar);
            itemswitch.setActionView(R.layout.switch_button);

            final Switch sw = (Switch) menu.findItem(R.id.switch_action_bar).getActionView().findViewById(R.id.switch2);

            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isEntrance = isChecked;
                    if (isChecked) {
                        Toast.makeText(CameraActivity.this, "MODE ENTREE", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CameraActivity.this, "MODE SORTIE", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        }
        return false;
    }
}
