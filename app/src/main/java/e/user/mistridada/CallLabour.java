package e.user.mistridada;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallLabour extends AppCompatActivity {

    CircleImageView avatar_image;
    TextView txt_name,txt_phone;
    Button btn_call_labour_phone;

    String phoneNum;
    String MName;
    String avataid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_labour);

        //Init View
        avatar_image = (CircleImageView)findViewById(R.id.avatar_image);
        txt_name = (TextView)findViewById(R.id.txt_name);
        txt_phone = (TextView)findViewById(R.id.txt_phone);
        btn_call_labour_phone = (Button)findViewById(R.id.btn_call_labour_phone);

        btn_call_labour_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+txt_phone.getText().toString()));
                startActivity(intent);
            }
        });

        //get intent
        if(getIntent() !=null)
        {
            phoneNum = getIntent().getStringExtra("phoneId");
            MName = getIntent().getStringExtra("MistriName");
            avataid = getIntent().getStringExtra("avatarId");

        }
        txt_name.setText(MName);
        txt_phone.setText(phoneNum);
        //Load Avatar
        if(avataid != null
                && !TextUtils.isEmpty(avataid)) {
            Picasso.with(this)
                    .load(avataid)
                    .into(avatar_image);
        }
    }
}
