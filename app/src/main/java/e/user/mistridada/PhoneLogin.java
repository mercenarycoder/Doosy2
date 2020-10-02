package e.user.mistridada;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import e.user.mistridada.Common.Common;
import e.user.mistridada.Model.Rider;
import io.paperdb.Paper;

public class PhoneLogin extends AppCompatActivity  {

    EditText editTextPhone, editTextCode;
    TextView sendotp,some_number;
    TextView buttonSign;
    EditText  digit_1,digit_2,digit_3,digit_4,digit_5,digit_6;
ScrollView countrycd;
LinearLayout after_number;

    FirebaseAuth mAuth;
    private static final String TAG = "PhoneLogin";

    String codeSent;
    String phonenum;
    android.app.AlertDialog waitingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login2);
        mAuth = FirebaseAuth.getInstance();
        digit_1=(EditText)findViewById(R.id.digit_1);
        digit_2=(EditText)findViewById(R.id.digit_2);
        digit_3=(EditText)findViewById(R.id.digit_3);
        digit_4=(EditText)findViewById(R.id.digit_4);
        digit_5=(EditText)findViewById(R.id.digit_5);
        digit_6=(EditText)findViewById(R.id.digit_6);
        digit_1.addTextChangedListener(new watcher(digit_1));
        digit_2.addTextChangedListener(new watcher(digit_2));
        digit_3.addTextChangedListener(new watcher(digit_3));
        digit_4.addTextChangedListener(new watcher(digit_4));
        digit_5.addTextChangedListener(new watcher(digit_5));
        digit_6.addTextChangedListener(new watcher(digit_6));
        waitingDialog = new SpotsDialog(PhoneLogin.this);
        after_number=(LinearLayout)findViewById(R.id.after_number);
        buttonSign = findViewById(R.id.buttonSignIn);
        sendotp = findViewById(R.id.buttonGetVerificationCode);
        editTextPhone = findViewById(R.id.editTextPhone);
        countrycd = (ScrollView)findViewById(R.id.countrycd);
        //editTextCode.setVisibility(View.INVISIBLE);
        buttonSign.setVisibility(View.INVISIBLE);
    some_number=(TextView)findViewById(R.id.some_number);
        findViewById(R.id.buttonGetVerificationCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(PhoneLogin.this, 0);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.confirm_dialog);
                final CheckBox checkbox=(CheckBox)dialog.findViewById(R.id.checkbox);
                Button cancel_verification=(Button)dialog.findViewById(R.id.cancel_verification);
                Button proceed_verification=(Button)dialog.findViewById(R.id.proceed_verification);
                cancel_verification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(PhoneLogin.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                proceed_verification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkbox.isChecked())
                        {
                            sendVerificationCode();
                            some_number.setText("We Have Sent a OTP on your Number \\n +91-"+editTextPhone.getText().toString());
                            dialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(PhoneLogin.this,"First check the box first",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });


        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingDialog.show();
                verifySignInCode();
            }
        });
    }

    private void verifySignInCode(){

        String code ="";
        int i=0;
            switch(i)
            {
                case 0:
                    code+=digit_1.getText().toString();
                case 1:
                    code+=digit_2.getText().toString();
                case 3:
                    code+=digit_3.getText().toString();
                case 4:
                    code+=digit_4.getText().toString();
                case 5:
                    code+=digit_5.getText().toString();
                case 6:
                    code+=digit_6.getText().toString();
                    break;
            }
        if(code.length()<6)
        {
            Toast.makeText(PhoneLogin.this,"Code should be of length 6",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(code)){
            Toast.makeText(PhoneLogin.this, "Please enter code", Toast.LENGTH_SHORT).show();
            waitingDialog.dismiss();
        }
        else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                           final DatabaseReference databaseReference= FirebaseDatabase.getInstance().
                                   getReference(Common.user_rider_tbl).
                                   child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    //do ur stuff
                                                    Common.currentUser = dataSnapshot.getValue(Rider.class);
                                                    waitingDialog.dismiss();
                                                    startActivity(new Intent(PhoneLogin.this,Home.class));
                                                    Paper.book().write(Common.phone_field,phonenum);
                                                    finish();
                                                } else {
                                                    //do something if not exists
                                                    waitingDialog.dismiss();
                                                    Intent intent = new Intent(PhoneLogin.this, EnterName.class);
                                                    intent.putExtra("phone", phonenum);
                                                    startActivity(intent);
                                                }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                            // ...
                        } else {
                            waitingDialog.dismiss();
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneLogin.this, "OTP invalid.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }

    private void sendVerificationCode(){

        String phone = editTextPhone.getText().toString();
        String pho = "+";
        Integer cod= 91;
        String total= Integer.toString(cod);
        phonenum = pho.concat(total).concat(phone);

        if(phone.isEmpty()){
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return;
        }

        if(phone.length() < 10 ){
            editTextPhone.setError("Please enter a valid phone");
            editTextPhone.requestFocus();
            return;
        }

       // editTextCode.setVisibility(View.VISIBLE);
//        editTextCode.setEnabled(true);
        buttonSign.setVisibility(View.VISIBLE);
        buttonSign.setEnabled(true);
        editTextPhone.setVisibility(View.INVISIBLE);
        editTextPhone.setEnabled(false);
        countrycd.setVisibility(View.INVISIBLE);
        sendotp.setVisibility(View.INVISIBLE);
        sendotp.setEnabled(false);
        after_number.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenum,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }



    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new
            PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(PhoneLogin.this, "Phone number verified instantly",
                    Toast.LENGTH_LONG).show();
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s,@NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
          //  super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

 private class  watcher implements TextWatcher {
     View view;
     public watcher(View view)
     {
         this.view=view;
     }
        @Override
     public void beforeTextChanged(CharSequence s, int start, int count, int after) {

     }

     @Override
     public void onTextChanged(CharSequence s, int start, int before, int count) {

     }

     @Override
     public void afterTextChanged(Editable s) {
         String text = s.toString();
         switch (view.getId()) {
             case R.id.digit_1: {
                 if (text.length() == 1) {
                     digit_2.requestFocus();
                 }
                 break;
             }
             case R.id.digit_2: {
                 if (text.length() == 1) {
                     digit_3.requestFocus();
                 } else if (text.length() == 0) {
                     digit_1.requestFocus();
                 }
                 break;
             }
             case R.id.digit_3: {
                 if (text.length() == 1) {
                     digit_4.requestFocus();
                 } else if (text.length() == 0) {
                     digit_3.requestFocus();
                 }
                 break;
             }
             case R.id.digit_4: {
                 if (text.length() == 1) {
                     digit_5.requestFocus();
                 } else if (text.length() == 0) {
                     digit_4.requestFocus();
                 }
                 break;
             }
             case R.id.digit_5: {
                 if (text.length() == 1) {
                     digit_6.requestFocus();
                 } else if (text.length() == 0) {
                     digit_4.requestFocus();
                 }
                 break;
             }
             case R.id.digit_6: {
                 if (text.length() == 0) {
                     digit_5.requestFocus();
                 }
                 break;
             }
         }
     }
 }
}
