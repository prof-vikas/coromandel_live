package com.sipl.rfidtagscanner.utils;

public class ApiConstants {
    /*
     * Dev environment
     * */
//    public static final String BASE_URL = "http://10.66.66.5:8081/coromandel-rest-repo/";
//    public static final String BASE_URL = "http://10.66.66.91:8080/coromandel-rest-repo/";


    /*
     * client UAT environment
     * */
//    public static final String BASE_URL = "https://rmmsuat.coromandel.biz/coromandel-rest-repo/";
    public static final String BASE_URL = "http://10.210.213.6:8080/coromandel-rest-repo/";


    /*
     * client PROD environment
     * */
//    public static final String BASE_URL = "https://rmms.coromandel.biz/coromandel-rest-repo/";


    /*
     * login endPoints
     * */
    public static final String LOGIN = BASE_URL + "api/v1/auth/login/";
    public static final String GET_LOGIN_USER_DETAIL = BASE_URL + "users/v2/locations/valid-mapped/";

   /*
   * CIL endPoints
   * */
    public static final String GET_CIL_LOADING_IN_TAG_DETAILS = BASE_URL + "RfidLepIssue/getRfidLepIssueDetailsByTagNumber/";
    public static final String GET_CIL_LOADING_OUT_TAG_DETAILS = BASE_URL + "transactions/getTransactionFromFlagAndTagNumber";
    public static final String ADD_CIL_LOADING_IN = BASE_URL + "transactions/addLoadingTransactions/";
    public static final String UPDATE_CIL_LOADING_OUT_TAG_DETAILS = BASE_URL + "transactions/updatePortLoadingTransactions";
    public static final String GET_CIL_WAREHOUSE_TAG_DETAILS = BASE_URL + "transactions/getTransactionFromFlagAndTagNumber";
    public static final String UPDATE_CIL_WAREHOUSE = BASE_URL + "CoWhSupervisor/";

    /*
    * Bothra endPoints
    * */
    public static final String GET_BOTHRA_LOADING_ADVISE_TAG_DETAILS = BASE_URL + "transactions/getTransactionFromFlagAndTagNumber";
    public static final String UPDATE_BOTHRA_LOADING_ADVISE = BASE_URL + "transactions/updateLoadingTransactions/";
    public static final String GET_BOTHRA_WAREHOUSE_UNLOADING_IN_TAG_DETAILS = BASE_URL + "transactions/getTransactionFromFlagAndTagNumber";
    public static final String GET_BOTHRA_WAREHOUSE_UNLOADING_OUT_TAG_DETAILS = BASE_URL + "transactions/getTransactionFromFlagAndTagNumber";
    public static final String UPDATE_BOTHRA_WAREHOUSE = BASE_URL + "CoWhSupervisor/";

  /*
  * common endPoints
  * */
    public static final String GET_ALL_REMARKS = BASE_URL + "CoWhSupervisor/getAllRemarks/";

}
