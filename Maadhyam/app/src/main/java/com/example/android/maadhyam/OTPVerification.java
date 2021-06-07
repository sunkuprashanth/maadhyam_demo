package com.example.android.maadhyam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {

    private String verificationid,code,otpCheck="verified";
    private FirebaseAuth mAuth;
    private Button next_button,back_button;
    private EditText otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        String mobile_no_extracted = "+91"+getIntent().getStringExtra("MOBILE_NO");
        EditText mobile_no = findViewById(R.id.mobile_no);
        otp = findViewById(R.id.otp);

        mobile_no.setHint("  "+mobile_no_extracted);
        mobile_no.setFocusable(false);

        mAuth = FirebaseAuth.getInstance();
        sendVerificationCode(mobile_no_extracted);

        next_button = findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp.getText().toString().equals(""))
                    Toast.makeText(OTPVerification.this,"Please Enter OTP",Toast.LENGTH_SHORT).show();
                else
                    verifyCode(otp.getText().toString().trim());
            }
        });
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);
    }
    private void signInWithCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(OTPVerification.this, "Mobile no is verified", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPref = getSharedPreferences("otpCheckKey", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("value", otpCheck);
                    editor.apply();

                    otp.setText(code);
                    Intent intent = new Intent(OTPVerification.this,UserType.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(OTPVerification.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
        mCallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public  void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken){
            super.onCodeSent(s,forceResendingToken);
            verificationid=s;
        }
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                verifyCode(code);
//                Intent intent = new Intent(OTPVerification.this,UserType.class);
//                startActivity(intent);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OTPVerification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onBackPressed() {
        otp = findViewById(R.id.otp);
        if (code==null || !(otp.getText().toString().equals(code)) ) {
            super.onBackPressed();
        } else {
            Toast.makeText(OTPVerification.this, "Verified, click on next", Toast.LENGTH_SHORT).show();
        }
    }
}