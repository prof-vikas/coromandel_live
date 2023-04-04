package com.sipl.rfidtagscanner.api;

import static com.sipl.rfidtagscanner.utils.ApiConstants.ADD_RFID_LEP_ISSUE;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_BOTHRA_SUPERVISOR;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_PINNACLE_SUPERVISOR;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_REMARK;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_REMARKS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_RMG_NUMBER;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_ALL_WAREHOUSE_NUMBER;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_DESTINATION_LOCATION_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_LEP_NUMBER;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_LEP_NUMBER_BOTHRA;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_LEP_NUMBER_COROMANDEL;
import static com.sipl.rfidtagscanner.utils.ApiConstants.GET_SOURCE_LOCATION_DETAILS;
import static com.sipl.rfidtagscanner.utils.ApiConstants.LOGIN;
import static com.sipl.rfidtagscanner.utils.ApiConstants.UPDATE_RMG_NO;
import static com.sipl.rfidtagscanner.utils.ApiConstants.UPDATE_WAREHOUSE_NUMBER;

import com.sipl.rfidtagscanner.dto.request.JwtRequest;
import com.sipl.rfidtagscanner.dto.request.LoadingAdviseRequestDto;
import com.sipl.rfidtagscanner.dto.request.UpdateRmgRequestDto;
import com.sipl.rfidtagscanner.dto.request.UpdateWareHouseNoRequestDto;
import com.sipl.rfidtagscanner.dto.response.BothraSupervisorApiResponse;
import com.sipl.rfidtagscanner.dto.response.DestinationLocationResponseApi;
import com.sipl.rfidtagscanner.dto.response.JwtAuthResponse;
import com.sipl.rfidtagscanner.dto.response.LoadingAdvisePostApiResponse;
import com.sipl.rfidtagscanner.dto.response.PinnacleSupervisorApiResponse;
import com.sipl.rfidtagscanner.dto.response.RemarkApiResponse;
import com.sipl.rfidtagscanner.dto.response.RfidLepApiResponse;
import com.sipl.rfidtagscanner.dto.response.RmgNumberApiResponse;
import com.sipl.rfidtagscanner.dto.response.SourceLocationApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface LoadingAdviseApi {

    //    Login
    @POST(LOGIN)
    Call<JwtAuthResponse> login(@Body JwtRequest jwtRequest);


    //    Loading Advise
    @GET(GET_LEP_NUMBER)
    Call<RfidLepApiResponse> getALlLepNumber(@Header("Authorization") String authToken);

    @GET(GET_SOURCE_LOCATION_DETAILS)
    Call<SourceLocationApiResponse> getAllSourceLocation(@Header("Authorization") String authToken);

    @GET(GET_DESTINATION_LOCATION_DETAILS)
    Call<DestinationLocationResponseApi> getAllDestinationLocation(@Header("Authorization") String authToken);

    @GET(GET_ALL_PINNACLE_SUPERVISOR)
    Call<PinnacleSupervisorApiResponse> getAllPinnacleSupervisor(@Header("Authorization") String authToken);

    @GET(GET_ALL_BOTHRA_SUPERVISOR)
    Call<BothraSupervisorApiResponse> getAllBothraSupervisor(@Header("Authorization") String authToken);

    @POST(ADD_RFID_LEP_ISSUE)
    Call<LoadingAdvisePostApiResponse> addRfidLepIssue(@Header("Authorization") String authToken, @Body LoadingAdviseRequestDto loadingAdviseRequestDto);

    //Coromandel
    @GET(GET_LEP_NUMBER_COROMANDEL)
    Call<TransactionsApiResponse> getALlLepNumberWithFlag(@Header("Authorization") String authToken);

    @GET(GET_ALL_RMG_NUMBER)
    Call<RmgNumberApiResponse> getAllCoromandelRmgNo(@Header("Authorization") String authToken, @Query("plantCode") String id);

    @GET(GET_ALL_REMARKS)
    Call<RemarkApiResponse> getAllCoromandelRemark(@Header("Authorization") String authToken);

    @PUT(UPDATE_RMG_NO)
    Call<TransactionsApiResponse> updateRmgNo(@Header("Authorization") String authToken, @Body UpdateRmgRequestDto updateRmgRequestDto);


    //Bothra
    @GET(GET_LEP_NUMBER_BOTHRA)
    Call<TransactionsApiResponse> getALlLepNumberBothra(@Header("Authorization") String authToken);

    @GET(GET_ALL_WAREHOUSE_NUMBER)
    Call<RmgNumberApiResponse> getAllWareHouse(@Header("Authorization") String authToken, @Query("plantCode") String id);

    @GET(GET_ALL_REMARK)
    Call<RemarkApiResponse> getAllBothraRemark(@Header("Authorization") String authToken);

    @PUT(UPDATE_WAREHOUSE_NUMBER)
    Call<TransactionsApiResponse> updateWareHouse(@Header("Authorization") String authToken, @Body UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto);
}


