package com.inspirenetz.app.proapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.dictionary.ActivityType;
import com.inspirenetz.app.proapp.resources.CustomerActivityListResource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by raju on 2/7/17.
 */

public class CustomerActivityListViewAdapter extends ArrayAdapter<CustomerActivityListResource> {

    private Context context;

    private List<CustomerActivityListResource> objects;


    public CustomerActivityListViewAdapter(Context context, int resource, List<CustomerActivityListResource> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Initialize the view
        final View rowView = inflater.inflate(R.layout.content_of_customer_activity_list, parent, false);

        // Initialize content of the list components
        TextView txtCuaDate = (TextView)rowView.findViewById(R.id.txtCuaDate);
        TextView txtCuaMerchantName = (TextView)rowView.findViewById(R.id.txtCuaMerchantName);
        TextView txtCuaMobileNo = (TextView)rowView.findViewById(R.id.txtCuaMobileNo);
        TextView txtCuaActivityType = (TextView)rowView.findViewById(R.id.txtCuaActivityType);
        TextView txtCuaRemarks = (TextView)rowView.findViewById(R.id.txtCuaRemarks);
        TextView txtTxnStatus = (TextView)rowView.findViewById(R.id.txtTxnStatus);
        ImageView imgTxnStatus = (ImageView) rowView.findViewById(R.id.imgTxnStatus);
        Button btnVoidTransact = (Button)rowView.findViewById(R.id.btnVoidTransact);

        final CustomerActivityListResource customerActivityListResource = objects.get(position);

        String cuaDate = customerActivityListResource.getCuaDate();
        String cuaTime = customerActivityListResource.getCuaTime();

        // Change redemption date format
        try{

            final String OLD_FORMAT = "yyyy-MM-dd";
            final String NEW_FORMAT = "dd MMM yyyy";

            String oldDateString = cuaDate;
            String newDateFormat = "";

            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            newDateFormat = sdf.format(d);

            // convert time 24 hours to 12 hours
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(cuaTime);
            String time = _12HourSDF.format(_24HourDt);

            // Set text view in the list view
            txtCuaDate.setText(newDateFormat+" | "+time);

        }catch (ParseException e){

            e.printStackTrace();
        }

        txtCuaMobileNo.setText(customerActivityListResource.getCuaLoyaltyId());
        txtCuaMerchantName.setText("Demo Merchant");
        txtCuaRemarks.setText(customerActivityListResource.getCuaRemarks());

        String activityType = customerActivityListResource.getCuaActivityType();

        String  activityTypeText = getActivityTypeText(activityType);

        txtCuaActivityType.setText(activityTypeText);

        return rowView;
    }

    /**
     * Method to get the activity type in text
     * @param activityType pass type in integer
     * @return return type in string
     */
    public String getActivityTypeText(String activityType){

        String activityTypeInText = "";

        if(activityType.equals(String.valueOf(ActivityType.REGISTER))){

            activityTypeInText = "Register";

        }else if(activityType.equals(String.valueOf(ActivityType.EARNING))){

            activityTypeInText = "Earning";

        }else if(activityType.equals(String.valueOf(ActivityType.REDEMPTION))){

            activityTypeInText = "Redemption";

        }else if(activityType.equals(String.valueOf(ActivityType.TRANSFER_POINT))){

            activityTypeInText = "Transfer Point";

        }else if(activityType.equals(String.valueOf(ActivityType.BUY_POINT))){

            activityTypeInText = "Buy Point";

        }else if(activityType.equals(String.valueOf(ActivityType.POINT_ENQUIRY))){

            activityTypeInText = "Points Enquiry";

        }else if(activityType.equals(String.valueOf(ActivityType.REDEMPTION_ENQUIRY))){

            activityTypeInText = "Redemption Enquiry";

        }else if(activityType.equals(String.valueOf(ActivityType.EVENT_REGISTER))){

            activityTypeInText = "Event Register";

        }else if(activityType.equals(String.valueOf(ActivityType.UNREGISTER))){

            activityTypeInText = "Unregister";

        }else if(activityType.equals(String.valueOf(ActivityType.ACCOUNT_LINKING))){

            activityTypeInText = "Account Linking";

        }else if(activityType.equals(String.valueOf(ActivityType.POINT_DEDUCTION))){

            activityTypeInText = "Point Deduction";

        }else if(activityType.equals(String.valueOf(ActivityType.TRANSFER_ACCOUNT))){

            activityTypeInText = "Transfer Account";

        }else if(activityType.equals(String.valueOf(ActivityType.REWARD_EXPIRY))){

            activityTypeInText = "Reward Expiry";

        }else if(activityType.equals(String.valueOf(ActivityType.TIER_UPGRADE))){

            activityTypeInText = "Tier Upgrade";

        }else if(activityType.equals(String.valueOf(ActivityType.TIER_DOWNGRADE))){

            activityTypeInText = "Tier Downgrade";

        }else if(activityType.equals(String.valueOf(ActivityType.CASH_BACK_REDEMPTION))){

            activityTypeInText = "Cash back Redemption";

        }else if(activityType.equals(String.valueOf(ActivityType.CASHBACK))){

            activityTypeInText = "Cash back";

        }

        return activityTypeInText;
    }
}
