package com.inspirenetz.app.proapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.promotions.PromotionListFragment;
import com.inspirenetz.app.proapp.resources.PromotionListResource;

import java.util.List;

/**
 * Created by raju on 2/4/17.
 */

public class PromotionListRecyclerViewAdapter extends RecyclerView.Adapter<PromotionListRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<PromotionListResource> promotionList;
    private PromotionListFragment promotionListFragment;


    //inner class for recycler view holder to get the view
    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgPromotionThumbnail;
        private TextView lblPromotionName;

        //constructor for get the view
        public MyViewHolder(View view){
            super(view);

            //get promotion thumbnail image view
            imgPromotionThumbnail = (ImageView)view.findViewById(R.id.imgPromotionThumbnail);

            //get promotion name text view
            lblPromotionName = (TextView)view.findViewById(R.id.lblPromotionName);
        }
    }

    public PromotionListRecyclerViewAdapter(Context mContext, List<PromotionListResource> promotionList, PromotionListFragment promotionListFragment){

        this.mContext = mContext;
        this.promotionList = promotionList;
        this.promotionListFragment = promotionListFragment;
    }


    @Override
    public PromotionListRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_of_promotions, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PromotionListRecyclerViewAdapter.MyViewHolder holder, final int position) {

        //get the position from the list
        final PromotionListResource promotionListResource = promotionList.get(position);

        //get all promotion values from the api response
        /*String promotionId = promotionListResource.getPrmId();
        String prmMerchantNo = promotionListResource.getPrmMerchantNo();*/
        final String promotionName = promotionListResource.getPrmName();
        /*String prmShortDescription = promotionListResource.getPrmShortDescription();
        String prmLongDescription = promotionListResource.getPrmLongDescription();
        String prmMoreDetails = promotionListResource.getPrmMoreDetails();
        String prmExpiryDate = promotionListResource.getPrmExpiryDate();
        String prmClaimExpiryDays = promotionListResource.getPrmClaimExpiryDays();*/

        //set all promotion values from the api response
        promotionListResource.setPrmId(promotionListResource.getPrmId());
        promotionListResource.setPrmMerchantNo(promotionListResource.getPrmMerchantNo());
        promotionListResource.setPrmName(promotionListResource.getPrmName());
        promotionListResource.setPrmShortDescription(promotionListResource.getPrmShortDescription());
        promotionListResource.setPrmLongDescription(promotionListResource.getPrmLongDescription());
        promotionListResource.setPrmMoreDetails(promotionListResource.getPrmMoreDetails());
        promotionListResource.setPrmExpiryDate(promotionListResource.getPrmExpiryDate());
        promotionListResource.setPrmClaimExpiryDays(promotionListResource.getPrmClaimExpiryDays());

        //set promotion name to the text view
        holder.lblPromotionName.setText(promotionName);

        final String promotionImagePath = ApplicationConfiguration.MERCHANT_PROMOTIONS_IMAGE_PATH + promotionListResource.getPrmImagePath();

        try{

            //loading promotion images using glide library
            Glide.with(mContext).load(promotionImagePath).into(holder.imgPromotionThumbnail);

        }catch (Exception e){

            e.printStackTrace();
        }


        holder.imgPromotionThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                promotionListFragment.moveToFragmentPromotionDetailsView(promotionListResource);
            }
        });


    }

    @Override
    public int getItemCount() {

        return promotionList.size();
    }
}
