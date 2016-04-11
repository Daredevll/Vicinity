package com.vicinity.vicinity.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.Constants;
import com.vicinity.vicinity.utilities.ServerCommManager;

public class RegBusinessActivity extends AppCompatActivity {

    Button buttonSend, buttonSendCode;
    CheckBox agree;
    TextView preSendText, postSendText;
    EditText codeForm;




    String placeId;
    String accId;
    String placeLocPhone;
    String placeName;
    String placeAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_business);

        buttonSend = (Button) findViewById(R.id.business_reg_send_button);
        buttonSendCode = (Button) findViewById(R.id.business_reg_send_code_button);
        agree = (CheckBox) findViewById(R.id.business_reg_agree_check);

        preSendText = (TextView) findViewById(R.id.business_reg_terms);
        postSendText = (TextView) findViewById(R.id.business_reg_after_instructions);
        codeForm = (EditText) findViewById(R.id.business_reg_input_code);

        if (getIntent().getBooleanExtra(Constants.REGISTER_BUSINESS_ACTIVITY_VERIFY_BOOLEAN_EXTRA, false)){
            accId = getIntent().getStringExtra("ACC_ID");
            setVisibilities(true);
        }
        else {
            placeId = getIntent().getStringExtra("PLACE_ID");
            accId = getIntent().getStringExtra("ACC_ID");
            placeLocPhone = getIntent().getStringExtra("PLACE_LOC_PHONE");
            placeName = getIntent().getStringExtra("PLACE_NAME");
            placeAddress = getIntent().getStringExtra("PLACE_ADDRESS");
            setVisibilities(false);
        }

        buttonSend.setVisibility(View.INVISIBLE);
        agree.setChecked(false);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerCommManager.getInstance().onPostRegisterBusiness(getApplicationContext(), accId, placeId, placeLocPhone, placeName, placeAddress);
                Log.e("Request", "Place business Registration method invoked");
                setVisibilities(true);
            }
        });

        agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    buttonSend.setVisibility(View.INVISIBLE);
                }
            }
        });

        buttonSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerCommManager.getInstance().onSendGeneratedCode(getApplicationContext(), accId, codeForm.getText().toString());
                codeForm.setText("");
            }
        });

    }

    private void setVisibilities(boolean postSent){
        if (postSent){
            postSendText.setVisibility(View.VISIBLE);
            buttonSendCode.setVisibility(View.VISIBLE);
            codeForm.setVisibility(View.VISIBLE);

            preSendText.setVisibility(View.INVISIBLE);
            agree.setVisibility(View.INVISIBLE);
            buttonSend.setVisibility(View.INVISIBLE);
        }
        else {
            postSendText.setVisibility(View.INVISIBLE);
            buttonSendCode.setVisibility(View.INVISIBLE);
            codeForm.setVisibility(View.INVISIBLE);

            preSendText.setVisibility(View.VISIBLE);
            agree.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.VISIBLE);
        }
    }
}
