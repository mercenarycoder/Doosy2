package e.user.mistridada;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About extends AppCompatActivity {

    private static final String TAG = "About";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhoneNumber();
            }
            else
            {
                Log.e(TAG, "Permission not Granted");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Element adsElement = new Element();
        adsElement.setTitle("Doosy");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_doozy_round)
                .setDescription("Doosy is door to door delivery service provider in bhopal. Need something from your favourite local store and shop or want to send biryani to your friend. We deliver grocery, sancks, food, documents, drinks and everything from anywhere within 30-60 minutes. you can send sweet to your loved one, gift for your friend. Get daily needs in just one click.")
                .addItem(new Element().setTitle("Version 1.1"))
                .addItem(adsElement)
                .addItem(CallUs())
                .addItem(PrivacyPolicy())
                .addGroup("Connect with us")
                .addEmail("ohoindia2019@gmail.com")
                .addWebsite("https://doozy.in")
                .addFacebook("https://facebook.com/Oho")
                .addTwitter("My twitter")
                .addYoutube("https://www.youtube.com/")
                .addPlayStore("https://play.google.com/store/apps/details?id=e.user.mistridada")
                .addInstagram("")
                .create();

        setContentView(aboutPage);

    }

    private Element PrivacyPolicy() {
        final Element policy = new Element();
        final String policyString = String.format("Privacy Policy");
        policy.setTitle(policyString);
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String url ="https://ohopolicy.000webhostapp.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        return policy;
    }

    private Element CallUs() {
        final Element policy = new Element();
        final String policyString = String.format("Call us");
        policy.setTitle(policyString);
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhoneNumber();
            }
        });
        return policy;
    }
    public void callPhoneNumber() {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(About.this, new String[]{android.Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + "6260729400"));
                startActivity(callIntent);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + "6260729400"));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
