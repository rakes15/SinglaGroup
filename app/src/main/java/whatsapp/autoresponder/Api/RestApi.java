package whatsapp.autoresponder.Api;

import orderbooking.StaticValues;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import whatsapp.autoresponder.Model.Rest.ListResponse;

public interface RestApi {

    public static final String BASE_URL = StaticValues.STATIC_BASE_URL;//"http://singlaapparels.com/whatsapp_api/";

    public static final String AKey = "Testing123";
    public static final String UserPwd = "123456#123T";

    @FormUrlEncoded
    @POST("whatsapp_recMsg")
    Call<ListResponse<String>> storeIncomingMsg(@Field("DeviceID") String deviceId,
                                                @Field("ConverName") String converName,
                                                @Field("ConverNumber") String number,
                                                @Field("profileImage") String profileImage,
                                                @Field("text") String text,
                                                @Field("time") String time,
                                                @Field("isGroup") String isFromGroup,
                                                @Field("Id") String Id);
    @FormUrlEncoded
    @POST("Device_FCM_Update")
    Call<ListResponse<String>> storeFcmToken(@Field("AKey") String AKey,
                                             @Field("UserPwd") String UserPwd,
                                             @Field("ServerKey") String ServerKey,
                                             @Field("ModelNo") String ModelNo,
                                             @Field("MacID") String MacID,
                                             @Field("IMEINo") String IMEINo,
                                             @Field("UniqueKey") String UniqueKey,
                                             @Field("FCMID") String FCMID,
                                             @Field("SerialNo") String SerialNo);

    @FormUrlEncoded
    @POST("whatsappMsgStatusUpdateApi")
    Call<ListResponse<String>> whatsappMsgStatusUpdateApi(@Field("DeviceID") String DeviceID,
                                                          @Field("MsgID") String MsgID,
                                                          @Field("Status") String Status,
                                                          @Field("StatusDateTime") String StatusDateTime);

}




