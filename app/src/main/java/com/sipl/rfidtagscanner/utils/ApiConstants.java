package com.sipl.rfidtagscanner.utils;

public class ApiConstants {
//    Local 90 server
    public static final String BASE_URL = "http://10.66.66.90:8080/coromandel-rest-repo/";


//    Apache
//    public static final String BASE_URL = "http://10.210.213.4/coromandel-rest-repo/";
//    public static final String BASE_URL = "http://localhost:8082/coromandel-rest-repo/";
//    public static final String BASE_URL = "http://10.66.66.5:8082/coromandel-rest-repo/";
//    public static final String BASE_URL = "https://rmmsuat.coromandel.biz/coromandel-rest-repo/";
//    public static final String BASE_URL = "https://rmms.coromandel.biz/coromandel-rest-repo/";


//    Tomcat
//    public static final String BASE_URL = "http://10.210.213.5:8080/coromandel-rest-repo/";



    // login
    public static final String LOGIN = BASE_URL + "api/v1/auth/login/";
    public static final String LOGOUT = BASE_URL + "users/logoutUser";
    public static final String LOGIN_WITHOUT_JWT = BASE_URL + "users/validate";

    // Loading Advise
    public static final String GET_COROMANDEL_LOADING_ADVISE_DETAILS = BASE_URL + "RfidLepIssue/getRfidLepIssueDetailsByTagNumber/";
    public static final String GET_BOTHRA_LOADING_ADVISE_DETAILS = BASE_URL + "transactions/getTransactionFromFlagAndTagNumber";

    // Bothra loading advise
    public static final String GET_DESTINATION_LOCATION_DETAILS = BASE_URL + "storageLocation/getAllStorageLocation/";
    public static final String GET_ALL_PINNACLE_SUPERVISOR = BASE_URL + "pinnacle/getAllPinnacleDetails/";
    public static final String GET_ALL_BOTHRA_SUPERVISOR = BASE_URL + "bothra/getAllBothraDetails/";
    public static final String ADD_RFID_LEP_ISSUE = BASE_URL + "transactions/addLoadingTransactions/";
    public static final String ADD_OUT_RFID_LEP_ISSUE = BASE_URL + "/transactions/updatePortLoadingTransactions";
    public static final String UPDATE_BOTHRA_LOADING_ADVISE = BASE_URL + "transactions/updateLoadingTransactions/";

    //    Coromandel
    public static final String GET_COROMANDEL_WAREHOUSE_SCREEN_DETAILS = BASE_URL + "transactions/getTransactionFromFlagAndTagNumber";
    public static final String GET_ALL_RMG_NUMBER = BASE_URL + "storageLocation/";
    public static final String GET_ALL_REMARKS = BASE_URL + "CoWhSupervisor/getAllRemarks/";
    public static final String UPDATE_RMG_NO = BASE_URL + "CoWhSupervisor/";

    //    Bothra
    public static final String GET_BOTHRA_WAREHOUSE_SCREEN_DETAILS = BASE_URL + "transactions/getTransactionFromFlagAndTagNumber";
    public static final String GET_ALL_WAREHOUSE_NUMBER = BASE_URL + "storageLocation/";
    public static final String GET_ALL_REMARK = BASE_URL + "CoWhSupervisor/getAllRemarks/";
    public static final String UPDATE_WAREHOUSE_NUMBER = BASE_URL + "CoWhSupervisor/";

}
