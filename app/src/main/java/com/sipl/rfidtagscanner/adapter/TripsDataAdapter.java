package com.sipl.rfidtagscanner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.model.TripModel;

import java.util.List;

public class TripsDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String TAG = "BothraTag";
    private Context context;
    private List<TripModel> tripModelList;

    public TripsDataAdapter(Context context, List<TripModel> tripModelList) {
        this.context = context;
        this.tripModelList = tripModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.layout_common_table_trips_items_row_data,
                parent, false);
        viewHolder = new ViewHolderTableDataRow(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (tripModelList != null && tripModelList.size() > 0) {
            Log.i(TAG, "onBindViewHolder: () <<start>>");
            TripModel tripModel = tripModelList.get(position);
            Log.i(TAG, "onBindViewHolder: tripModel date" + tripModel.getDate() +"tripmodel trips" + tripModel.getTrips());
            ViewHolderTableDataRow regularAdapter = (ViewHolderTableDataRow) holder;

            regularAdapter.cellDate.setText(tripModel.getDate());
            regularAdapter.cellDate.setTextColor(ContextCompat.getColor(context, R.color.table_data_text_recyclerview));
            regularAdapter.cellTrips.setText(tripModel.getTrips());
            regularAdapter.cellTrips.setTextColor(ContextCompat.getColor(context, R.color.table_data_text_recyclerview));
            Log.i(TAG, "onBindViewHolder: () <<end>>");
        }
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: tripModelList.size()"+ tripModelList.size());
        return tripModelList.size();
    }

    public class ViewHolderTableDataRow extends RecyclerView.ViewHolder {

        TextView cellDate, cellTrips;

        public ViewHolderTableDataRow(@NonNull View itemView) {
            super(itemView);
            Log.i(TAG, "ViewHolderTableDataRow: <<start>>");
            cellDate = itemView.findViewById(R.id.common_date_row);
            cellTrips = itemView.findViewById(R.id.common_trips_row);
            Log.i(TAG, "ViewHolderTableDataRow: <<end>>");
        }
    }
}
