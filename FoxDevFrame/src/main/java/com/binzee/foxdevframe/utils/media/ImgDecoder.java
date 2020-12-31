package com.binzee.foxdevframe.utils.media;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.PostProcessor;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 基于ImageDecoder的图片解码工具
 *
 * @author 狐彻
 * 2020/10/27 10:20
 */
@TargetApi(Build.VERSION_CODES.P)
public class ImgDecoder {
    private static final String TAG = "ImgDecoder";

    //数据源
    @NonNull
    private final ImageDecoder.Source source;
    //头监听器数组
    private final Set<ImageDecoder.OnHeaderDecodedListener> decodeListenerSet
            = new HashSet<>();
    //不完全解码监听器，若返回true则展示不完全的图片
    private ImageDecoder.OnPartialImageListener partialImageListener = null;
    //图片预处理器
    private PostProcessor postProcessor = null;
    //尺寸撤职监听器角标
    private int[] size = null;
    private int sampleSize = -1;   //清晰度监
    //真正的头加载监听器，用户设置的监听将在这里调用
    private final ImageDecoder.OnHeaderDecodedListener mMainHeadListener
            = (decoder, info, source) -> {

        //设置各个监听
        if (postProcessor != null) decoder.setPostProcessor(postProcessor);
        if (partialImageListener != null) decoder.setOnPartialImageListener(partialImageListener);
        if (sampleSize != -1) decoder.setTargetSampleSize(sampleSize);
        if (size != null) decoder.setTargetSize(size[0], size[1]);
        for (ImageDecoder.OnHeaderDecodedListener listener: decodeListenerSet)
            listener.onHeaderDecoded(decoder, info, source);
    };


    /**
     * 构造器
     *
     * @author 狐彻 2020/10/27 10:29
     */
    private ImgDecoder(@NonNull ImageDecoder.Source source) {
        this.source = source;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 业务方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 解码为Drawable
     *
     * @author 狐彻 2020/10/27 10:30
     */
    public Drawable decodeDrawable() throws IOException {
        return ImageDecoder.decodeDrawable(source, mMainHeadListener);
    }

    /**
     * 解码为位图
     *
     * @author 狐彻 2020/10/27 10:31
     */
    public Bitmap decodeBitmap() throws IOException {
        return ImageDecoder.decodeBitmap(source, mMainHeadListener);
    }

    /**
     * 设置头监听器
     *
     * @author 狐彻 2020/10/27 10:38
     */
    public ImgDecoder setHeadDecodeListener(ImageDecoder.OnHeaderDecodedListener listener) {
        decodeListenerSet.add(listener);
        return this;
    }

    /**
     * 设置图片预处理方法
     *
     * @author 狐彻 2020/10/27 10:32
     */
    public ImgDecoder setPostProcessor(PostProcessor postProcessor) {
        this.postProcessor = postProcessor;
        return this;
    }

    /**
     * 设置不完整加载监听器
     *
     * @author 狐彻 2020/10/27 10:42
     */
    public ImgDecoder setPartialImageListener(ImageDecoder.OnPartialImageListener listener) {
        partialImageListener = listener;
        return this;
    }

    /**
     * 设置图片尺寸
     *
     * @author 狐彻 2020/10/27 10:40
     */
    public ImgDecoder setSize(final int width, final int height) {
        size = new int[]{width, height};
        return this;
    }

    /**
     * 设置图片压缩
     *
     * @author 狐彻 2020/10/27 10:41
     */
    public ImgDecoder setSampleSize(final int sampleSize){
        this.sampleSize = sampleSize;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 预设图片处理器
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 设置圆角处理器
     *
     * @author 狐彻 2020/10/27 10:44
     */
    public ImgDecoder setRoundCornersPostProcessor(final float roundX, final float roundY) {
        postProcessor = canvas -> {
            Path path = new Path();
            path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            path.addRoundRect(0 ,0, width, height, roundX, roundY
                    , Path.Direction.CW);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.TRANSPARENT);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            canvas.drawPath(path, paint);
            return PixelFormat.TRANSLUCENT;
        };
        return this;
    }
}
