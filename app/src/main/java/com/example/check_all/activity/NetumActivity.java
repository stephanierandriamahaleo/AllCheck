package com.example.check_all.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetumActivity extends AppCompatActivity {

    private EditText netumInput;
    private SharedPreferences sharedPreferences;
    boolean isEntrance = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netum);
        this.getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        this.registerReceiver(broadcastReceiver , filter);


        netumInput = findViewById(R.id.netumInput);
        netumInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( -1 != s.toString().indexOf("\n") ){
                    // doSendMsg();
                    netumInput.setEnabled(false);
                    checkTicket(s.toString());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        /*TextView text = new TextView(this);
        text.setText(getIntent().getStringExtra("name")+" ato @ netum ");
        RelativeLayout layout = findViewById(R.id.netumLayout);
        layout.addView(text);*/
    }

    public void checkTicket(String data) {
        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);
        data = data.replace("\n", "");
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
                netumInput.getText().clear();
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
                View emailView = layout.findViewById(R.id.email_layout);
                View vehicleView = layout.findViewById(R.id.vehicle_number_view);
                View niveauView = layout.findViewById(R.id.niveau_layout);
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
                                        NetumActivity.this,
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
                                        netumInput,
                                        null,
                                        sharedPreferences.getString("action", "")
                                );
                            } else if (isBadge()) {
                                placeView.setVisibility(View.GONE);
                                sectionView.setVisibility(View.GONE);
                                Badge badge = Service.getBadge(body);
                                DisplayInformationService.displayBadgePopup(
                                        NetumActivity.this,
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
                                        netumInput,
                                        null,
                                        sharedPreferences.getString("action", "")
                                );
                            }else {
                                niveauView.setVisibility(View.GONE);
                                porteView.setVisibility(View.GONE);
                                placeView.setVisibility(View.GONE);
                                sectionView.setVisibility(View.GONE);
                                emailView.setVisibility(View.GONE);
                                vehicleView.setVisibility(View.VISIBLE);
                                CoupeFile coupeFile = Service.getCoupeFile(body);
                                DisplayInformationService.displayCoupeFilePopup(
                                        NetumActivity.this,
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
                                        netumInput,
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
                                        NetumActivity.this,
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
                                        netumInput,
                                        null,
                                        sharedPreferences.getString("action", "")
                                );
                            } else if (isBadge()) {
                                placeView.setVisibility(View.GONE);
                                sectionView.setVisibility(View.GONE);
                                Badge badge = Service.getBadge(body);
                                dateLabel.setText(isEntrance ? "Entrée le : " : "Sortie le: ");
                                DisplayInformationService.displayBadgePopup(
                                        NetumActivity.this,
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
                                        netumInput,
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
                                NetumActivity.this,
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
                                netumInput,
                                null
                        );
                    }

                        } catch (IOException e) {
                            netumInput.setEnabled(true);
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
                        AlertDialog dialog = new AlertDialog.Builder(NetumActivity.this, R.style.MyDialogTheme)
                                .setTitle("")
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        mTimer.cancel();
                                        netumInput.setEnabled(true);
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
                netumInput.setEnabled(true);
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

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        BluetoothDevice device;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(NetumActivity.this, "Found", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Toast.makeText(NetumActivity.this, "ConnectedDevice", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(NetumActivity.this, "finish search", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                Toast.makeText(NetumActivity.this, "Disconnecting", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Toast.makeText(NetumActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        }
    };

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
                        Toast.makeText(NetumActivity.this, "MODE ENTREE", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NetumActivity.this, "MODE SORTIE", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        }
        return false;
    }
}
