package com.inspirenetz.app.proapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.resources.MembershipResource;

import java.util.List;

/**
 * Created by Raju on 31-Aug-16.
 */
public class MerchantsListSpinnerAdapter extends ArrayAdapter<MembershipResource> {

    private final Context context;
    private List<MembershipResource> objects;

    public MerchantsListSpinnerAdapter(Context context, int textViewResourceId, List objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.spinner_item, parent, false);

        // initialize spinner text
        TextView lblSpinnerText=(TextView)rowView.findViewById(R.id.lblSpinnerText);

        // Get the position of list object
        MembershipResource membershipResource = objects.get(position);

        // Get merchant name from the membershipResource object
        String merchantName = membershipResource.getMerMerchantName();

        // Set the merchant name to the spinner text view
        lblSpinnerText.setText(merchantName);

        return rowView;

    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.spinner_item, parent, false);

        // initialize spinner text
        TextView lblSpinnerText=(TextView)rowView.findViewById(R.id.lblSpinnerText);

        // Get the position of list object
        MembershipResource membershipResource = objects.get(position);

        // Get merchant name from the membershipResource object
        String merchantName = membershipResource.getMerMerchantName();

        // Set the merchant name to the spinner text view
        lblSpinnerText.setText(merchantName);

        return rowView;
    }
}
