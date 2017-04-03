package com.inspirenetz.app.proapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.catalogues.CataloguesListFragment;
import com.inspirenetz.app.proapp.general.ApplicationConfiguration;
import com.inspirenetz.app.proapp.resources.CatalogueResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by raju on 2/16/17.
 */

public class CatalogueListRecyclerViewAdapter extends RecyclerView.Adapter<CatalogueListRecyclerViewAdapter.MyViewHolder> implements Filterable{

    private Context mContext;
    private List<CatalogueResource> catalogueResources;
    private CataloguesListFragment cataloguesListFragment;

    private CustomFilter customFilter;

    //inner class for recycler view holder to get the view
    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgCatalogueThumbnail;
        private TextView lblCatalogueName;

        //constructor for get the view
        public MyViewHolder(View view){
            super(view);

            //get promotion thumbnail image view
            imgCatalogueThumbnail = (ImageView)view.findViewById(R.id.imgCatalogueThumbnail);

            //get promotion name text view
            lblCatalogueName = (TextView)view.findViewById(R.id.lblCatalogueName);
        }
    }

    public CatalogueListRecyclerViewAdapter(Context mContext,  int textViewResourceId, List<CatalogueResource> catalogueResources, CataloguesListFragment cataloguesListFragment){

        this.mContext = mContext;
        this.catalogueResources = catalogueResources;
        this.cataloguesListFragment = cataloguesListFragment;
        //this.customFilter = new CustomFilter(CatalogueListRecyclerViewAdapter.this);
    }

    @Override
    public CatalogueListRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_of_catalogues_recycler_view, parent, false);

        return new CatalogueListRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //get the position from the list
        final CatalogueResource catalogueResource = catalogueResources.get(position);

        //set all the values from the api response
        catalogueResource.setCatCategory(catalogueResource.getCatCategory());
        catalogueResource.setCatCategoryName(catalogueResource.getCatCategoryName());
        catalogueResource.setCatNumPoints(catalogueResource.getCatNumPoints());
        catalogueResource.setCatEndDate(catalogueResource.getCatEndDate());
        catalogueResource.setRwdCurrencyName(catalogueResource.getRwdCurrencyName());
        catalogueResource.setMerMerchantName(catalogueResource.getMerMerchantName());
        catalogueResource.setCatProductNo(catalogueResource.getCatProductNo());
        catalogueResource.setCatProductCode(catalogueResource.getCatProductCode());

        //set the values to ui
        holder.lblCatalogueName.setText(catalogueResource.getCatDescription());

        final String catalogueImagePath = ApplicationConfiguration.MERCHANT_PROMOTIONS_IMAGE_PATH + catalogueResource.getCatProductImagePath();

        try{

            //loading catalogue images using glide library
            Glide.with(mContext).load(catalogueImagePath).into(holder.imgCatalogueThumbnail);

        }catch (Exception e){

            e.printStackTrace();
        }

        holder.imgCatalogueThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cataloguesListFragment.moveToFragmentCatalogueDetailsView(catalogueResource);
            }
        });

    }

    @Override
    public int getItemCount() {

        return catalogueResources.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults results = new FilterResults();
                // If there's nothing to filter on, return the original data for
                // your list

                if (charSequence == null || charSequence.length() == 0) {

                    results.values = catalogueResources;
                    results.count = catalogueResources.size();

                } else {
                    List<CatalogueResource> filterResultsData = new ArrayList<CatalogueResource>();

                    // if search details is 0, search fullName, else, search
                    // all details

                        for (CatalogueResource catalogueResource : catalogueResources) {
                            if (catalogueResource.getCatDescription().toLowerCase(Locale.ENGLISH)
                                    .contains(charSequence)) {
                                filterResultsData.add(catalogueResource);
                            }
                        }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                catalogueResources = (List<CatalogueResource>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public List<CatalogueResource> getFilteredResults(String constraint) {

        List<CatalogueResource> results = new ArrayList<CatalogueResource>();

        for (CatalogueResource catalogueResource : catalogueResources) {

            if (catalogueResource.getCatDescription().toLowerCase().contains(constraint)) {

                results.add(catalogueResource);
            }
        }
        return results;
    }
}
