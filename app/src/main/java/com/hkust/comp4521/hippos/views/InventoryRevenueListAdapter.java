package com.hkust.comp4521.hippos.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hkust.comp4521.hippos.R;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.datastructures.InventoryRevenue;
import com.hkust.comp4521.hippos.utils.ImageRetriever;

import java.util.List;

public class InventoryRevenueListAdapter extends RecyclerView.Adapter<InventoryRevenueListAdapter.InventoryViewHolder>{

        private Context mContext;
        private List<InventoryRevenue> invList;

        public InventoryRevenueListAdapter(Context pCon, List<InventoryRevenue> pList) {
            this.mContext = pCon;
            this.invList = pList;
        }
         
        @Override
        public int getItemCount() {
            return invList.size();
        }

        public double getInventoryRevenueTotal() {
            // Add up total prices from each of the invoice inventory
            double price = 0;
            for(InventoryRevenue inv : invList) {
                price += inv.revenue;
            }
            return price;
        }

        @Override
        public void onBindViewHolder(InventoryViewHolder contactViewHolder, int i) {
             InventoryRevenue cii = invList.get(i);
             Inventory ci = Commons.getInventory(cii.id);
             contactViewHolder.itemName.setText(ci.getName());
             contactViewHolder.itemPrice.setText(cii.getFormattedRevenue());
             contactViewHolder.itemStock.setText("x" + cii.getQuantity());
             new ImageRetriever(contactViewHolder.heroImage, ci.getImage(), mContext.getResources().getDrawable(R.mipmap.placeholder)).execute();
        }

        @Override
        public InventoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).
            inflate(R.layout.item_sales_details, viewGroup, false);
            return new InventoryViewHolder(itemView);
        }

        public static class InventoryViewHolder extends RecyclerView.ViewHolder {
                protected TextView itemName;
                protected TextView itemPrice;
                protected TextView itemStock;
                protected ImageView heroImage;

                public InventoryViewHolder(View v) {
                    super(v);
                    itemName =  (TextView) v.findViewById(R.id.tv_inventory_item_name);
                    itemPrice = (TextView)  v.findViewById(R.id.tv_card_inventory_item_price);
                    itemStock = (TextView) v.findViewById(R.id.tv_card_inventory_item_stock);
                    heroImage = (ImageView) v.findViewById(R.id.iv_inventory);
                }
        }


}

