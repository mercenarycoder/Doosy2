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

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductDescription, txtProductPrice, pid,eletext;
    public ImageView imageView;
    public ElegantNumberButton numberButton;
    public Button addToCartButton,eleminus,eleplus;
    public ItemClickListner listner;
    private int counter = 1;
    public LinearLayout Ele;



    public ProductViewHolder(View itemView)
    {
        super(itemView);

        Ele = (LinearLayout)itemView.findViewById(R.id.elelayout);
        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        pid = (TextView) itemView.findViewById(R.id.pid);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
        addToCartButton = (Button) itemView.findViewById(R.id.pd_add_to_cart_button);
        //numberButton = (ElegantNumberButton) itemView.findViewById(R.id.number_btn);
        eleplus = (Button) itemView.findViewById(R.id.plusBtn);
        eleminus = (Button) itemView.findViewById(R.id.minusBtn);
        eletext = (TextView) itemView.findViewById(R.id.eletext);
        eleplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusCounter();
            }
        });
        eleminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusCounter();
            }
        });
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                    addingToCartList();

            }
        });
    }

    private void minusCounter() {
        counter = Integer.valueOf(eletext.getText().toString());
        if(counter!=1){
            counter--;
            eletext.setText(Integer.toString(counter));
        }
    }

    private void plusCounter() {
        counter = Integer.valueOf(eletext.getText().toString());
        if(counter!=24){
            counter++;
            eletext.setText(Integer.toString(counter));
        }
    }

    private void addingToCartList() {
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", pid.getText().toString());
        cartMap.put("pname", txtProductName.getText().toString());
        cartMap.put("price", txtProductPrice.getText().toString());
        cartMap.put("description", txtProductDescription.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", Integer.toString(counter));
        cartMap.put("discount", "");

        cartListRef.child("User View").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Products").child(pid.getText().toString())
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("Products").child(pid.getText().toString())
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(itemView.getContext(), "Item added to cart", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        }
                    }
                });
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
