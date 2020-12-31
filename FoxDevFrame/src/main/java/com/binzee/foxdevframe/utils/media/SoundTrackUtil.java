package com.binzee.foxdevframe.utils.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.binzee.foxdevframe.FoxCore;

import java.util.List;

/**
 * 声音工具类
 * <p>
 * 用于播放Raw文件内的声音
 *
 * @author tong.xw
 * 2020/12/17 19:07
 */
@SuppressWarnings("UnusedReturnValue")
public class SoundTrackUtil {
    private static final String TAG = "SoundTrackUtil";
    @NonNull
    private final SoundPool mPool;
    private final AudioManager mManager;

    private final SparseIntArray mIdArray = new SparseIntArray();   //存放流ID
    private final SparseIntArray mStreamArray = new SparseIntArray();   //存放流ID

    /**
     * 私有化构造器
     */
    private SoundTrackUtil(@NonNull SoundPool pool) {
        mManager = (AudioManager) FoxCore.getApplication().getSystemService(Context.AUDIO_SERVICE);
        mPool = pool;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 初始化重载
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 初始化
     *
     * @param pool 提供声音池
     */
    public static SoundTrackUtil create(@NonNull SoundPool pool) {
        return new SoundTrackUtil(pool);
    }

    /**
     * 初始化
     *
     * @param attributes 参数
     * @param maxStreams 最大音流数
     */
    public static SoundTrackUtil create(@Nullable AudioAttributes attributes, int maxStreams) {
        SoundPool.Builder builder = new SoundPool.Builder()
                .setMaxStreams(maxStreams);
        if (attributes != null) builder.setAudioAttributes(attributes);
        return new SoundTrackUtil(builder.build());
    }

    /**
     * 初始化
     *
     * @param maxStreams 最大音流数
     */
    public static SoundTrackUtil create(int maxStreams) {
        return create(null, maxStreams);
    }

    /**
     * 初始化
     * <p>
     * 默认单音流
     *
     * @param attributes 参数
     */
    public static SoundTrackUtil create(@NonNull AudioAttributes attributes) {
        return create(attributes, 1);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 业务方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 加载声音
     *
     * @param soundId 声音文件的ID
     */
    public SoundTrackUtil loadSound(@RawRes int soundId) {
        mIdArray.append(soundId, getSoundPool().load(FoxCore.getApplication(), soundId, 1));
        return this;
    }

    /**
     * 批量加载声音
     */
    public void loadSounds(@NonNull List<Integer> soundIdList) {
        for (int id : soundIdList)
            loadSound(id);
    }

    /**
     * 播放声音
     *
     * @param soundId 注册过的声音资源文件
     * @param loop    若0为不循环，-1为无限循环，其它数值为循环次数（播放次数等于循环次数+1）
     */
    public void play(@RawRes int soundId, int loop) {
        synchronized (mStreamArray) {
            float volRatio = getVolumeRatio();
            int streamId = getSoundPool().play(mIdArray.get(soundId), volRatio, volRatio, 0, loop, 1);
            mStreamArray.put(soundId, streamId);
        }
    }

    /**
     * 停止播放声音
     */
    public void silence() {
        synchronized (mStreamArray) {
            for (int i = 0; i < mStreamArray.size(); i++) {
                int streamId = mStreamArray.valueAt(i);
                mPool.stop(streamId);
            }
            mStreamArray.clear();
        }
    }

    /**
     * 暂停全部
     */
    public void pauseAll() {
        synchronized (mStreamArray) {
            for (int i = 0; i < mStreamArray.size(); i++) {
                int streamId = mStreamArray.valueAt(i);
                mPool.pause(streamId);
            }
        }
    }

    /**
     * 恢复全部
     */
    public void resumeAll() {
        synchronized (mStreamArray) {
            for (int i = 0; i < mStreamArray.size(); i++) {
                int streamId = mStreamArray.valueAt(i);
                mPool.resume(streamId);
            }
        }
    }

    /**
     * 播放一次声音
     *
     * @param soundId 注册过的声音ID
     */
    public void play(@RawRes int soundId) {
        play(soundId, 0);
    }

    /**
     * 暂停播放
     */
    public void pause(@RawRes int soundId) {
        synchronized (mStreamArray) {
            int streamId = mStreamArray.get(soundId, 0);
            if (streamId != 0) {
                mPool.pause(streamId);
            }
        }
    }

    /**
     * 恢复播放
     */
    public void resume(@RawRes int soundId) {
        synchronized (mStreamArray) {
            int streamId = mStreamArray.get(soundId, 0);
            if (streamId != 0) {
                mPool.resume(streamId);
            }
        }
    }

    /**
     * 停止播放
     */
    public void stop(@RawRes int soundId) {
        synchronized (mStreamArray) {
            int streamId = mStreamArray.get(soundId, 0);
            if (streamId != 0) {
                mPool.stop(streamId);
                mStreamArray.removeAt(mStreamArray.indexOfKey(soundId));
            }
        }
    }

    /**
     * 当前是否有声音流
     */
    public boolean isInStream() {
        synchronized (mStreamArray) {
            return mStreamArray.size() != 0;
        }
    }

    /**
     * 获取声音管理器
     */
    public AudioManager getAudioManager() {
        return mManager;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 获取音轨
     */
    private SoundPool getSoundPool() {
        return mPool;
    }

    /**
     * 获取音量
     */
    private float getVolumeRatio() {
        // 获取最大音量值
        float audioMaxVolume = mManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        // 不断获取当前的音量值
        float audioCurrentVolume = mManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        //最终影响音量
        return Math.max(audioCurrentVolume / audioMaxVolume, 0.6f);
    }
}
