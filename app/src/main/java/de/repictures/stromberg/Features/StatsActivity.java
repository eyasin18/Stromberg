package de.repictures.stromberg.Features;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.GetStatsAsyncTask;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.R;
import de.repictures.stromberg.uiHelper.BalanceDevelopmentTimeFormatter;
import de.repictures.stromberg.uiHelper.BalanceDevelopmentValueFormatter;

public class StatsActivity extends AppCompatActivity implements OnChartValueSelectedListener, View.OnClickListener {

    @BindView(R.id.stats_balance_development_chart) LineChart balanceDevelopmentChart;
    @BindView(R.id.stats_balance_selected_value) TextView selectedValue;
    @BindView(R.id.toolbar_progress_bar) ProgressBar toolbarProgressBar;
    @BindView(R.id.toolbar_image_view) ImageView toolbarReloadImageView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private List<Entry> entries = new ArrayList<>();
    private DecimalFormat stromerFormat = new DecimalFormat("0.00");
    private int companyPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.statistics);
        setSupportActionBar(toolbar);

        companyPosition = getIntent().getIntExtra("company_array_position", 0);

        toolbarReloadImageView.setOnClickListener(this);
        toolbarReloadImageView.setVisibility(View.INVISIBLE);
        toolbarProgressBar.setVisibility(View.VISIBLE);

        XAxis xAxis = balanceDevelopmentChart.getXAxis();
        xAxis.setLabelCount(3, false);
        xAxis.setValueFormatter(new BalanceDevelopmentTimeFormatter());
        YAxis leftYAxis = balanceDevelopmentChart.getAxisLeft();
        leftYAxis.setLabelCount(3, false);
        leftYAxis.setValueFormatter(new BalanceDevelopmentValueFormatter());
        balanceDevelopmentChart.setTouchEnabled(true);
        balanceDevelopmentChart.getAxisRight().setEnabled(false);
        balanceDevelopmentChart.setDrawMarkers(true);
        balanceDevelopmentChart.setOnChartValueSelectedListener(this);
        balanceDevelopmentChart.getDescription().setEnabled(false);

        GetStatsAsyncTask asyncTask = new GetStatsAsyncTask(StatsActivity.this);
        asyncTask.execute(companyPosition);
    }

    public void setBalanceDevelopment(int[] times, double[] values){
        entries.clear();
        for (int i = 0; i < times.length; i++){
            entries.add(new Entry(times[i], (float) values[i]));
        }
    }

    public void updateData(){
        toolbarReloadImageView.setVisibility(View.VISIBLE);
        toolbarProgressBar.setVisibility(View.INVISIBLE);
        if (entries.size() < 1){
            return;
        }
        LineDataSet balanceDevelopmentDataSet = new LineDataSet(entries, getResources().getString(R.string.balance));
        balanceDevelopmentDataSet.setColor(getResources().getColor(R.color.light_blue));
        balanceDevelopmentDataSet.setDrawFilled(true);
        balanceDevelopmentDataSet.setFillColor(getResources().getColor(R.color.light_blue));
        balanceDevelopmentDataSet.setLineWidth(2);
        balanceDevelopmentDataSet.setHighlightLineWidth(1);
        balanceDevelopmentDataSet.setHighlightEnabled(true);
        balanceDevelopmentDataSet.setValueTextSize(8);
        balanceDevelopmentDataSet.setDrawValues(false);
        balanceDevelopmentDataSet.setHighlightEnabled(true);
        LineData balanceDevelopmentData = new LineData(balanceDevelopmentDataSet);

        balanceDevelopmentChart.setData(balanceDevelopmentData);
        balanceDevelopmentData.notifyDataChanged();
        balanceDevelopmentChart.invalidate();
        balanceDevelopmentChart.highlightValue(entries.get(entries.size()-1).getX(), 0);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        int time = (int) e.getX();
        int days = Account.getDaysFromMinutes(time)+1;
        int hours = Account.getHoursFromMinutes(time);
        int minutes = Account.getMinutesOfHourFromMinutes(time);
        String[] weekdays = getResources().getStringArray(R.array.weekdays);
        String timeStr =  weekdays[days%7] + " " + decimalFormat.format(hours) + ":" + decimalFormat.format(minutes);
        selectedValue.setText(new StringBuilder().append(timeStr)
                .append(getResources().getString(R.string.oclock))
                .append(": ")
                .append(stromerFormat.format(e.getY()))
                .append("S").toString());
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View view) {
        toolbarReloadImageView.setVisibility(View.INVISIBLE);
        toolbarProgressBar.setVisibility(View.VISIBLE);
        GetStatsAsyncTask asyncTask = new GetStatsAsyncTask(StatsActivity.this);
        asyncTask.execute(companyPosition);
    }
}
