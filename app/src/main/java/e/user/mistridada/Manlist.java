package e.user.mistridada;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import e.user.mistridada.Common.Common;
import e.user.mistridada.Model.CreateOrder;


public class Manlist extends AppCompatActivity {


    DatabaseReference databaseArtists;
    Double picklat,picklon;
    Double droplat,droplon;
    private LatLng pickupLatLng;
    private LatLng pickupLatLng2;

    ListView listViewArtists;

    List<CreateOrder> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manlist);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseArtists = FirebaseDatabase.getInstance().getReference(Common.create_order);

        listViewArtists = (ListView) findViewById(R.id.listViewArtists);

        artistList = new ArrayList<>();

        LabourList adapter = new LabourList(Manlist.this, artistList);
        listViewArtists.setStackFromBottom(true);
        Collections.reverse(artistList);
        listViewArtists.setAdapter(adapter);

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                //Getting the selected artist
                CreateOrder rider = artistList.get(i);


                //creating an intent
                Intent intent = new Intent(getApplicationContext(), OrderDetail.class);

                //putting labour name,avatar,phone to new activity
                if(rider.getOrderid()==null){
                    Toast.makeText(Manlist.this, "This request Couldn't open!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    intent.putExtra("orderid", rider.getOrderid());
                    startActivity(intent);
                    /**picklat = rider.getPickuplat();
                    picklon= rider.getPickuplon();
                    droplat = rider.getDroplat();
                    droplon= rider.getDroplon();

                    pickupLatLng = new LatLng(picklat,picklon);
                    pickupLatLng2 = new LatLng(droplat,droplon);

                    Bundle c = new Bundle();
                    c.putParcelable("pickupaddval", pickupLatLng);
                    intent.putExtras(c);

                    Bundle b = new Bundle();
                    b.putParcelable("dropaddva1", pickupLatLng2);
                    intent.putExtras(b);**/

                    //Starting activity with intent

                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseArtists.orderByChild("customerid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                artistList.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    CreateOrder artist = postSnapshot.getValue(CreateOrder.class);
                    //adding artist to the list
                    artistList.add(artist);
                }

                //creating adapter
                LabourList adapter = new LabourList(Manlist.this, artistList);
                //attaching adapter to the listview
                listViewArtists.setAdapter(adapter);

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
