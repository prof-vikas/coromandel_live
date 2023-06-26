package com.sipl.rfidtagscanner.utils;

public class Config {

    //    view constants
    public static final boolean isJWTEnable = true;


    public static final boolean isPlantDetailsRequiredInSideNav = true;

    //    Roles Constants
/*    public static final String ROLES_BWH = "Bothra CIL Security Guard";
    public static final String ROLES_CWH = "Factory Warehouse Operator";
    public static final String ROLES_LAO = "LoadingAdvise Operator";
    public static final String ROLES_ADMIN_SUPER = "Super Admin";
    public static final String ROLES_ADMIN_PLANT = "Plant Admin";*/

    public static final String ROLES_BWH = "9";
    public static final String ROLES_CWH = "18";
    public static final String ROLES_LAO = "7";
    public static final String ROLES_ADMIN_SUPER = "";
    //TODO below 4 is need to use delete 1 in admin plant
    public static final String ROLES_ADMIN_PLANT = "1";
//    public static final String ROLES_ADMIN_PLANT = "4";


/*    public static final String ROLES_BWH = "Bothra CIL Security Guard";
    public static final String ROLES_CWH = "Factory Warehouse Operator";
    public static final String ROLES_LAO = "Coromandel LoadingAdvise Operator";
    public static final String ROLES_ADMIN_SUPER = "Super Admin";
    public static final String ROLES_ADMIN_PLANT = "Plant Admin";*/


    //    Dialog message type
    public static final String DIALOG_ERROR = "ERROR";
    public static final String DIALOG_WARNING = "WARNING";
    public static final String DIALOG_SUCCESS = "SUCCESS";

    public static final String WRONG_CREDENTIALS = "User name or password Mismatch";


    public static final String EMPTY_DESTINATION_LOCATION = "No Destination location available";
    public static final String EMPTY_BOTHRA_SUPERVISOR = "No Bothra Supervisor is available";
    public static final String EMPTY_PINNACLE_SUPERVISOR = "No Pinnacle Supervisor is available";

    //Coromandel
    public static final String EMPTY_RMG_NUMBER = "No RMG Number available";
    public static final String EMPTY_REMARKS = "No remarks available";

    //Coromandel
    public static final String EMPTY_WAREHOUSE_NUMBER = "No WareHouse available";

}
