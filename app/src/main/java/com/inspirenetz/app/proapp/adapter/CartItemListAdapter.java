package com.inspirenetz.app.proapp.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.catalogues.CatalogueCartItemViewFragment;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.general.GeneralMethods;
import com.inspirenetz.app.proapp.general.SessionManager;
import com.inspirenetz.app.proapp.resources.CatalogueCartItemResource;
import com.inspirenetz.app.proapp.resources.CatalogueResource;
import com.inspirenetz.app.proapp.resources.UserInfoResource;

import java.util.List;

/**
 * Created by raju on 2/28/17.
 */

public class CartItemListAdapter extends ArrayAdapter<CatalogueCartItemResource> {

    private Context context;
    private List<CatalogueCartItemResource> objects;
    private CatalogueCartItemViewFragment catalogueCartItemViewFragment;
    private SessionManager sessionManager;

    private int quantityCount = 0;

    //keep customer mobile number
    private String cusLoyaltyId = "";

    //keep total points of the item
    private String totalPointsOfItem = "";

    private GeneralMethods generalMethods = new GeneralMethods();

    public CartItemListAdapter(Context context, int resource, List<CatalogueCartItemResource> objects, CatalogueCartItemViewFragment catalogueCartItemViewFragment) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        this.catalogueCartItemViewFragment = catalogueCartItemViewFragment;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Initialize the view
        @SuppressLint("ViewHolder") final View rowView = inflater.inflate(R.layout.content_of_cart_items_list, parent, false);

        sessionManager = new SessionManager(context);

        // Initialize content of the list components
        TextView txtCattDetails = (TextView)rowView.findViewById(R.id.txtCattDetails);
        final TextView txtCartQuantity = (TextView)rowView.findViewById(R.id.txtCartQuantity);
        TextView txtPoints = (TextView)rowView.findViewById(R.id.txtPoints);
        TextView lblItemQuantity = (TextView)rowView.findViewById(R.id.lblItemQuantity);
        TextView txtProcessedItemQuantity = (TextView)rowView.findViewById(R.id.txtProcessedItemQuantity);
        final TextView txtTotalPoints = (TextView)rowView.findViewById(R.id.txtTotalPoints);
        ImageView imgCatImage = (ImageView) rowView.findViewById(R.id.imgCatImage);
        ImageView imgRemoveItem = (ImageView) rowView.findViewById(R.id.imgRemoveItem);
        ImageView imgCartDecrease = (ImageView) rowView.findViewById(R.id.imgCartDecrease);
        ImageView imgCartIncrease = (ImageView) rowView.findViewById(R.id.imgCartIncrease);
        ImageView imgStatusSuccess = (ImageView) rowView.findViewById(R.id.imgStatusSuccess);
        ImageView imgStatusFailed = (ImageView) rowView.findViewById(R.id.imgStatusFailed);

        final CatalogueCartItemResource catalogueCartItemResource = objects.get(position);

        String status = catalogueCartItemResource.getStatus();

        if(!status.equals("available")){

            imgCartDecrease.setVisibility(View.GONE);
            imgCartIncrease.setVisibility(View.GONE);
            txtCartQuantity.setVisibility(View.GONE);
            lblItemQuantity.setVisibility(View.VISIBLE);
            txtProcessedItemQuantity.setVisibility(View.VISIBLE);

            if (status.equals("success")){

                txtProcessedItemQuantity.setText(String.valueOf(catalogueCartItemResource.getQty()));
                imgRemoveItem.setImageResource(R.drawable.ic_status_success);

            }else {

                txtProcessedItemQuantity.setText(String.valueOf(catalogueCartItemResource.getQty()));
                imgRemoveItem.setImageResource(R.drawable.ic_status_failed);
            }

        }

        final CatalogueResource catalogueResource = catalogueCartItemResource.getCatalogueResource();

        //get the product details to single string
        String productDetails = catalogueResource.getCatProductCode()+" "+ catalogueResource.getCatDescription()
                +" "+ catalogueResource.getMerMerchantName()+" in "+ catalogueResource.getCatCategoryName();

        //get the image path
        final String catalogueImagePath = ApplicationConfiguration.MERCHANT_PROMOTIONS_IMAGE_PATH + catalogueResource.getCatProductImagePath();

        try{

            //loading promotion images using glide library
            Glide.with(context).load(catalogueImagePath).into(imgCatImage);

        }catch (Exception e){

            e.printStackTrace();
        }

        //set the product details to single text view
        txtCattDetails.setText(productDetails);

        //set the points
        txtPoints.setText(catalogueResource.getCatNumPoints());

        //set the cart item quantity
        txtCartQuantity.setText(String.valueOf(catalogueCartItemResource.getQty()));

        final int itemQuantity = Integer.parseInt(txtCartQuantity.getText().toString());
        final int itemPointsRequired = Integer.parseInt(catalogueResource.getCatNumPoints());

        //set total points initially
        txtTotalPoints.setText(String.valueOf(itemQuantity * itemPointsRequired));

        UserInfoResource userInfoResource = sessionManager.getUserInfo();
        cusLoyaltyId = userInfoResource.getUsrLoginId();

        imgCartDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int decreasedItemQuantity = Integer.parseInt(txtCartQuantity.getText().toString());

                if(decreasedItemQuantity==1){

                    generalMethods.showToastMessage(context, "Minimum 1 quantity");

                }else {

                    decreasedItemQuantity-=1;

                    //set decrease count
                    txtCartQuantity.setText(String.valueOf(decreasedItemQuantity));

                    //set total points
                    txtTotalPoints.setText(String.valueOf(decreasedItemQuantity * itemPointsRequired));

                    totalPointsOfItem = txtTotalPoints.getText().toString();

                    catalogueCartItemViewFragment.decreaseCartItemQuantity(cusLoyaltyId, catalogueResource, itemPointsRequired);
                }
            }
        });

        imgCartIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int increasedItemQuantity = Integer.parseInt(txtCartQuantity.getText().toString());

                increasedItemQuantity+=1;

                //set increase count
                txtCartQuantity.setText(String.valueOf(increasedItemQuantity));

                //set total points
                txtTotalPoints.setText(String.valueOf(increasedItemQuantity * itemPointsRequired));

                totalPointsOfItem = txtTotalPoints.getText().toString();

                catalogueCartItemViewFragment.increaseCartItemQuantity(cusLoyaltyId, catalogueResource, itemPointsRequired);
            }
        });

        imgRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                totalPointsOfItem = txtTotalPoints.getText().toString();

                showErrorPopupMessage(context, catalogueResource, totalPointsOfItem);
            }
        });

        return rowView;
    }

    /**
     * Method to show the alert message
     * when click the cart item to remove
     * open with a new alert window
     * @param context application context
     */
    public void showErrorPopupMessage(Context context, final CatalogueResource catalogueResource, final String totalPointsOfItem){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("Remove cart item...");

        // Setting Dialog Message
        alertDialog.setMessage("Are sure want to remove this item?");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_status_failed);

        // Setting OK Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                catalogueCartItemViewFragment.removeCartItem(cusLoyaltyId, catalogueResource, "removeItem", totalPointsOfItem);
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                //Toast.makeText(context, "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        AlertDialog dialog = alertDialog.create();
        dialog.show();

    }
}
