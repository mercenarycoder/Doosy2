package e.user.mistridada.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import e.user.mistridada.Interface.ItemClickListner;

import e.user.mistridada.R;

public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtStoreName, txtStoreAddress, txtStoreFoods,shop_range;
    public ImageView imageView;
    public ElegantNumberButton numberButton;
    public Button addToCartButton,eleminus,eleplus;
    public ItemClickListner listner;

    public StoreViewHolder(View itemView)
    {
        super(itemView);
        shop_range=(TextView)itemView.findViewById(R.id.store_range);
        imageView = (ImageView) itemView.findViewById(R.id.store_image);
        txtStoreName = (TextView) itemView.findViewById(R.id.store_name);
        txtStoreAddress = (TextView) itemView.findViewById(R.id.store_address);
        txtStoreFoods = (TextView) itemView.findViewById(R.id.store_food);
    }


    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }


}
