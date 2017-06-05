package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.repictures.stromberg.R;

public class TransferListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private ClickListener clickListener;
    public TextView transferDay, transferTime, tranferAmountCents, transferAmountEuros, transferCompanyName, transferType;
    public RelativeLayout listLayout;

    public TransferListViewHolder(View itemView) {
        super(itemView);
        transferDay = (TextView) itemView.findViewById(R.id.tranfer_day);
        transferTime = (TextView) itemView.findViewById(R.id.transfer_time);
        tranferAmountCents = (TextView) itemView.findViewById(R.id.tranfer_amount_cents);
        transferAmountEuros = (TextView) itemView.findViewById(R.id.tranfer_amount_euros);
        transferCompanyName = (TextView) itemView.findViewById(R.id.tranfer_company_name);
        transferType = (TextView) itemView.findViewById(R.id.transfer_type);
        listLayout = (RelativeLayout) itemView.findViewById(R.id.transfer_list_layout);
        listLayout.setOnClickListener(this);
        listLayout.setOnLongClickListener(this);
        listLayout.setClickable(true);
        listLayout.setTag(this);
    }

    public interface ClickListener{
        void onClick(View v, int position, boolean isLongClick);
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        clickListener.onClick(view, getLayoutPosition(), false);
    }

    @Override
    public boolean onLongClick(View view) {
        clickListener.onClick(view, getLayoutPosition(), true);
        return true;
    }
}
