package com.sipl.rfidtagscanner.api;

import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_APP_VERSION_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_BOTHRA_WAREHOUSE_UNLOADING_OUT_TAG_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_CIL_LOADING_OUT_TAG_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.UPDATE_CIL_LOADING_OUT_TAG_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.ADD_CIL_LOADING_IN;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_REMARKS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_BOTHRA_LOADING_ADVISE_TAG_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_BOTHRA_WAREHOUSE_UNLOADING_IN_TAG_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_CIL_LOADING_IN_TAG_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_CIL_WAREHOUSE_TAG_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_LOGIN_USER_DETAIL;
import static com.sipl.rfidtagscanner.utils.ApiConstants.LOGIN;
import static com.sipl.rfidtagscanner.utils.ApiConstants.UPDATE_BOTHRA_LOADING_ADVISE;
import static com.sipl.rfidtagscanner.utils.ApiConstants.UPDATE_CIL_WAREHOUSE;
import static com.sipl.rfidtagscanner.utils.ApiConstants.UPDATE_BOTHRA_WAREHOUSE;

import com.sipl.rfidtagscanner.dto.request.JwtRequest;
import com.sipl.rfidtagscanner.dto.request.LoadingAdviseRequestDto;
import com.sipl.rfidtagscanner.dto.request.UpdateBothraLoadingAdviseDto;
import com.sipl.rfidtagscanner.dto.request.UpdateRmgRequestDto;
import com.sipl.rfidtagscanner.dto.request.UpdateWareHouseNoRequestDto;
import com.sipl.rfidtagscanner.dto.response.AndroidApiResponse;
import com.sipl.rfidtagscanner.dto.response.GenericeApiResponse;
import com.sipl.rfidtagscanner.dto.response.JwtAuthResponse;
import com.sipl.rfidtagscanner.dto.response.LoadingAdvisePostApiResponse;
import com.sipl.rfidtagscanner.dto.response.RemarkApiResponse;
import com.sipl.rfidtagscanner.dto.response.RfidLepApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiController {
    /*
    * Login endPoints
    * */
    @POST(LOGIN)
    Call<JwtAuthResponse> login(@Body JwtRequest jwtRequest);

    @GET(GET_LOGIN_USER_DETAIL + "{userId}")
    Call<GenericeApiResponse> getLoginUserDetail(@Header("Authorization") String authToken, @Path("userId") String userId);

    @GET(GET_APP_VERSION_DETAILS + "{appId}" + "/{version}")
    Call<AndroidApiResponse> getAppVersion(@Path("appId") String appId, @Path("version") String version);


    /*
    * CIL endPoints
    * */
    @GET(GET_CIL_LOADING_IN_TAG_DETAILS + "{tag}" + "/{userId}")
    Call<RfidLepApiResponse> getCilLaTagDetails(@Header("Authorization") String authToken, @Path("tag") String tag, @Path("userId") String userId);

    @GET(GET_CIL_LOADING_OUT_TAG_DETAILS)
    Call<TransactionsApiResponse> getCilLoadingOutTagDetails(@Header("Authorization") String authToken, @Query("currentTransactionFlag") String currentTransactionFlag, @Query("prevTransactionFlag") String prevTransactionFlag, @Query("tagNumber") String tagNumber, @Query("userId") String userId);

    @POST(ADD_CIL_LOADING_IN)
    Call<LoadingAdvisePostApiResponse> addCilLoadingIn(@Header("Authorization") String authToken, @Body LoadingAdviseRequestDto loadingAdviseRequestDto);

    @PUT(UPDATE_CIL_LOADING_OUT_TAG_DETAILS)
    Call<TransactionsApiResponse> updateCilLoadingOut(@Header("Authorization") String authToken, @Body LoadingAdviseRequestDto loadingAdviseRequestDto);

    @GET(GET_CIL_WAREHOUSE_TAG_DETAILS)
    Call<TransactionsApiResponse> getCilWarehouseDetail(@Header("Authorization") String authToken, @Query("currentTransactionFlag") String currentTransactionFlag, @Query("prevTransactionFlag") String prevTransactionFlag, @Query("tagNumber") String tagNumber, @Query("userId") String userId);

    @PUT(UPDATE_CIL_WAREHOUSE)
    Call<TransactionsApiResponse> updateCilWarehouse(@Header("Authorization") String authToken, @Body UpdateRmgRequestDto updateRmgRequestDto);


    /*
    * Bothra endPoints
    * */
    @GET(GET_BOTHRA_LOADING_ADVISE_TAG_DETAILS)
    Call<TransactionsApiResponse> getBothraLaTagDetails(@Header("Authorization") String authToken, @Query("currentTransactionFlag") String currentTransactionFlag, @Query("prevTransactionFlag") String prevTransactionFlag, @Query("tagNumber") String tagNumber, @Query("userId") String userId);

    @PUT(UPDATE_BOTHRA_LOADING_ADVISE)
    Call<TransactionsApiResponse> updateBothraLoadingAdvise(@Header("Authorization") String authToken, @Body UpdateBothraLoadingAdviseDto updateBothraLoadingAdviseDto);

    @GET(GET_BOTHRA_WAREHOUSE_UNLOADING_IN_TAG_DETAILS)
    Call<TransactionsApiResponse> getBothraWhUnloadingInTagDetails(@Header("Authorization") String authToken, @Query("currentTransactionFlag") String currentTransactionFlag, @Query("prevTransactionFlag") String prevTransactionFlag, @Query("tagNumber") String tagNumber, @Query("userId") String userId);

    @GET(GET_BOTHRA_WAREHOUSE_UNLOADING_OUT_TAG_DETAILS)
    Call<TransactionsApiResponse> getBothraWhUnloadingOutTagDetails(@Header("Authorization") String authToken, @Query("currentTransactionFlag") String currentTransactionFlag, @Query("tagNumber") String tagNumber, @Query("userId") String userId);

    @PUT(UPDATE_BOTHRA_WAREHOUSE)
    Call<TransactionsApiResponse> updateBothraWarehouse(@Header("Authorization") String authToken, @Body UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto);


    /*
    * Common endPoints
    * */
    @GET(GET_ALL_REMARKS)
    Call<RemarkApiResponse> getRemarks(@Header("Authorization") String authToken);
}


