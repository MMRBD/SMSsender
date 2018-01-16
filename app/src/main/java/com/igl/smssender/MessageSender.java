package com.igl.smssender;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;

public class MessageSender extends AppCompatActivity {

    TextView maxNumber, showProgress;
    ProgressBar progressBar;

    String message;
    Stack<String> contacts;

    String SENT = "SMS_SENT";

    int contactsLength;

    BroadcastReceiver sentReceiver;

    final Handler handler = new Handler();
    WarningMessage dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_sender);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);

        initializationAllXMLCompononet();

        message = getIntent().getExtras().getString("MESSAGE");

        contacts = MainActivity.contacts;
        MainActivity.clearContacts();

        contactsLength = contacts.size();
        maxNumber.setText(" / " + contactsLength);

        progressBar.setMax(contactsLength);

        buildDialog();


        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        sentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //increaseProgress();// inclease the SMS progress
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        increaseProgress();
                        //Toast.makeText(context, "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };


        //---when the SMS has been sent---
        registerReceiver(sentReceiver, new IntentFilter(SENT));

        final SmsManager sms = SmsManager.getDefault();
        //sms.sendTextMessage("01632594151",null,message,pendingIntent,null);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!contacts.isEmpty()) {

                    SystemClock.sleep(2000);
                    Log.e("GetNumber" + contacts.size(), contacts.peek());
                    sms.sendTextMessage(contacts.peek(), null, message, pendingIntent, null);
                    sentProgress();
                    contacts.pop();
                }
            }
        }).start();

    }


    @Override
    protected void onResume() {
        //---when the SMS has been sent---
        registerReceiver(sentReceiver, new IntentFilter(SENT));

        super.onResume();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(sentReceiver);

        super.onDestroy();
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    private void initializationAllXMLCompononet() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        maxNumber = (TextView) findViewById(R.id.maxNumber);
        showProgress = (TextView) findViewById(R.id.showProgress);
    }

    private void buildDialog() {
        dialog = new WarningMessage(this, "ALL SMS ARE", "Successfully Sent");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
    }


    public void sentProgress() {
        progressBar.setProgress(progressBar.getProgress() + 1);
        showProgress.setText(String.valueOf(progressBar.getProgress()));
    }

    public void increaseProgress() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                sentProgress();
                isDone();
            }
        });
    }

    public void isDone() {
        if (progressBar.getProgress() == contactsLength) {
            dialog.display();
        }
    }
}