package com.inspirenetz.app.proapp.adapter;

import android.widget.Filter;

import com.inspirenetz.app.proapp.resources.CatalogueResource;

import java.util.List;

/**
 * Created by raju on 3/5/17.
 */

public class CustomFilter extends Filter {
    // List of all dictionary words
    private List<CatalogueResource> dictionaryWords;
    private List<CatalogueResource> filteredList;
    private CatalogueListRecyclerViewAdapter catalogueListRecyclerViewAdapter;
    public CustomFilter(CatalogueListRecyclerViewAdapter catalogueListRecyclerViewAdapter) {
        super();
        this.catalogueListRecyclerViewAdapter = catalogueListRecyclerViewAdapter;
    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();
        if (constraint.length() == 0) {
            filteredList.addAll(dictionaryWords);
        } else {
            final String filterPattern = constraint.toString().toLowerCase().trim();
            for (final CatalogueResource catalogueResource : dictionaryWords) {
                if (catalogueResource.getCatDescription().toLowerCase().startsWith(filterPattern)) {
                    filteredList.add(catalogueResource);
                }
            }
        }
        System.out.println("Count Number " + filteredList.size());
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        System.out.println("Count Number 2 " + ((List<CatalogueResource>) results.values).size());
        this.catalogueListRecyclerViewAdapter.notifyDataSetChanged();
    }
}
