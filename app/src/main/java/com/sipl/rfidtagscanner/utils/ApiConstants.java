package com.sipl.rfidtagscanner.utils;

public class ApiConstants {
    //    public static final String BASE_URL = "http://192.168.1.109:3000/coromandel-rest-repo/";
//   public static final String BASE_URL = "http://192.168.1.202:8080/coromandel-rest-repo/";
//  sudhir
//    public static final String BASE_URL = "http://10.66.66.5:8089/coromandel-rest-repo/";
    public static final String BASE_URL = "http://10.66.66.90:8080/coromandel-rest-repo/";

    //   login
//    public static final String LOGIN = BASE_URL + "api/v1/auth/login/";
    public static final String LOGIN = BASE_URL + "users/validate/";


    //   Loading Advise
    public static final String GET_LEP_NUMBER = BASE_URL + "RfidLepIssue/";
    public static final String GET_SOURCE_LOCATION_DETAILS = BASE_URL + "source/getAllSourceDetails/";
    public static final String GET_DESTINATION_LOCATION_DETAILS = BASE_URL + "storageLocation/getAllStorageLocation/";
    public static final String GET_ALL_PINNACLE_SUPERVISOR = BASE_URL + "pinnacle/getAllPinnacleDetails/";
    public static final String GET_ALL_BOTHRA_SUPERVISOR = BASE_URL + "bothra/getAllBothraDetails/";
    public static final String ADD_RFID_LEP_ISSUE = BASE_URL + "transactions/addLoadingTransactions/";

    //    Coromandel
    public static final String GET_LEP_NUMBER_COROMANDEL = BASE_URL + "transactions/3/4/";
    public static final String GET_ALL_RMG_NUMBER = BASE_URL + "storageLocation/getAllStorageLocationByPlantCode/";
    public static final String GET_ALL_REMARKS = BASE_URL + "CoWhSupervisor/getAllRemarks/";
    public static final String UPDATE_RMG_NO = BASE_URL + "CoWhSupervisor/";

    //    Bothra
    public static final String GET_LEP_NUMBER_BOTHRA = BASE_URL + "transactions/7/8/";
    public static final String GET_ALL_WAREHOUSE_NUMBER = BASE_URL + "storageLocation/getAllStorageLocationByPlantCode/";
    public static final String GET_ALL_REMARK = BASE_URL + "CoWhSupervisor/getAllRemarks/";
    public static final String UPDATE_WAREHOUSE_NUMBER = BASE_URL + "CoWhSupervisor/";

//    Bothra loading advise
    public static final String UPDATE_BOTHRA_LOADING_ADVISE = BASE_URL + "transactions/updateLoadingTransactions/";


}
