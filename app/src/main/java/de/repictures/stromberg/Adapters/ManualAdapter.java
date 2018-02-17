package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.repictures.stromberg.Fragments.SimpleTextBodyDialogFragment;
import de.repictures.stromberg.ManualActivity;
import de.repictures.stromberg.R;

public class ManualAdapter extends RecyclerView.Adapter<ManualAdapter.ViewHolder>{

    private ManualActivity manualActivity;
    private String[] manualsDescriptions;
    private String[] manuals;

    public ManualAdapter(ManualActivity manualActivity){
        this.manualActivity = manualActivity;
        this.manualsDescriptions = manualActivity.getResources().getStringArray(R.array.anleitungen_short);
        this.manuals = manualActivity.getResources().getStringArray(R.array.anleitungen);
    }

    @Override
    public ManualAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.featureslist, parent, false);
        return new ManualAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ManualAdapter.ViewHolder holder, int position) {
        holder.manualTitleText.setText(manualsDescriptions[position]);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleTextBodyDialogFragment fragment = new SimpleTextBodyDialogFragment();
                fragment.setTitle(manualsDescriptions[position]);
                fragment.setTextBody(manuals[position]);
                fragment.show(manualActivity.getSupportFragmentManager(), "sesf");
            }
        });
    }

    @Override
    public int getItemCount() {
        return manualsDescriptions.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView manualTitleText;
        RelativeLayout layout;

        public ViewHolder(View v) {
            super(v);
            manualTitleText = (TextView) v.findViewById(R.id.feature);
            layout = (RelativeLayout) v.findViewById(R.id.feature_layout);
        }
    }
}