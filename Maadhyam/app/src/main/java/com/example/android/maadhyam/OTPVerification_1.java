package com.example.android.maadhyam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPVerification_1 extends AppCompatActivity {
    private String verificationid,code[],otpCheck="verified",c;
    private FirebaseAuth mAuth;
    private Button conf_button;
    private TextView tvView;
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private EditText et5;
    private EditText et6;
    private EditText[] edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification);

        String mobile_no_extracted = "+91" + getIntent().getStringExtra("MOBILE_NO");
        // EditText mobile_no = findViewById(R.id.mobile_no);
        String text = "Please enter the 6 digit code sent to " + mobile_no_extracted;
        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);
        tvView = findViewById(R.id.tvView);
        tvView.setText(text);



       /* mobile_no.setHint("  "+mobile_no_extracted);
        mobile_no.setFocusable(false);*/

        mAuth = FirebaseAuth.getInstance();
        sendVerificationCode(mobile_no_extracted);
        EditText[] edit = {et1, et2, et3, et4, et5, et6};
        et1.addTextChangedListener(new GenericTextWatcher(edit, et1));
        et2.addTextChangedListener(new GenericTextWatcher(edit, et2));
        et3.addTextChangedListener(new GenericTextWatcher(edit, et3));
        et4.addTextChangedListener(new GenericTextWatcher(edit, et4));
        et5.addTextChangedListener(new GenericTextWatcher(edit, et5));
        et6.addTextChangedListener(new GenericTextWatcher(edit, et6));

        conf_button = findViewById(R.id.conf_button);
        conf_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp_recd = et1.getText().toString()+et2.getText().toString()+et3.getText().toString()
                        +et4.getText().toString()+et5.getText().toString()+et6.getText().toString();
                verifyCode(otp_recd);
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
                if(task.isSuccessful()) {
                    Toast.makeText(OTPVerification_1.this, "Mobile no is verified", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPref = getSharedPreferences("otpCheckKey", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("value", otpCheck);
                    editor.apply();

                    et1.setText(code[0]);
                    et2.setText(code[1]);
                    et3.setText(code[2]);
                    et4.setText(code[3]);
                    et5.setText(code[4]);
                    et6.setText(code[5]);
                    Intent intent = new Intent(OTPVerification_1.this,UserType.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(OTPVerification_1.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
            for(int i=0;i<6;i++)
            {
            code[i] = phoneAuthCredential.getSmsCode();
            c = c+code[i];}

            if(c!=null){
                verifyCode(c);
//                Intent intent = new Intent(OTPVerification.this,UserType.class);
//                startActivity(intent);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OTPVerification_1.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

  /*  @Override
    public void onBackPressed() {
       otp = findViewById(R.id.otp);
       if (code==null || !(otp.getText().toString().equals(code)) ) {
            super.onBackPressed();
        } else {
            Toast.makeText(OTPVerification.this, "Verified, click on next", Toast.LENGTH_SHORT).show();
        }
    }

    public void Back(View view) {
        onBackPressed();
    }*/
}

