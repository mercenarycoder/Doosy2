package e.user.mistridada.ViewHolder;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import e.user.mistridada.CartActivity;
import e.user.mistridada.Interface.ItemClickListner;
import e.user.mistridada.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductPrice, txtProductQuantity, pid, txtProductDescription;
    private ItemClickListner itemClickListner;
    public Button removeitem;

    DatabaseReference cartListRef;


    public CartViewHolder(View itemView)
    {
        super(itemView);
        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        txtProductDescription = itemView.findViewById(R.id.cart_product_description);
        pid = (TextView) itemView.findViewById(R.id.pid1);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);
        removeitem = (Button) itemView.findViewById(R.id.remove);

    }

    private void removecartitem() {
        cartListRef.child("User View")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Products")
                .child(pid.getText().toString())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(itemView.getContext(), "Item removed successfully.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    @Override
    public void onClick(View view)
    {
        itemClickListner.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner)
    {
        this.itemClickListner = itemClickListner;
    }
}
