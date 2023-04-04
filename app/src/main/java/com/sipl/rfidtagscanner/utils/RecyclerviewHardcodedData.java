package com.sipl.rfidtagscanner.utils;

import com.sipl.rfidtagscanner.model.RmgModel;
import com.sipl.rfidtagscanner.model.TripModel;

import java.util.ArrayList;
import java.util.List;

public class RecyclerviewHardcodedData {

    private ArrayList<RmgModel> rmgModelsList = new ArrayList<>();
    private ArrayList<TripModel> tripModelsList = new ArrayList<>();

    public RecyclerviewHardcodedData() {
    }

    public List<RmgModel> initRmgData() {
        rmgModelsList.add(new RmgModel("523444", "23-09-2022", "23"));
        rmgModelsList.add(new RmgModel("ID5678", "23-09-2022", "235"));
        rmgModelsList.add(new RmgModel("323444", "23-09-2022", "233"));
        rmgModelsList.add(new RmgModel("ID4678", "23-09-2022", "2315"));
        rmgModelsList.add(new RmgModel("423444", "23-09-2022", "231"));
        rmgModelsList.add(new RmgModel("8D5678", "23-09-2022", "2145"));
        rmgModelsList.add(new RmgModel("023444", "23-09-2022", "2113"));
        rmgModelsList.add(new RmgModel("1D5678", "23-09-2022", "21345"));

        return rmgModelsList;
    }

    public List<TripModel> initTripData() {
        tripModelsList.add(new TripModel("14-12-2022", "20"));
        tripModelsList.add(new TripModel("14-12-2022", "23"));
        tripModelsList.add(new TripModel("14-12-2022", "30"));
        tripModelsList.add(new TripModel("14-12-2022", "10"));
        tripModelsList.add(new TripModel("14-12-2022", "22"));
        tripModelsList.add(new TripModel("14-12-2022", "240"));
        tripModelsList.add(new TripModel("14-12-2022", "56"));
        tripModelsList.add(new TripModel("14-12-2022", "245"));

        return tripModelsList;
    }


}
