package com.hkust.comp4521.hippos.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hkust.comp4521.hippos.R;
import com.hkust.comp4521.hippos.datastructures.Inventory;
import com.hkust.comp4521.hippos.datastructures.InvoiceInventory;
import com.hkust.comp4521.hippos.utils.ImageRetriever;

import java.util.ArrayList;
import java.util.List;

public class InvoiceInventoryListAdapter extends RecyclerView.Adapter<InvoiceInventoryListAdapter.InvoiceInventoryViewHolder>{

        private Context mContext;
        private int mode = -1;
        private List<InvoiceInventory> invList;

        public InvoiceInventoryListAdapter(Context pCon, int mode) {
            this.mContext = pCon;
            this.invList = new ArrayList<InvoiceInventory>();
            this.mode = mode;
        }
         
        @Override
        public int getItemCount() {
            return invList.size();
        }

        public void addItem(InvoiceInventory mInv) {
            invList.add(mInv);
            this.notifyItemInserted(invList.size() - 1);
        }

        @Override
        public void onBindViewHolder(InvoiceInventoryViewHolder contactViewHolder, int i) {
             Inventory ci = invList.get(i).getInventory();
             contactViewHolder.itemName.setText(ci.getName());
             contactViewHolder.itemPrice.setText(ci.getFormattedPrice());
             contactViewHolder.itemStock.setText("x1");
             contactViewHolder.invId = i;
             new ImageRetriever(contactViewHolder.heroImage, ci.getImage()).execute();
        }

        @Override
        public InvoiceInventoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).
            inflate(R.layout.card_invoice_inventory, viewGroup, false);
            return new InvoiceInventoryViewHolder(itemView);
        }

        public static class InvoiceInventoryViewHolder extends RecyclerView.ViewHolder {
                protected TextView itemName;
                protected TextView itemPrice;
                protected TextView itemStock;
                protected ImageView heroImage;
                protected ImageButton btnAdd, btnSubtract;

                // Info used for referring in OnClick method
                protected int invId;

                public InvoiceInventoryViewHolder(View v) {
                    super(v);
                    invId = -1;
                    itemName =  (TextView) v.findViewById(R.id.tv_inventory_item_name);
                    itemPrice = (TextView)  v.findViewById(R.id.tv_card_inventory_item_price);
                    itemStock = (TextView) v.findViewById(R.id.tv_card_inventory_item_stock);
                    heroImage = (ImageView) v.findViewById(R.id.iv_inventory);
                    btnAdd = (ImageButton) v.findViewById(R.id.ib_invoice_inventory_add);
                    btnSubtract = (ImageButton) v.findViewById(R.id.ib_invoice_inventory_add);
                }
        }

}

