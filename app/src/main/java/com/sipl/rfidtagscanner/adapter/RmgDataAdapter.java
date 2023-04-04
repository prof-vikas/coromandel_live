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
import com.sipl.rfidtagscanner.model.RmgModel;

import java.util.List;

public class RmgDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<RmgModel> rmgModelList;

    public RmgDataAdapter(Context context, List<RmgModel> rmgModelList) {
        this.context = context;
        this.rmgModelList = rmgModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        view = LayoutInflater.from(context).inflate(R.layout.layout_common_table_rmg_items_row_data,
                parent, false);
        viewHolder = new ViewHolderTableDataRow(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(rmgModelList != null && rmgModelList.size() > 0){
            RmgModel rmgModel = rmgModelList.get(position);
            ViewHolderTableDataRow regularAdapter = (ViewHolderTableDataRow) holder;

            regularAdapter.cellRNG_No.setText(rmgModel.getRmg_no());
            regularAdapter.cellRNG_No.setTextColor(ContextCompat.getColor(context, R.color.table_data_text_recyclerview));
            regularAdapter.cellTilldate.setText(rmgModel.getTill_date());
            regularAdapter.cellTilldate.setTextColor(ContextCompat.getColor(context, R.color.table_data_text_recyclerview));
            regularAdapter.cellTotal.setText(rmgModel.getTotal());
            regularAdapter.cellTotal.setTextColor(ContextCompat.getColor(context, R.color.table_data_text_recyclerview));
        }
    }

    @Override
    public int getItemCount() {
        return rmgModelList.size();
    }

    public class ViewHolderTableDataRow extends RecyclerView.ViewHolder {

        TextView cellRNG_No, cellTilldate, cellTotal;

        public ViewHolderTableDataRow(@NonNull View itemView) {
            super(itemView);
            cellRNG_No = itemView.findViewById(R.id.common_rng_no_row);
            cellTilldate = itemView.findViewById(R.id.common_till_date_row);
            cellTotal = itemView.findViewById(R.id.common_total_row);
        }
    }
}