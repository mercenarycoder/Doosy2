package e.user.mistridada;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import e.user.mistridada.Common.Common;
import e.user.mistridada.Model.DoozyInfo;
import e.user.mistridada.Model.Rider;
import e.user.mistridada.Model.Wallet;
import io.paperdb.Paper;

public class  EnterName extends AppCompatActivity {

    EditText editTextName, enterreferral;
    Button buttonAdd;

    FirebaseDatabase db;
    DatabaseReference users;

    String phone, refer;
    String price;
    String referee;
    String statuspdg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_name);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db= FirebaseDatabase.getInstance();
        users = db.getReference(Common.user_rider_tbl);
        phone = getIntent().getStringExtra("phone");

        editTextName = (EditText) findViewById(R.id.entername);
        enterreferral = (EditText) findViewById(R.id.enterreferral);
        refer = enterreferral.getText().toString();

        rewardoffer();

        buttonAdd = (Button) findViewById(R.id.enterbutton);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check validation
                if (TextUtils.isEmpty(editTextName.getText().toString())) {
                    Toast.makeText(EnterName.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(enterreferral.getText().toString())) {
                    referee = (enterreferral.getText().toString()).replace("D","+91").replace("B","00")
                            .replace("C","11").replace("E","22").replace("F","33").replace("G","44")
                            .replace("H","55").replace("I","66").replace("J","77").replace("K","88")
                            .replace("L","99").replace("N","10").replace("O","12").replace("P","23")
                            .replace("S","56").replace("T","67").replace("U","78").replace("V","89")
                            .replace("W","20").replace("X","30").replace("Y","40").replace("Z","50")
                            .replace("A","60").replace("M","70").replace("R","80").replace("Q","90");

                    rewardcredits();
                }

                //Save user to db
                Rider rider = new Rider();
                rider.setEmail("");
                rider.setName(editTextName.getText().toString());
                rider.setPhone(phone);
                //default value for it
                rider.setAvatarUrl("");

                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(rider)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EnterName.this, "Registered successfully!", Toast.LENGTH_LONG).show();
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                //After assigned value
                                                Common.currentUser = dataSnapshot.getValue(Rider.class);
                                                //Start new activity
                                                final DatabaseReference doozy = FirebaseDatabase.getInstance().getReference("DoozyInfo");
                                                doozy.child("-LlREybDHInYc4ycvYhq").child("signupreward").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        String price1 = dataSnapshot.getValue(String.class);
                                                        final DatabaseReference referdb = FirebaseDatabase.getInstance().getReference("Wallet");

                                                        Wallet rider = new Wallet();
                                                        rider.setCredits(price1);

                                                        referdb.child(phone)
                                                                .setValue(rider)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {


                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                    }
                                                                });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                Intent intent = new Intent(EnterName.this,Home.class);
                                                intent.putExtra("status",statuspdg);
                                                intent.putExtra("price",price);
                                                intent.putExtra("referee",referee);
                                                startActivity(intent);
                                                Paper.book().write(Common.phone_field,phone);
                                                finish();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EnterName.this, "Failed "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void rewardoffer() {
        final DatabaseReference doozy = FirebaseDatabase.getInstance().getReference("DoozyInfo");
        doozy.child("-LlREybDHInYc4ycvYhq").child("refer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                price = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void rewardcredits() {
        final DatabaseReference doozy = FirebaseDatabase.getInstance().getReference("Wallet");
        doozy.child(referee).child("credits").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null) {
                    String cost = dataSnapshot.getValue(String.class);
                    final int x = Integer.parseInt(cost);
                    final int y = Integer.parseInt(price);
                    final int z = x+y;
                    String cos= Integer.toString(z);
                    final DatabaseReference referdb = FirebaseDatabase.getInstance().getReference("Wallet");

                    Wallet rider = new Wallet();
                    rider.setCredits(cos);

                    referdb.child(referee)
                            .setValue(rider)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EnterName.this, "updated successfully!", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EnterName.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                    //else if(getIntent().getStringExtra("changecharge").equals("restnot"));
                }
                else
                {

                        final DatabaseReference referdb = FirebaseDatabase.getInstance().getReference("Wallet");

                        Wallet rider = new Wallet();
                        rider.setCredits(price);

                        referdb.child(referee)
                                .setValue(rider)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EnterName.this, "Reward Created!", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EnterName.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
