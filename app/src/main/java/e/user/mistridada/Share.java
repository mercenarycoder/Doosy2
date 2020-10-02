package e.user.mistridada;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.net.URI;

import e.user.mistridada.Common.Common;
import e.user.mistridada.Model.DoozyInfo;


public class Share extends AppCompatActivity {

    EditText editTextName;
    Button buttonAdd;
    TextView mob,testmob,txtrefer;
    String refdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonAdd = (Button) findViewById(R.id.sharebtn);
        mob = (TextView)findViewById(R.id.mnum);
        testmob = (TextView)findViewById(R.id.testnum);
        txtrefer = (TextView)findViewById(R.id.txtrefer);
        String pho = Common.currentUser.getPhone();
        final String refcode = String.valueOf(pho).replace("+91","D").replace("00","B")
                .replace("11","C").replace("22","E").replace("33","F").replace("44","G")
                .replace("55","H").replace("66","I").replace("77","J").replace("88","K")
                .replace("99","L").replace("10","N").replace("12","O").replace("23","P")
                .replace("56","S").replace("67","T").replace("78","U").replace("89","V")
                .replace("20","W").replace("30","X").replace("40","Y").replace("50","Z")
                .replace("60","A").replace("70","M").replace("80","R").replace("90","Q");
        mob.setText(refcode);

        /***String testrefcode = (refcode).replace("D","+91").replace("B","00")
                .replace("C","11").replace("E","22").replace("F","33").replace("G","44")
                .replace("H","55").replace("I","66").replace("J","77").replace("K","88")
                .replace("L","99").replace("N","10").replace("O","12").replace("P","23")
                .replace("S","56").replace("T","67").replace("U","78").replace("V","89")
                .replace("W","20").replace("X","30").replace("Y","40").replace("Z","50")
                .replace("A","60").replace("M","70").replace("R","80").replace("Q","90");
        testmob.setText(testrefcode);***/
        fetchprice();
        referdata();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://play.google.com/store/apps/details?id=e.user.mistridada";

                FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse(link))
                        .setDomainUriPrefix("https://doosy.page.link")
                        .setAndroidParameters(
                                new DynamicLink.AndroidParameters.Builder("com.example.android")
                                        .setMinimumVersion(125)
                                        .build())
                        .buildShortDynamicLink()
                        .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                            @Override
                            public void onSuccess(ShortDynamicLink shortDynamicLink) {
                                Uri mInvitationUrl = shortDynamicLink.getShortLink();
                                String invitationLink = mInvitationUrl.toString();
                                String msg = refdata + " Use my referrer code: "+ refcode +"  "
                                        + invitationLink;
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_TEXT, msg);
                                intent.setType("text/plain");
                                startActivity(intent);

                            }
                        });
            }
        });
    }

    private void referdata() {
        final DatabaseReference doozy = FirebaseDatabase.getInstance().getReference("DoozyInfo");
        doozy.child("-LlREybDHInYc4ycvYhq").child("reftext").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                refdata = dataSnapshot.getValue(String.class);
                txtrefer.setText(refdata);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchprice() {

            final DatabaseReference doozy = FirebaseDatabase.getInstance().getReference("Wallet");
            doozy.child(Common.currentUser.getPhone()).child("credits").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(String.class)!=null) {
                        String rewards = dataSnapshot.getValue(String.class);
                        testmob.setText(rewards);
                        //else if(getIntent().getStringExtra("changecharge").equals("restnot"));
                    }
                    else
                        testmob.setText("0");
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
