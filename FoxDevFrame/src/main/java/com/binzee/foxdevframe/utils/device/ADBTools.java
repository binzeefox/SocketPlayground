package com.binzee.foxdevframe.utils.device;

import androidx.annotation.NonNull;

import com.binzee.foxdevframe.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * adb工具
 *
 * @author 狐彻
 * 2020/11/11 9:06
 */
public class ADBTools {
    private static final String TAG = "ADBTools";

    /**
     * 执行命令
     *
     * @param command 命令
     * @return 结果
     * @author 狐彻 2020/11/11 9:08
     */
    public synchronized static String execute(String command) throws IOException {
        try (BufferedReader reader = getProcessReader(command)) {
            StringBuilder output = new StringBuilder();
            int read;
            char[] buffer = new char[1024];
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            LogUtil.d(TAG, "execute: content = " + output.toString());
            return output.toString();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 获取Reader
     *
     * @author 狐彻 2020/11/13 8:53
     */
    private static BufferedReader getProcessReader(@NonNull String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
    }
}
