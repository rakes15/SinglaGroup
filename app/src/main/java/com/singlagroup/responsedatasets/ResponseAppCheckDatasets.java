package com.singlagroup.responsedatasets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.singlagroup.datasets.Result;

public class ResponseAppCheckDatasets {

@SerializedName("status")
@Expose
private Integer status;
@SerializedName("msg")
@Expose
private String msg;
@SerializedName("Result")
@Expose
private Result result;

public Integer getStatus() {
return status;
}

public void setStatus(Integer status) {
this.status = status;
}

public String getMsg() {
return msg;
}

public void setMsg(String msg) {
this.msg = msg;
}

public Result getResult() {
return result;
}

public void setResult(Result result) {
this.result = result;
}

}