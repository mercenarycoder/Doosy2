package e.user.mistridada.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;


import e.user.mistridada.Interface.ItemClickListner;
import e.user.mistridada.R;

public class OrderItemView extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductPrice, txtProductQuantity, pid;
    private ItemClickListner itemClickListner;



    public OrderItemView(View itemView)
    {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.cart_product_name2);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price2);
        pid = (TextView) itemView.findViewById(R.id.pid12);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity2);


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
