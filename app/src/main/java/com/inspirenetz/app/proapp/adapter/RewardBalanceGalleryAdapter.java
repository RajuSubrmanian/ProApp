package com.inspirenetz.app.proapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.RewardBalanceResource;
import com.inspirenetz.app.proapp.resources.UserInfoResource;

import java.util.List;

/**
 * Created by Raju on 29-Jun-16.
 */
public class RewardBalanceGalleryAdapter extends ArrayAdapter<RewardBalanceResource> {

    private final Context context;
    private List<RewardBalanceResource> objects;

    public RewardBalanceGalleryAdapter(Context context, int textViewResourceId, List<RewardBalanceResource> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.content_of_gallery_adapter, parent, false);

        //Declare text view's
        TextView lblCusLoyaltyId = (TextView)rowView.findViewById(R.id.lblCusLoyaltyId);
        TextView lblRewardPoints = (TextView)rowView.findViewById(R.id.lblRewardPoints);
        TextView lblRewardCurrency = (TextView)rowView.findViewById(R.id.lblRewardCurrency);

        //convert reward balance double to int
        RewardBalanceResource rewardBalanceResource=objects.get(position);
        Double rewardBalance = rewardBalanceResource.getRwd_balance();
        int convertRewardBalance = rewardBalance.intValue();

        //get loyalty id from session
        SessionManager sessionManager = new SessionManager(context);
        UserInfoResource userInfoResource = sessionManager.getUserInfo();
        String cusLoyaltyId = userInfoResource.getUsrLoginId();

        //set values to text view
        lblCusLoyaltyId.setText(cusLoyaltyId);
        lblRewardPoints.setText(String.valueOf(convertRewardBalance));
        lblRewardCurrency.setText(rewardBalanceResource.getRwd_name());

        return rowView;
    }

}
