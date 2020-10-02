package e.user.mistridada;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class Done extends AppCompatActivity {


    private String totalAmount = "";
    private String id = "";
    TextView orderid,pay;
    Button done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        totalAmount = getIntent().getStringExtra("Total Price");
        id = getIntent().getStringExtra("Orderid");


        orderid = (TextView)findViewById(R.id.orderid);
        pay = (TextView)findViewById(R.id.payment);
        done = (Button) findViewById(R.id.btndone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Done.this, GroceryHistory.class);
                startActivity(intent);
                finish();
            }
        });


        orderid.setText(id);
        pay.setText(totalAmount);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
