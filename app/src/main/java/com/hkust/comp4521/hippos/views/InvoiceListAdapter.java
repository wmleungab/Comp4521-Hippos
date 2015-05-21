package com.hkust.comp4521.hippos.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hkust.comp4521.hippos.R;
import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Invoice;

import java.util.List;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.InvoiceViewHolder>{

        private Context mContext;
        private int mode = -1;
        private List<Invoice> invList;
        private OnInvoiceClickListener mOnClickListener;

        public InvoiceListAdapter(Context pCon) {
            this.mContext = pCon;
            this.invList = Commons.getInvoiceList();
        }
         
        @Override
        public int getItemCount() {
            return invList.size();
        }

        @Override
        public void onBindViewHolder(InvoiceViewHolder contactViewHolder, int i) {
             Invoice ci = invList.get(i);
             contactViewHolder.itemName.setText(mContext.getString(R.string.invoice_id_text) + ci.getId());
             contactViewHolder.itemPrice.setText(ci.getFinalPrice()+"");
             contactViewHolder.itemStock.setText(ci.getDateTime());
             contactViewHolder.invIndex = i;
             contactViewHolder.mListener = mOnClickListener;
        }

        @Override
        public InvoiceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).
            inflate(R.layout.card_invoice, viewGroup, false);
            return new InvoiceViewHolder(itemView);
        }

        public void setOnClickListener(OnInvoiceClickListener ocl) {
            mOnClickListener = ocl;
        }

        // Interface for onClick function in the adapter
        public interface OnInvoiceClickListener {
            public void onClick(View v, int invIndex);
        }

        public static class InvoiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
                protected TextView itemName;
                protected TextView itemPrice;
                protected TextView itemStock;

                // Info used for referring in OnClick method
                protected int invIndex;
                protected OnInvoiceClickListener mListener;

                public InvoiceViewHolder(View v) {
                    super(v);
                    invIndex = -1;
                    itemName =  (TextView) v.findViewById(R.id.tv_invoice_id);
                    itemPrice = (TextView)  v.findViewById(R.id.tv_invoice_name);
                    itemStock = (TextView) v.findViewById(R.id.tv_invoice_datetime);
                    v.setOnClickListener(this);
                }

                @Override
                public void onClick(View v) {
                    // trigger on click method from delegated listener
                    mListener.onClick(v, invIndex);
                }
        }

}

