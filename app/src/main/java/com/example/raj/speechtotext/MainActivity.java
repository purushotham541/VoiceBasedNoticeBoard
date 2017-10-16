package com.example.raj.speechtotext;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private EditText et;
    private ImageButton speak;
    private Button postmessage;
    private final static int SEND_SMS_PERMISSION_REQUEST_CODE=111;
    Button sms;
    String text="";

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.voiceInput);
        postmessage=(Button)findViewById(R.id.b1);
        sms=(Button)findViewById(R.id.b2);
        speak = (ImageButton) findViewById(R.id.btnSpeak);
        speak.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                startVoiceInput();
            }
        });
        postmessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                text=text+ et.getText().toString();
                if(text.isEmpty())
                {
                    et.setError("Error");
                }
                else
                {
                 Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
                    PackageManager pm=getPackageManager();
                    try
                    {

                        Intent waIntent = new Intent(Intent.ACTION_SEND);
                        waIntent.setType("text/plain");
                        String msg=text;

                        PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);

                        waIntent.setPackage("com.whatsapp");

                        waIntent.putExtra(Intent.EXTRA_TEXT, text);
                        startActivity(Intent.createChooser(waIntent, "Share with"));

                    }
                    catch (PackageManager.NameNotFoundException e)
                    {
                        Toast.makeText(MainActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
        if(checkPermission(Manifest.permission.SEND_SMS))
        {
            sms.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    text=text+ et.getText().toString();
                    if(text.isEmpty())
                    {
                        et.setError("Text required");
                    }
                    else
                    {
                        //String phone_no=et1.getText().toString();
                        //  String message=et2.getText().toString();
                      /*  SmsManager senssms = SmsManager.getDefault();
                        senssms.sendTextMessage("8125251050", null, text, null, null);*/
                        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View v1 = li.inflate(R.layout.custumdailouge, null, false);
                        final EditText mobileno = (EditText) v1.findViewById(R.id.etmobile);
                        Button bsend = (Button) v1.findViewById(R.id.bsend);


                        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                        adb.setView(v1);
                        final AlertDialog ad = adb.create();

                        ad.show();
                        bsend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                String mobilenum = mobileno.getText().toString();

                                SmsManager senssms = SmsManager.getDefault();
                                senssms.sendTextMessage(mobilenum, null, text, null, null);

                                Toast.makeText(MainActivity.this, "Your message has been sent successfully", Toast.LENGTH_LONG).show();

                                finish();


                            }


                        });
                    }



                }
            });

        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_REQUEST_CODE);

        }
    }


    private void startVoiceInput()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try
        {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException a)
        {
            Toast.makeText(this,"Speech not recognised",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQ_CODE_SPEECH_INPUT:
            {
                if (resultCode == RESULT_OK && null != data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    et.setText(result.get(0));
                }
                break;
            }

        }
    }
    private boolean checkPermission(String permission)
    {
        int checkPermission= ContextCompat.checkSelfPermission(this,permission);
        return checkPermission== PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case SEND_SMS_PERMISSION_REQUEST_CODE:
            {
                if(grantResults.length>0&& (grantResults[0]==PackageManager.PERMISSION_GRANTED))
                {

                }
                break;
            }
        }
    }


}
