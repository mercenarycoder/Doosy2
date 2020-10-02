package e.user.mistridada;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.provider.Settings;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class RequestForm extends AppCompatActivity {

    EditText  editTextName1, editTextName2, editTextPhone;
    TextView editTextAddress,editTextName;
    Button buttonAdd;
    Spinner spinnerService;
    String hint;
    LatLng pickuplatlag;
    String pick_address="";
    LatLng destinationLatlag;
    String dest_address="";
    Context mContext;
    String find_check="";
    boolean check_first=false,check_second=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=RequestForm.this;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                       /* requestPermissions((String[]) permissionsToRequest.toArray(new
                                        String[permissionsToRequest
                                        .size()]),
                             ALL_PERMISSIONS_RESULT);
                */
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("GPS is not Enabled!");
                alertDialog.setMessage("Enable GPS for smooth experience ..");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        //startActivityForResult(intent,ALL_PERMISSIONS_RESULT);
                        startActivity(intent);
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
            else
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("GPS is not Enabled!");
                alertDialog.setMessage("Do you want to turn on GPS?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        //startActivityForResult(intent,ALL_PERMISSIONS_RESULT);
                        startActivity(intent);
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        }

        setContentView(R.layout.activity_request_form);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        pickuplatlag=new LatLng(0,0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hint = getIntent().getStringExtra("hint1");

        destinationLatlag=new LatLng(0,0);


        editTextName = (TextView) findViewById(R.id.edtseName);
        //editTextName1 = (EditText) findViewById(R.id.edtseName1);
        editTextName2 = (EditText) findViewById(R.id.edtseName2);
        editTextAddress = (TextView) findViewById(R.id.edtseAddress);
        buttonAdd = (Button) findViewById(R.id.orderbutton);
        spinnerService = (Spinner) findViewById(R.id.spinnerservice);
        //editTextName1.setVisibility(View.GONE);
        editTextName.setHint(hint);
       // editTextAddress.setInputType(InputType.TYPE_NULL);
       // editTextName.setInputType(InputType.TYPE_NULL);
        //editTextAddress.setText(dest_address);
        editTextAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextName.getText().toString().equals("Pick from...")) {
                    Intent intent = new Intent(RequestForm.this, Welcome1.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent,2);
                  //  finish();
                }
                else
                {
                    Toast.makeText(RequestForm.this,"First Enter picking location",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        //editTextName.setText(pick_address);
        editTextName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestForm.this, Welcome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,1);
              //  finish();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pick_address.equals("Pickup from")||dest_address.equals("Deliver to...")||!check_first||!check_second)
                {
                    Toast.makeText(RequestForm.this,"Please fill the picking and delivering location first",Toast.LENGTH_SHORT).show();
                }
                else {
                  if(checkDistance()) {
                      Intent intent = new Intent(RequestForm.this, SendOrder.class);
                      intent.putExtra("pickupaddress", editTextName.getText().toString().trim());
                      intent.putExtra("deliveryaddress", editTextAddress.getText().toString().trim());
                      intent.putExtra("packagecontents", spinnerService.getSelectedItem().toString());
                      intent.putExtra("taskdetail", editTextName2.getText().toString().trim());
                      //intent.putExtra("changecharge", "restnot");
                      Bundle b = new Bundle();
                      b.putParcelable("pickupaddval", pickuplatlag);
                      intent.putExtras(b);
                      Bundle c = new Bundle();
                      c.putParcelable("destinationLatlag", destinationLatlag);
                      intent.putExtras(c);

                      startActivity(intent);
                  }
                  else
                  {

                  }
                  //return;
                }
                }
        });
    }
public boolean checkDistance()
{
    if(SphericalUtil.computeDistanceBetween(pickuplatlag,destinationLatlag) <3){
        Toast.makeText(RequestForm.this, "Pickup location and drop location is " +
                "very close or same. Please change!!", Toast.LENGTH_LONG).show();
    return false;
    }
    else if(SphericalUtil.computeDistanceBetween(pickuplatlag,destinationLatlag) >40000){
        Toast.makeText(RequestForm.this, "Distance is too long!!", Toast.LENGTH_LONG).show();
    return false;
    }
return true;
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode== 1) {

                    pickuplatlag = data.getParcelableExtra("pickupaddval");
                    pick_address = data.getStringExtra("pick_parcel");
               //     Toast.makeText(RequestForm.this, pick_address, Toast.LENGTH_SHORT).show();
                    editTextName.setText(pick_address);
                    check_second=true;
            }
            if (resultCode == 2) {
                destinationLatlag = data.getParcelableExtra("destinationLatlag");
                dest_address = data.getStringExtra("dest_address");
             //   Toast.makeText(RequestForm.this, dest_address, Toast.LENGTH_SHORT).show();
                check_first = true;
                editTextAddress.setText(dest_address);
            }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
