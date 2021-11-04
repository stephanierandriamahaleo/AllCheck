package com.example.check_all.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ZebraActivity extends AppCompatActivity implements EMDKManager.EMDKListener, com.symbol.emdk.barcode.Scanner.DataListener, com.symbol.emdk.barcode.Scanner.StatusListener{

    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;
    private TextView statusTextView = null;
    private SharedPreferences sharedPreferences;
    boolean isEntrance = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zebra);

        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);
        statusTextView = findViewById(R.id.textViewStatusZebra);

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);
        if(results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            statusTextView.setText("Erreur EMDK");
        }
    }

    private void initializeScanner() throws ScannerException {
        if(scanner == null){
            barcodeManager = (BarcodeManager) this.emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            scanner.addDataListener(this);
            scanner.addStatusListener(this);
            scanner.triggerType = com.symbol.emdk.barcode.Scanner.TriggerType.HARD;
            scanner.enable();
            scanner.read();
        }
    }

    private class AsyncDataUpdate extends
            AsyncTask<ScanDataCollection, Void, String> {

        @Override
        protected String doInBackground(ScanDataCollection... params) {
            String statusStr = "";
            ScanDataCollection scanDataCollection = params[0];
            if (scanDataCollection != null && scanDataCollection.getResult() == ScannerResults.SUCCESS) {
                ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
                for (ScanDataCollection.ScanData data : scanData) {
                    String barcodeDate = data.getData();
                    statusStr = barcodeDate ;
                }
            }
            return statusStr;
        }

        @SuppressLint("WrongConstant")
        @Override
        protected void onPostExecute(String data) {
            try {
                scanner.disable();
            } catch (ScannerException e) {
                e.printStackTrace();
            }
            String request = "api/event/billet/check";
            sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);
            JsonPlaceholderApi jsonPlaceHolderApi = Api.getApi(sharedPreferences.getString("url", "http://192.168.176.114:8000/")).create(JsonPlaceholderApi.class);
            QRRequest qrRequest;
            if (!isBadge()) {
                qrRequest= new QRRequest(data, sharedPreferences.getInt("idUser", 0), sharedPreferences.getString("action", ""));
            } else {
                qrRequest= new QRRequest(data, sharedPreferences.getInt("idUser", 0), sharedPreferences.getString("action", ""), isEntrance);
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
                    View vehicleView = layout.findViewById(R.id.vehicle_number_view);
                    View view = layout.findViewById(R.id.check_date_layout);
                    View prePrintView = layout.findViewById(R.id.pre_print_check_date_layout);
                    View placeView = layout.findViewById(R.id.place_layout);
                    View sectionView = layout.findViewById(R.id.section_layout);
                    View porteView = layout.findViewById(R.id.porte_layout);
                    View niveauView = layout.findViewById(R.id.niveau_layout);
                    View emailView = layout.findViewById(R.id.email_layout);
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
                                    emailView.setVisibility(View.GONE);
                                    porteView.setVisibility(View.GONE);
                                    niveauView.setVisibility(View.GONE);
                                    Ticket ticket = Service.getTicket(body);
                                    DisplayInformationService.displayTicketPopup(
                                            ZebraActivity.this,
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
                                            null,
                                            null,
                                            scanner,
                                            sharedPreferences.getString("action", "")
                                    );
                                } else if (isBadge()) {
                                    placeView.setVisibility(View.GONE);
                                    sectionView.setVisibility(View.GONE);
                                    Badge badge = Service.getBadge(body);
                                    DisplayInformationService.displayBadgePopup(
                                            ZebraActivity.this,
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
                                            null,
                                            null,
                                            scanner,
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
                                            ZebraActivity.this,
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
                                            null,
                                            null,
                                            scanner
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
                                            ZebraActivity.this,
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
                                            null,
                                            null,
                                            scanner,
                                            sharedPreferences.getString("action", "")
                                    );
                                } else if (isBadge()) {
                                    placeView.setVisibility(View.GONE);
                                    sectionView.setVisibility(View.GONE);
                                    Badge badge = Service.getBadge(body);
                                    dateLabel.setText(isEntrance ? "Entrée le : " : "Sortie le: ");
                                    DisplayInformationService.displayBadgePopup(
                                            ZebraActivity.this,
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
                                            null,
                                            null,
                                            scanner,
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
                                            ZebraActivity.this,
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
                                            null,
                                            null,
                                            scanner
                                    );
                                }
                            } catch (IOException e) {
                                try {
                                    scanner.enable();
                                } catch (ScannerException scannerException) {
                                    scannerException.printStackTrace();
                                }
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
                            AlertDialog dialog = new AlertDialog.Builder(ZebraActivity.this, R.style.MyDialogTheme)
                                    .setTitle("")
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            mTimer.cancel();
                                            try {
                                                scanner.enable();
                                            } catch (ScannerException e) {
                                                e.printStackTrace();
                                            }
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
                    try {
                        scanner.enable();
                    } catch (ScannerException e) {
                        e.printStackTrace();
                    }
                }
            });
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

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class AsyncStatusUpdate extends AsyncTask<StatusData, Void, String> {

        @Override
        protected String doInBackground(StatusData... params) {
            String statusStr = "";
            StatusData statusData = params[0];
            StatusData.ScannerStates state = statusData.getState();
            switch (state) {
                case IDLE:
                    try {
                        scanner.read();
                    }
                    catch (ScannerException e) {e.printStackTrace();}
                    statusStr = "The scanner enabled and its idle";
                    break;
                case SCANNING:
                    final AudioManager mode = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    mode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    mode.setStreamVolume(AudioManager.STREAM_MUSIC,  mode.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                    statusStr = "Scanning............";
                    break;
                case WAITING:
                    statusStr = "Waiting for Trigger Press..";
                    break;
                case DISABLED:
                    statusStr = "Scanner is not enabled";
                    break;
                case ERROR:
                    break;
                default:
                    break;
            }
            return statusStr;
        }

        @Override
        protected void onPostExecute(String result) {
            statusTextView.setText(result);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
        try {
            initializeScanner();
            Toast.makeText(ZebraActivity.this, "Appuyer sur le button pour demarrer le scan", Toast.LENGTH_SHORT).show();
        }catch (ScannerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClosed() {

    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        new AsyncDataUpdate().execute(scanDataCollection);
    }

    @Override
    public void onStatus(StatusData statusData) {
        new AsyncStatusUpdate().execute(statusData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (scanner != null) {
                scanner.removeDataListener(this);
                scanner.removeStatusListener(this);
                scanner.disable();
                scanner = null;
            }
        } catch (ScannerException e) {
            e.printStackTrace();
        }
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
                        Toast.makeText(ZebraActivity.this, "MODE ENTREE", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ZebraActivity.this, "MODE SORTIE", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        }
        return false;
    }
}