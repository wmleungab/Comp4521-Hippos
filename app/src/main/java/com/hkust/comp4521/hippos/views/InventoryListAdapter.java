package com.hkust.comp4521.hippos.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hkust.comp4521.hippos.InventoryDetailsActivity;
import com.hkust.comp4521.hippos.R;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;

import java.util.List;

public class InventoryListAdapter extends ImageListBaseAdapter<InventoryListAdapter.InventoryViewHolder>{


        private int categoryId, catIndex;
        private Context mContext;
        private List<Inventory> invList;
        private OnInventoryClickListener mOnClickListener;

        public InventoryListAdapter(Context pCon, int cId, int cIdx) {
            this.mContext = pCon;
            this.categoryId = cId;
            this.catIndex = cIdx;
            this.invList = Commons.getInventoryList(cId);
        }
         
        @Override
        public int getItemCount() {
            return invList.size();
        }

        public int getCategoryId() {
        return categoryId;
    }

        @Override
        public void onBindViewHolder(InventoryViewHolder contactViewHolder, int i) {
             Inventory ci = invList.get(i);
             ci.setIndex(catIndex, i);
             if(ci.getStatus() == Inventory.INVENTORY_DISABLED) {
                 contactViewHolder.itemName.setText(ci.getName() + mContext.getString(R.string.inventory_details_disabled));
             } else {
                 contactViewHolder.itemName.setText(ci.getName());
                 contactViewHolder.mListener = mOnClickListener;
             }
             contactViewHolder.itemPrice.setText(ci.getFormattedPrice());
             contactViewHolder.itemStock.setText(mContext.getString(R.string.stock) + ci.getStock());
             contactViewHolder.catId = categoryId;
             contactViewHolder.invId = i;
             setBitmapToView(mContext, ci, contactViewHolder.heroImage, 0);
             //new ImageRetriever(contactViewHolder.heroImage, ci.getImage(), mContext.getResources().getDrawable(R.mipmap.placeholder)).execute();
        }

        public void removeImageCache(String key) {
            mMemoryCache.remove(key);
        }

        public void setOnClickListener(OnInventoryClickListener ocl) {
            mOnClickListener = ocl;
        }

        @Override
        public InventoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).
            inflate(R.layout.card_inventory, viewGroup, false);
            return new InventoryViewHolder(itemView);
        }

        public void setInventoryList(List<Inventory> pInvList) {
            invList = pInvList;
        }

        public List<Inventory> getInventoryList() {
            return invList;
        }

        // Interface for onClick function in the adapter
        public interface OnInventoryClickListener {
            public void onClick(View v, int cId, int invIndex);
        }

        public static class InventoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
                protected TextView itemName;
                protected TextView itemPrice;
                protected TextView itemStock;
                protected ImageView heroImage;

                // Info used for referring in OnClick method
                protected int catId;
                protected int invId;
                protected OnInventoryClickListener mListener;

                public InventoryViewHolder(View v) {
                    super(v);
                    catId = -1;
                    invId = -1;
                    itemName =  (TextView) v.findViewById(R.id.tv_inventory_item_name);
                    itemPrice = (TextView)  v.findViewById(R.id.tv_card_inventory_item_price);
                    itemStock = (TextView) v.findViewById(R.id.tv_card_inventory_item_stock);
                    heroImage = (ImageView) v.findViewById(R.id.iv_inventory);
                    v.setOnClickListener(this);
                }

                @Override
                public void onClick(View v) {
                    // trigger on click method from delegated listener
                    if(mListener != null)
                        mListener.onClick(v, catId, invId);
                    // Hook drawable to InventoryDetailsActivity, otherwise transition cannot be done smoothly (Due to asynchronized operation)
                    InventoryDetailsActivity.heroImageDrawable = heroImage.getDrawable();
                }
        }

}

