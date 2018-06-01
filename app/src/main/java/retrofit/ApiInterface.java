package retrofit;

import com.singlagroup.responsedatasets.ResponseAppCheckDataset;
import com.singlagroup.responsedatasets.ResponseAppCheckDatasets;
import com.singlagroup.responsedatasets.ResponseChangePasswordDataset;
import com.singlagroup.responsedatasets.ResponseCityListDataset;
import com.singlagroup.responsedatasets.ResponseCountryListDataset;
import com.singlagroup.responsedatasets.ResponseDeviceRegistrationDataset;
import com.singlagroup.responsedatasets.ResponseDivisionListDataset;
import com.singlagroup.responsedatasets.ResponseLogInUserMainDataset;
import com.singlagroup.responsedatasets.ResponseOTPSendDataset;
import com.singlagroup.responsedatasets.ResponseOTPValidateDataset;
import com.singlagroup.responsedatasets.ResponseSessionDataset;
import com.singlagroup.responsedatasets.ResponseStateListDataset;
import com.singlagroup.responsedatasets.ResponseTempDeviceRegistrationDataset;

import java.util.Map;

import inventory.analysis.catalogue.filterdataset.ResponseFilter;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import orderbooking.catalogue.addtobox.dataset.ResponseBoxListDataset;
import orderbooking.catalogue.addtobox.responsedataset.ResponseMoveToWishlistDataset;
import orderbooking.catalogue.addtobox.responsedataset.ResponseRemoveToBoxDataset;
import orderbooking.catalogue.responsedataset.ResponseAddItemInBoxDataset;
import orderbooking.catalogue.responsedataset.ResponseAddOrRemoveDataset;
import orderbooking.catalogue.responsedataset.ResponseAllGroupsDataset;
import orderbooking.catalogue.responsedataset.ResponseColorOptionDataset;
import orderbooking.catalogue.responsedataset.ResponseFilterDataset;
import orderbooking.catalogue.responsedataset.ResponseImageListDataset;
import orderbooking.catalogue.responsedataset.ResponseItemDetailsDataset;
import orderbooking.catalogue.responsedataset.ResponseItemListMainDataset;
import orderbooking.catalogue.responsedataset.ResponseSimilarColorDataset;
import orderbooking.catalogue.responseitemlist.ResponseItemList;
import orderbooking.catalogue.wishlist.dataset.ResponseWishListDataset;
import orderbooking.customerlist.partyinfo.model.PartyCompleteInfo;
import orderbooking.customerlist.partyinfo.model.SubPartyCompleteInfo;
import orderbooking.customerlist.responsedatasets.ResponseSelectCustomerForOrderDataset;
import orderbooking.customerlist.responsedatasets.ResponseSelectSubCustomerForOrderDataset;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import uploadimagesfiles.documentattachment.responsedatasets.ResponseDocumentDataset;
import uploadimagesfiles.responsedatasets.ResponseFileImageUploadDataset;
import uploadimagesfiles.responsedatasets.ResponseItemInfoDataset;
import uploadimagesfiles.responsedatasets.ResponseUploadItemImageDataset;
import uploadimagesfiles.voucherdocupload.responsedatasets.ResponseMasterDocumentDataset;
import uploadimagesfiles.voucherdocupload.responsedatasets.ResponseVoucherDocumentDataset;
import uploadimagesfiles.voucherdocupload.responsedatasets.ResponseVoucherDocumentSpinnerDataset;

/**
 * Created by Rakesh on 28-Sept-16.
 */
public interface ApiInterface {

    @FormUrlEncoded
    @POST("AppCheck")
    Call<ResponseAppCheckDatasets> getAppCheck(@FieldMap Map<String, String> params);

    @POST("GetDivisionList")
    Call<ResponseDivisionListDataset> getDivisionList();

    @FormUrlEncoded
    @POST("GetCountryList")
    Call<ResponseCountryListDataset> getCountryList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetStateList")
    Call<ResponseStateListDataset> getStateList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetCityList")
    Call<ResponseCityListDataset> getCityList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("TempDeviceRegistration")
    Call<ResponseTempDeviceRegistrationDataset> insertUserRegistrationTemp(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("DeviceRegistartion")
    Call<ResponseDeviceRegistrationDataset> insertUserRegistration(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("RequestOTP")
    Call<ResponseOTPSendDataset> requestOTP(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("OTPValidate")
    Call<ResponseOTPValidateDataset> getOTPValidate(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("LogInUser")
    Call<ResponseLogInUserMainDataset> getLogInUser(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("PasswordChange")
    Call<ResponseChangePasswordDataset> getChangePassword(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("PartyList")
    Call<ResponseSelectCustomerForOrderDataset> getPartyListAll(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("SubPartyList")
    Call<ResponseSelectSubCustomerForOrderDataset> getSubPartyListAll(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("PartyList")
    Call<ResponseItemInfoDataset> getItemImageInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("DocAttechVTypeList")
    Call<ResponseVoucherDocumentSpinnerDataset> getDocAttachVTypeList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("DocAttechVoucherList")
    Call<ResponseVoucherDocumentDataset> getDocAttachVoucherList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("DocAttechVoucherList")
    Call<ResponseMasterDocumentDataset> getDocAttachMasterList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("VoucherAttechment")
    Call<ResponseFileImageUploadDataset> getDocAttechVoucherUpload(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("PartyBillList")
    Call<ResponseDocumentDataset> getPartyBillList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("UploadCommonDocToFolder")
    Call<ResponseFileImageUploadDataset> getFileImageUpload(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("ItemDetForImageUpload")
    Call<ResponseItemInfoDataset> getItemInfoWithImage(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("ItemImageUploadInDB")
    Call<ResponseUploadItemImageDataset> postItemImageUpload(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("LogOut")
    Call<ResponseSessionDataset> getSessionLogout(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("UserActiveTimeOnApp")
    Call<ResponseSessionDataset> getUserActiveTimeOnApp(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("ItemCategory")
    Call<ResponseAllGroupsDataset> getAllGroupsList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("ItemList")
    Call<ResponseItemList> getItemList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("ItemListColorWise")
    Call<orderbooking.catalogue.responseitemlistcolor.ResponseItemList> getItemListColor(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetFilter")
    Call<ResponseFilterDataset> getFilter(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("SearchItem")
    Call<ResponseItemListMainDataset> getSearchItemList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetSimilarItems")
    Call<ResponseSimilarColorDataset> getSimilarItemList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetItemsColorList")
    Call<ResponseColorOptionDataset> getColorOptionList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetItemsDetails")
    Call<ResponseItemDetailsDataset> getItemDetails(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetItemImageList")
    Call<ResponseImageListDataset> getImageList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetWishListItems")
    Call<ResponseWishListDataset> getWishList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetCartListItems")
    Call<ResponseBoxListDataset> getBoxList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("AddRemoveWishlistItem")
    Call<ResponseAddOrRemoveDataset> getStatusForWishListAddOrDelete(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("AddItemInCart")
    Call<ResponseAddItemInBoxDataset> setAddItemInCart(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("RemoveItemFromCart")
    Call<ResponseRemoveToBoxDataset> RemoveItemFromCart(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("CartToWishlistMove")
    Call<ResponseMoveToWishlistDataset> MoveToWishList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("PartyCompleteInfo")
    Call<PartyCompleteInfo> PartyCompleteInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("PartySubPartyCompleteInfo")
    Call<SubPartyCompleteInfo> SubPartyCompleteInfo(@FieldMap Map<String, String> params);


    //TODO: Inventory Analysis
    @FormUrlEncoded
    @POST("AllCategory")
    Call<inventory.analysis.catalogue.responsedataset.ResponseAllGroupsDataset> getInventoryAllCatagory(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("ItemCatalog")
    Call<inventory.analysis.catalogue.responseitemlist.ResponseItemList> getInventoryItemList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("ItemListColorWise")
    Call<inventory.analysis.catalogue.responseitemlistcolor.ResponseItemList> getInventoryItemListColor(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetFilterDetails")
    Call<ResponseFilter> getInventoryFilter(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("SearchItem")
    Call<inventory.analysis.catalogue.responsedataset.ResponseItemListMainDataset> getInventorySearchItemList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetSimilarItems")
    Call<inventory.analysis.catalogue.responsedataset.ResponseSimilarColorDataset> getInventorySimilarItemList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetItemsColorList")
    Call<inventory.analysis.catalogue.responsedataset.ResponseColorOptionDataset> getInventoryColorOptionList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetItemsDetails")
    Call<inventory.analysis.catalogue.responsedataset.ResponseItemDetailsDataset> getInventoryItemDetails(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetItemImageList")
    Call<inventory.analysis.catalogue.responsedataset.ResponseImageListDataset> getInventoryImageList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetWishListItems")
    Call<inventory.analysis.catalogue.wishlist.dataset.ResponseWishListDataset> getInventoryWishList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("GetCartListItems")
    Call<inventory.analysis.catalogue.addtobox.dataset.ResponseBoxListDataset> getInventoryBoxList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("AddRemoveWishlistItem")
    Call<inventory.analysis.catalogue.responsedataset.ResponseAddOrRemoveDataset> getInventoryStatusForWishListAddOrDelete(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("AddItemInCart")
    Call<inventory.analysis.catalogue.responsedataset.ResponseAddItemInBoxDataset> setInventoryAddItemInCart(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("RemoveItemFromCart")
    Call<inventory.analysis.catalogue.addtobox.responsedataset.ResponseRemoveToBoxDataset> InventoryRemoveItemFromCart(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("CartToWishlistMove")
    Call<inventory.analysis.catalogue.addtobox.responsedataset.ResponseMoveToWishlistDataset> InventoryMoveToWishList(@FieldMap Map<String, String> params);













    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadMultipleFiles(@Part("description") RequestBody name,@Part MultipartBody.Part file);

}

