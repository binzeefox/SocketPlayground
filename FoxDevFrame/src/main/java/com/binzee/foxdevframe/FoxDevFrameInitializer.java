package com.binzee.foxdevframe;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import java.util.Collections;
import java.util.List;

/**
 * StartUp初始化工具
 *
 * @author 狐彻
 * 2020/10/21 8:22
 */
public class FoxDevFrameInitializer implements Initializer<FoxCore> {

    @NonNull
    @Override
    public FoxCore create(@NonNull Context context) {
        FoxCore.init(context);
        return FoxCore.get();
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return Collections.emptyList();
    }
}
