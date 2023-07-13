package com.sipl.rfidtagscanner.api;

import static com.sipl.rfidtagscanner.utils.ApiConstants.ADD_OUT_RFID_LEP_ISSUE;
import static com.sipl.rfidtagscanner.utils.ApiConstants.ADD_RFID_LEP_ISSUE;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_REMARK;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_REMARKS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_RMG_NUMBER;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_WAREHOUSE_NUMBER;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_BOTHRA_LOADING_ADVISE_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_BOTHRA_WAREHOUSE_SCREEN_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_COROMANDEL_LOADING_ADVISE_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_COROMANDEL_WAREHOUSE_SCREEN_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_LOGIN_USER_DETAIL;
import static com.sipl.rfidtagscanner.utils.ApiConstants.LOGIN;
import static com.sipl.rfidtagscanner.utils.ApiConstants.LOGIN_WITHOUT_JWT;
import static com.sipl.rfidtagscanner.utils.ApiConstants.LOGOUT;
import static com.sipl.rfidtagscanner.utils.ApiConstants.UPDATE_BOTHRA_LOADING_ADVISE;
import static com.sipl.rfidtagscanner.utils.ApiConstants.UPDATE_RMG_NO;
import static com.sipl.rfidtagscanner.utils.ApiConstants.UPDATE_WAREHOUSE_NUMBER;

import com.sipl.rfidtagscanner.dto.dtos.UserMasterDto;
import com.sipl.rfidtagscanner.dto.request.JwtRequest;
import com.sipl.rfidtagscanner.dto.request.LoadingAdviseRequestDto;
import com.sipl.rfidtagscanner.dto.request.UpdateBothraLoadingAdviseDto;
import com.sipl.rfidtagscanner.dto.request.UpdateRmgRequestDto;
import com.sipl.rfidtagscanner.dto.request.UpdateWareHouseNoRequestDto;
import com.sipl.rfidtagscanner.dto.response.JwtAuthResponse;
import com.sipl.rfidtagscanner.dto.response.LoadingAdvisePostApiResponse;
import com.sipl.rfidtagscanner.dto.response.RemarkApiResponse;
import com.sipl.rfidtagscanner.dto.response.RfidLepApiResponse;
import com.sipl.rfidtagscanner.dto.response.RmgNumberApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.dto.response.UserValidateResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiController {

    //    Login
    @POST(LOGIN)
    Call<JwtAuthResponse> login(@Body JwtRequest jwtRequest);

    @GET(LOGIN_WITHOUT_JWT + "/{userId}" + "/{password}")
    Call<UserValidateResponseDto> loginWithOutJwt(@Path("userId") String userId, @Path("password") String password);

    @GET(GET_LOGIN_USER_DETAIL + "{username}")
    Call<UserValidateResponseDto> getLoginUserDetails(@Header("Authorization") String authToken, @Path("username") String userName);

    @PUT(LOGOUT)
    Call<UserMasterDto> logout(@Body UserMasterDto userMasterDto);


    //    Loading Advise
    @GET(GET_COROMANDEL_LOADING_ADVISE_DETAILS + "{tag}")
    Call<RfidLepApiResponse> getRfidTagDetailCoromandelLA(@Header("Authorization") String authToken, @Path("tag") String tag);

    @GET(GET_BOTHRA_LOADING_ADVISE_DETAILS)
    Call<TransactionsApiResponse> getRfidTagDetailBothraLA(@Header("Authorization") String authToken, @Query("currentTransactionFlag") String currentTransactionFlag, @Query("prevTransactionFlag") String prevTransactionFlag, @Query("tagNumber") String tagNumber);

    @POST(ADD_RFID_LEP_ISSUE)
    Call<LoadingAdvisePostApiResponse> addRfidLepIssue(@Header("Authorization") String authToken, @Body LoadingAdviseRequestDto loadingAdviseRequestDto);

    @PUT(ADD_OUT_RFID_LEP_ISSUE)
    Call<TransactionsApiResponse> updateCoromandelLoadingAdvise(@Header("Authorization") String authToken, @Body LoadingAdviseRequestDto loadingAdviseRequestDto);

    //  BothraLoadingAdvise
    @PUT(UPDATE_BOTHRA_LOADING_ADVISE)
    Call<TransactionsApiResponse> updateBothraLoadingAdvise(@Header("Authorization") String authToken, @Body UpdateBothraLoadingAdviseDto updateBothraLoadingAdviseDto);


    //Coromandel
    @GET(GET_COROMANDEL_WAREHOUSE_SCREEN_DETAILS)
    Call<TransactionsApiResponse> getCoromandelWHDetails(@Header("Authorization") String authToken, @Query("currentTransactionFlag") String currentTransactionFlag, @Query("prevTransactionFlag") String prevTransactionFlag, @Query("tagNumber") String tagNumber);

    @GET(GET_ALL_RMG_NUMBER + "{storageLocation}")
    Call<RmgNumberApiResponse> getAllCoromandelRmgNo(@Header("Authorization") String authToken, @Path("storageLocation") String storageLocation);

    @GET(GET_ALL_REMARKS)
    Call<RemarkApiResponse> getAllCoromandelRemark(@Header("Authorization") String authToken);

    @PUT(UPDATE_RMG_NO)
    Call<TransactionsApiResponse> updateRmgNo(@Header("Authorization") String authToken, @Body UpdateRmgRequestDto updateRmgRequestDto);


    //Bothra
    @GET(GET_BOTHRA_WAREHOUSE_SCREEN_DETAILS)
    Call<TransactionsApiResponse> getBothraWHDetails(@Header("Authorization") String authToken, @Query("currentTransactionFlag") String currentTransactionFlag, @Query("prevTransactionFlag") String prevTransactionFlag, @Query("tagNumber") String tagNumber);

    @GET(GET_BOTHRA_WAREHOUSE_SCREEN_DETAILS)
    Call<TransactionsApiResponse> getBothraWHDetailsForExit(@Header("Authorization") String authToken, @Query("currentTransactionFlag") String currentTransactionFlag, @Query("tagNumber") String tagNumber);

    @GET(GET_ALL_WAREHOUSE_NUMBER + "{storageLocation}")
    Call<RmgNumberApiResponse> getAllWareHouse(@Header("Authorization") String authToken, @Path("storageLocation") String storageLocation);

    @GET(GET_ALL_REMARK)
    Call<RemarkApiResponse> getAllBothraRemark(@Header("Authorization") String authToken);

    @PUT(UPDATE_WAREHOUSE_NUMBER)
    Call<TransactionsApiResponse> updateWareHouse(@Header("Authorization") String authToken, @Body UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto);

}


