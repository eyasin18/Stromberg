package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.repictures.stromberg.R;

public class FeaturesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private ClickListener clickListener;
    public TextView feature;
    public RelativeLayout listLayout;

    public FeaturesListViewHolder(View itemView) {
        super(itemView);
        feature = (TextView) itemView.findViewById(R.id.feature);
        listLayout = (RelativeLayout) itemView.findViewById(R.id.feature_layout);
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
