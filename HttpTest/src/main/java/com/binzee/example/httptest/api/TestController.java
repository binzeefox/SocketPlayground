package com.binzee.example.httptest.api;

import android.util.Log;

import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RestController;

/**
 * @author tong.xw
 * 2020/12/30 15:20
 */
@RestController
@RequestMapping(path = "/test")
public class TestController {
    private static final String TAG = "TestController";

    @GetMapping(path = "/test0")
    public String testApi(@QueryParam("arg0") String arg0) {
        Log.d(TAG, "testApi: called " + arg0);
        return "Hello World";
    }
}
