package e.user.mistridada;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import e.user.mistridada.Model.CreateOrder;


public class LabourList extends ArrayAdapter<CreateOrder> {
    private Activity context;
    List<CreateOrder> artists;

    public LabourList(Activity context, List<CreateOrder> artists) {
        super(context, R.layout.list_layout, artists);
        this.context = context;
        this.artists = artists;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView textViewPickup = (TextView) listViewItem.findViewById(R.id.txtli);
        TextView textViewDrop = (TextView) listViewItem.findViewById(R.id.txtli1);
        TextView textViewContent = (TextView) listViewItem.findViewById(R.id.packagecontent);
        TextView textViewCharges = (TextView) listViewItem.findViewById(R.id.deliverycharges);



        CreateOrder rider = artists.get(position);



        textViewPickup.setText(rider.getPickupaddress());
        textViewDrop.setText(rider.getDeliveryaddress());
        textViewContent.setText(rider.getPackagecontent());
        textViewCharges.setText(rider.getPickupcharges());


        return listViewItem;
    }
}