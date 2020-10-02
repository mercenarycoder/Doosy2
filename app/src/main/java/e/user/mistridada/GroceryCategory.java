package e.user.mistridada;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroceryCategory extends AppCompatActivity implements View.OnClickListener {


    private CardView mazdoor,mistri,plumber,c7,c4,c5,c6,c8;
    String cat1 = "Staples";
    String cat2 = "Personalcare";
    String cat3 = "Babycare";
    String cat7 = "Households";
    String cat5 = "Snacksandchocolates";
    String cat4 = "Beverages";
    String cat6 = "Dryandspices";
    String cat8 = "Fruitandveg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coordinate_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabcart);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GroceryCategory.this,CartActivity.class));
            }
        });

        //defining Cards
        mazdoor = (CardView) findViewById(R.id.mazdoor);
        mistri = (CardView) findViewById(R.id.perscare);
        plumber = (CardView) findViewById(R.id.babycare);
        c4 = (CardView) findViewById(R.id.beverage);
        c5 = (CardView) findViewById(R.id.snacks);
        c6 = (CardView) findViewById(R.id.dryandspices);
        c7 = (CardView) findViewById(R.id.homeclean);
        c8 = (CardView) findViewById(R.id.fruits);

        //Add Click Listener to the cards
        mazdoor.setOnClickListener(this);
        mistri.setOnClickListener(this);
        plumber.setOnClickListener(this);
        c4.setOnClickListener(this);
        c5.setOnClickListener(this);
        c6.setOnClickListener(this);
        c7.setOnClickListener(this);
        c8.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()) {
            case R.id.mazdoor: i = new Intent(this, prods.class); i.putExtra("category",cat1); startActivity(i); break;
            case R.id.perscare: i = new Intent(this, prods.class);i.putExtra("category",cat2); startActivity(i); break;
            case R.id.babycare: i = new Intent(this, prods.class);i.putExtra("category",cat3); startActivity(i); break;
            case R.id.beverage: i = new Intent(this, prods.class);i.putExtra("category",cat4); startActivity(i); break;
            case R.id.snacks: i = new Intent(this, prods.class);i.putExtra("category",cat5); startActivity(i); break;
            case R.id.dryandspices: i = new Intent(this, prods.class);i.putExtra("category",cat6); startActivity(i); break;
            case R.id.homeclean: i = new Intent(this, prods.class);i.putExtra("category",cat7); startActivity(i); break;
            case R.id.fruits: i = new Intent(this, prods.class);i.putExtra("category",cat8); startActivity(i); break;
            default:break;
        }
    }
}
