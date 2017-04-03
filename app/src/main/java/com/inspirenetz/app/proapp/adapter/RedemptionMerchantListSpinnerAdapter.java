package com.inspirenetz.app.proapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.resources.MembershipResource;
import com.inspirenetz.app.proapp.resources.RedemptionPartnerResource;

import java.util.List;

/**
 * Created by Raju on 31-Aug-16.
 */
public class RedemptionMerchantListSpinnerAdapter extends ArrayAdapter<RedemptionPartnerResource> {


    private final Context context;
    private List<RedemptionPartnerResource> objects;

    public RedemptionMerchantListSpinnerAdapter(Context context, int textViewResourceId, List objects) {
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
        RedemptionPartnerResource redemptionPartnerResource = objects.get(position);

        // Get partner merchant name from the redemptionPartnerResource object
        String merchantName = redemptionPartnerResource.getRemName();

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
        RedemptionPartnerResource redemptionPartnerResource = objects.get(position);

        // Get partner merchant name from the redemptionPartnerResource object
        String merchantName = redemptionPartnerResource.getRemName();

        // Set the merchant name to the spinner text view
        lblSpinnerText.setText(merchantName);

        return rowView;
    }
}
