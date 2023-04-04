package com.sipl.rfidtagscanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.model.supervisorModel;

import java.util.List;

public class SupervisorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<supervisorModel> modelList;

    public SupervisorAdapter(Context context, List<supervisorModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
            view = LayoutInflater.from(context).inflate(R.layout.bothra_supervisor_item_row_data,
                    parent, false);
            viewHolder = new ViewHolderTableDataRow(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(modelList != null && modelList.size() > 0)
        {
            supervisorModel modeldata = modelList.get(position);
            ViewHolderTableDataRow regularAdapter = (ViewHolderTableDataRow) holder;
            regularAdapter.cellRNG_No.setText(modeldata.getRng_no());
//            regularAdapter.cellRNG_No.setTextSize();
            regularAdapter.cellRNG_No.setTextColor(ContextCompat.getColor(context, R.color.table_data_text_recyclerview));
            regularAdapter.cellTilldate.setText(modeldata.getTill_date());
            regularAdapter.cellTilldate.setTextColor(ContextCompat.getColor(context, R.color.table_data_text_recyclerview));
            regularAdapter.cellTotal.setText(String.valueOf(modeldata.getTotal()));
            regularAdapter.cellTotal.setTextColor(ContextCompat.getColor(context, R.color.table_data_text_recyclerview));
            regularAdapter.cellDate.setText(String.valueOf(modeldata.getDate()));
            regularAdapter.cellDate.setTextColor(ContextCompat.getColor(context, R.color.table_data_text_recyclerview));
            regularAdapter.cellTrips.setText(String.valueOf(modeldata.getTrips()));
            regularAdapter.cellTrips.setTextColor(ContextCompat.getColor(context, R.color.table_data_text_recyclerview));
        }else{
            return;
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolderTableDataRow extends RecyclerView.ViewHolder {

        TextView cellRNG_No, cellTilldate, cellTotal, cellDate, cellTrips;

        public ViewHolderTableDataRow(@NonNull View itemView) {
            super(itemView);
            cellRNG_No = itemView.findViewById(R.id.bothra_rng_no_row);
            cellTilldate = itemView.findViewById(R.id.bothra_till_date_row);
            cellTotal = itemView.findViewById(R.id.bothra_total_row);
            cellDate = itemView.findViewById(R.id.bothra_date_row);
            cellTrips = itemView.findViewById(R.id.bothra_trips_row);
        }
    }
}
