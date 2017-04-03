package com.inspirenetz.app.proapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inspirenetz.app.proapp.R;
import com.inspirenetz.app.proapp.catalogues.CataloguesListFragment;
import com.inspirenetz.app.proapp.dictionary.CatalogueCategoryType;

import java.util.HashMap;
import java.util.List;

/**
 * Created by raju on 3/6/17.
 */

public class CatalogueCategoryRecyclerViewAdapter extends RecyclerView.Adapter<CatalogueCategoryRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<String> catCategoryList;
    private CataloguesListFragment cataloguesListFragment;
    int category = 0;

    //inner class for recycler view holder to get the view
    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgCatCategoryThumbnail;
        private TextView lblCatCategoryName;

        //constructor for get the view
        public MyViewHolder(View view){
            super(view);

            //get promotion thumbnail image view
            imgCatCategoryThumbnail = (ImageView)view.findViewById(R.id.imgCatCategoryThumbnail);

            //get promotion name text view
            lblCatCategoryName = (TextView)view.findViewById(R.id.lblCatCategoryName);
        }
    }

    public CatalogueCategoryRecyclerViewAdapter(Context context, List<String> catCategoryList, CataloguesListFragment cataloguesListFragment){
        this.context = context;
        this.catCategoryList = catCategoryList;
        this.cataloguesListFragment = cataloguesListFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catalogue_categories, parent, false);

        return new CatalogueCategoryRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final String categoryName = catCategoryList.get(position);

        switch (categoryName) {
            case "Dining":

                holder.imgCatCategoryThumbnail.setImageResource(R.drawable.cat_dining);
                holder.lblCatCategoryName.setText(categoryName);

                break;
            case "Electronic and Gadgets":

                holder.imgCatCategoryThumbnail.setImageResource(R.drawable.cat_electronics_gadgets);
                holder.lblCatCategoryName.setText(categoryName);

                break;
            case "Food and Beverages":

                holder.imgCatCategoryThumbnail.setImageResource(R.drawable.cat_food_and_beverages);
                holder.lblCatCategoryName.setText(categoryName);

                break;
            case "Health and beauty":

                holder.imgCatCategoryThumbnail.setImageResource(R.drawable.cat_health_and_beauty);
                holder.lblCatCategoryName.setText(categoryName);

                break;

            case "Leisure and Entertainment":

                holder.imgCatCategoryThumbnail.setImageResource(R.drawable.cat_leisure);
                holder.lblCatCategoryName.setText(categoryName);

                break;
            case "Travel and Holidays":

                holder.imgCatCategoryThumbnail.setImageResource(R.drawable.cat_travel_and_holidays);
                holder.lblCatCategoryName.setText(categoryName);

                break;
            case "Shopping":

                holder.imgCatCategoryThumbnail.setImageResource(R.drawable.cat_shopping);
                holder.lblCatCategoryName.setText(categoryName);

                break;
        }


        //category click listener
        holder.imgCatCategoryThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //call the method to the category type in int
                int catType = getCatType(categoryName);

                //call the method to get the catalogue list by category
                cataloguesListFragment.getCatalogueListByCategory(catType, categoryName);
            }
        });

    }

    @Override
    public int getItemCount() {
        return catCategoryList.size();
    }

    /**
     * Method to get the category name type in int
     * @param categoryName category name to get the category type
     * @return catype in int
     */
    public Integer getCatType(String categoryName){

        int catType = 0;

        switch (categoryName) {
            case "Dining":
                catType = CatalogueCategoryType.CAT_TYPE_DINING;

                break;
            case "Electronic and Gadgets":


                catType = CatalogueCategoryType.CAT_TYPE_ELECTRONICS_AND_GADGETS;

                break;
            case "Food and Beverages":


                catType = CatalogueCategoryType.CAT_TYPE_FOOD_AND_BEVERAGES;

                break;
            case "Health and beauty":


                catType = CatalogueCategoryType.CAT_TYPE_HEALTH_AND_BEAUTY;

                break;

            case "Leisure and Entertainment":


                catType = CatalogueCategoryType.CAT_TYPE_LEISURE_AND_ENTERTAINMENT;

                break;
            case "Travel and Holidays":

                catType = CatalogueCategoryType.CAT_TYPE_TRAVEL_AND_HOLIDAYS;

                break;
            case "Shopping":


                catType = CatalogueCategoryType.CAT_TYPE_SHOPPING;

                break;
        }

        return catType;
    }
}
