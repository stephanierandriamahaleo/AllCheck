package com.example.check_all.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.check_all.models.Badge;
import com.example.check_all.models.CoupeFile;
import com.example.check_all.models.Ticket;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerException;

import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class DisplayInformationService {
    public static void displayTicketPopup(Context context,
                                    int toneBeep,
                                    String popupColor,
                                    View layout,
                                    TextView eventTextView,
                                    TextView nameTextView,
                                    TextView phoneTextView,
                                    TextView placeTextView,
                                    TextView sectionTextView,
                                    TextView checkDateTextView,
                                    TextView prePrintCheckDateView,
                                    Ticket ticket,
                                    ZXingScannerView scannerView,
                                    EditText netumInput,
                                    Scanner scanner,
                                    String action) {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGenerator.startTone(toneBeep, 1000);
        layout.setBackgroundColor(Color.parseColor(popupColor));
        eventTextView.setText(ticket.getEvenement());
        nameTextView.setText(ticket.getNom());
        phoneTextView.setText(ticket.getTelephone());
        placeTextView.setText(ticket.getPlace());
        sectionTextView.setText(ticket.getSection());
        if(checkDateTextView != null) {
            checkDateTextView.setText(ticket.getCheckDate());
        }
        if(prePrintCheckDateView != null) {
            prePrintCheckDateView.setText(ticket.getPrePrintCheckDate());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", null);
        builder.setView(layout);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (scannerView != null) {
                            startCamera(scannerView, context);
                        } else if (netumInput != null) {
                            netumInput.setEnabled(true);
                        } else if (scanner != null) {
                            try {
                                scanner.enable();
                            } catch (ScannerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                }, 10000, 1);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor(popupColor));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timer.cancel();
                        if (scannerView != null) {
                            startCamera(scannerView, context);
                        } else if (netumInput != null) {
                            netumInput.setEnabled(true);
                        } else if (scanner != null) {
                            try {
                                scanner.enable();
                            } catch (ScannerException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.cancel();
                    }
                });
            }
        });
        dialog.show();
    }

    public static void displayBadgePopup(Context context,
                                          int toneBeep,
                                          String popupColor,
                                          View layout,
                                          TextView eventTextView,
                                          TextView nameTextView,
                                          TextView phoneTextView,
                                          TextView emailTextView,
                                          TextView checkDateTextView,
                                          TextView porteTextView,
                                          TextView niveauTextView,
                                          Badge badge,
                                          ZXingScannerView scannerView,
                                         EditText netumInput,
                                          Scanner scanner,
                                          String action) {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGenerator.startTone(toneBeep, 1000);
        layout.setBackgroundColor(Color.parseColor(popupColor));
        eventTextView.setText(badge.getEvenement());
        nameTextView.setText(badge.getNom());
        phoneTextView.setText(badge.getTelephone());
        porteTextView.setText(badge.getPorte());
        emailTextView.setText(badge.getEmail());
        niveauTextView.setText(badge.getNiveau());
        if(checkDateTextView != null) {
            checkDateTextView.setText(badge.getCheckDate());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", null);
        builder.setView(layout);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (scannerView != null) {
                            startCamera(scannerView, context);
                        } else if (netumInput != null) {
                            netumInput.setEnabled(true);
                        } else if (scanner != null) {
                            try {
                                scanner.enable();
                            } catch (ScannerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                }, 10000, 1);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor(popupColor));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timer.cancel();
                        if (scannerView != null) {
                            startCamera(scannerView, context);
                        } else if (netumInput != null) {
                            netumInput.setEnabled(true);
                        } else if (scanner != null) {
                            try {
                                scanner.enable();
                            } catch (ScannerException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.cancel();
                    }
                });
            }
        });
        dialog.show();
    }

    public static void displayCoupeFilePopup(Context context,
                                         int toneBeep,
                                         String popupColor,
                                         View layout,
                                         TextView eventTextView,
                                         TextView nameTextView,
                                         TextView phoneTextView,
                                         TextView vehicleNumberTextView,
                                         TextView checkDateTextView,
                                         TextView porteTextView,
                                         CoupeFile coupeFile,
                                         ZXingScannerView scannerView,
                                         EditText netumInput,
                                         Scanner scanner) {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGenerator.startTone(toneBeep, 1000);
        layout.setBackgroundColor(Color.parseColor(popupColor));
        eventTextView.setText(coupeFile.getEvenement());
        nameTextView.setText(coupeFile.getNom());
        phoneTextView.setText(coupeFile.getPhone());
        porteTextView.setText(coupeFile.getPorte());
        vehicleNumberTextView.setText(coupeFile.getNumeroVehicle());
        if(checkDateTextView != null) {
            checkDateTextView.setText(coupeFile.getCheckDate());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", null);
        builder.setView(layout);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (scannerView != null) {
                            startCamera(scannerView, context);
                        } else if (netumInput != null) {
                            netumInput.setEnabled(true);
                        } else if (scanner != null) {
                            try {
                                scanner.enable();
                            } catch (ScannerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                }, 10000, 1);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor(popupColor));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timer.cancel();
                        if (scannerView != null) {
                            startCamera(scannerView, context);
                        } else if (netumInput != null) {
                            netumInput.setEnabled(true);
                        } else if (scanner != null) {
                            try {
                                scanner.enable();
                            } catch (ScannerException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.cancel();
                    }
                });
            }
        });
        dialog.show();
    }

    private static void startCamera(ZXingScannerView scannerView, Context context) {
        final AudioManager mode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mode.setStreamVolume(AudioManager.STREAM_MUSIC,  mode.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        scannerView.startCamera();
    }
}
