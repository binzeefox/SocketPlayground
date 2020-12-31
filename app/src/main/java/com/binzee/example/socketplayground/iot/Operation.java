package com.binzee.example.socketplayground.iot;

/**
 * 网关操作
 *
 * @author tong.xw
 * 2020/12/28 17:27
 */
public class Operation {
    private String requestCode; //若为空则无需返回值
    private String fromDeviceId;
    private String toDeviceId;
    private String methodName;
    private String data;

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public String getFromDeviceId() {
        return fromDeviceId;
    }

    public void setFromDeviceId(String fromDeviceId) {
        this.fromDeviceId = fromDeviceId;
    }

    public String getToDeviceId() {
        return toDeviceId;
    }

    public void setToDeviceId(String toDeviceId) {
        this.toDeviceId = toDeviceId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
