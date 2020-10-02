package e.user.mistridada;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import e.user.mistridada.Model.Request;

public class OrderList extends ArrayAdapter<Request> {
    private Activity context;
    List<Request> artists;

    public OrderList(Activity context, List<Request> artists) {
        super(context, R.layout.orders_list, artists);
        this.context = context;
        this.artists = artists;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.orders_list, null, true);

        TextView textViewPickup = (TextView) listViewItem.findViewById(R.id.orderidh);
        TextView textViewDrop = (TextView) listViewItem.findViewById(R.id.payh);
        TextView textViewContent = (TextView) listViewItem.findViewById(R.id.addh);
        TextView textViewCharges = (TextView) listViewItem.findViewById(R.id.timeh);



        Request rider = artists.get(position);



        textViewPickup.setText(rider.getOrderid());
        textViewDrop.setText(rider.getTotal());
        textViewContent.setText(rider.getAddress());
        textViewCharges.setText(rider.getDate());


        return listViewItem;
    }
}
