package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.repictures.stromberg.R;

public class LanguageSelectorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private FeaturesListViewHolder.ClickListener clickListener;
    public TextView language;
    public RelativeLayout listLayout;
    public ImageView check;

    public LanguageSelectorViewHolder(View itemView) {
        super(itemView);
        language = (TextView) itemView.findViewById(R.id.language_selector_text);
        check = (ImageView) itemView.findViewById(R.id.language_selector_check);
        listLayout = (RelativeLayout) itemView.findViewById(R.id.language_selector_layout);
        listLayout.setOnClickListener(this);
        listLayout.setOnLongClickListener(this);
        listLayout.setClickable(true);
        listLayout.setTag(this);
    }

    public interface ClickListener{
        void onClick(View v, int position, boolean isLongClick);
    }

    public void setClickListener(FeaturesListViewHolder.ClickListener clickListener){
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
