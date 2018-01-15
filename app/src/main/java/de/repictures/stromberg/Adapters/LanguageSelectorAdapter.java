package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.repictures.stromberg.Fragments.SelectLanguageDialogFragment;
import de.repictures.stromberg.R;

public class LanguageSelectorAdapter  extends RecyclerView.Adapter<LanguageSelectorViewHolder> implements FeaturesListViewHolder.ClickListener {

    private static final String TAG = "TransferListAdapter";
    private SelectLanguageDialogFragment selectLanguageDialogFragment;
    private String[] languagesArray;

    public LanguageSelectorAdapter(SelectLanguageDialogFragment selectLanguageDialogFragment) {
        this.selectLanguageDialogFragment = selectLanguageDialogFragment;
        languagesArray = selectLanguageDialogFragment.getActivity().getResources().getStringArray(R.array.languages);
    }

    @Override
    public LanguageSelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_selector_list, parent, false);
        return new LanguageSelectorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LanguageSelectorViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: executed");
        holder.setClickListener(this);
        holder.language.setText(languagesArray[position]);
        if (selectLanguageDialogFragment.selectedLanguage == position){
            holder.check.setVisibility(View.VISIBLE);
            holder.language.setTextColor(selectLanguageDialogFragment.getActivity().getResources().getColor(R.color.light_blue));
        } else {
            holder.check.setVisibility(View.INVISIBLE);
            holder.language.setTextColor(selectLanguageDialogFragment.getActivity().getResources().getColor(R.color.grey));
        }
    }

    @Override
    public int getItemCount() {
        return selectLanguageDialogFragment.languages.length;
    }

    @Override
    public void onClick(View v, int position, boolean isLongClick) {
        selectLanguageDialogFragment.selectedLanguage = position;
        notifyDataSetChanged();
    }
}