package e.user.mistridada;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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


import e.user.mistridada.Model.Request;

public class GroceryHistory extends AppCompatActivity {


    DatabaseReference databaseArtists;

    ListView listViewArtists;

    List<Request> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grocery_history);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseArtists = FirebaseDatabase.getInstance().getReference("GroceryOrder");

        listViewArtists = (ListView) findViewById(R.id.listViewArtists2);

        artistList = new ArrayList<>();

        OrderList adapter = new OrderList(GroceryHistory.this, artistList);
        listViewArtists.setStackFromBottom(true);
        Collections.reverse(artistList);
        listViewArtists.setAdapter(adapter);

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                //Getting the selected artist
                Request rider = artistList.get(i);


                //creating an intent
                Intent intent = new Intent(getApplicationContext(), OrderProducts.class);

                //putting labour name,avatar,phone to new activity
                intent.putExtra("orderid", rider.getOrderid());

                //Starting activity with intent
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseArtists.orderByChild("customer").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                artistList.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Request artist = postSnapshot.getValue(Request.class);
                    //adding artist to the list
                    artistList.add(artist);
                }

                //creating adapter
                OrderList adapter = new OrderList(GroceryHistory.this, artistList);
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
