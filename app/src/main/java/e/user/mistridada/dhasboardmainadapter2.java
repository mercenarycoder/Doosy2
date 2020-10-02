package e.user.mistridada;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import e.user.mistridada.Model.Store_Model;
//import e.user.mistridadapart.Model.base_shop;

public class dhasboardmainadapter2  extends RecyclerView.Adapter<dhasboardmainadapter2.viewholder1>{
    ArrayList<Store_Model> list;
    Context context;
    String shop_type;
    DatabaseReference detail_shops;
    public dhasboardmainadapter2(ArrayList<Store_Model> list, Context context,String id)
    {
        this.list=list;
        this.context=context;
        this.shop_type=id;
    }
    @NonNull
    @Override
    public viewholder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        View inflator=LayoutInflater.from(context).inflate(R.layout.store_listing_layout, parent,
                false);
        viewholder1 viewhold=new viewholder1(inflator);
        return viewhold;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder1 holder, final int position) {
        final Store_Model model=list.get(position);
    Picasso.with(context).load(model.getImage())
    .placeholder(R.drawable.box)
    .into(holder.store_image);
    holder.store_name.setText(model.getName());
    holder.store_address.setText(model.getAddress());
    holder.store_food.setText(model.getType());
    holder.click_shop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, StoreProfile.class);
            intent.putExtra("merchantkey", model.getMerchant());
            intent.putExtra("shoptype", shop_type);
            context.startActivity(intent);
        }
    });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewholder1 extends RecyclerView.ViewHolder
    {

        LinearLayout click_shop;
        ImageView store_image;
        TextView store_address,store_food,store_name;
        public viewholder1(@NonNull View itemView) {
            super(itemView);
        click_shop=(LinearLayout)itemView.findViewById(R.id.click_shop);
        store_image=(ImageView)itemView.findViewById(R.id.store_image);
        store_address=(TextView)itemView.findViewById(R.id.store_address);
        store_food=(TextView)itemView.findViewById(R.id.store_food);
        store_name=(TextView)itemView.findViewById(R.id.store_name);
        }
    }
}
