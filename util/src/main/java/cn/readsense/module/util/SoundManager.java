package cn.readsense.module.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import cn.readsense.module.R;


/**
 * 播放短音(5.0+)
 */
public class SoundManager {

    private volatile static SoundManager soundManager;

    private static SoundPool mSoundPool;

    public static SoundManager getInstance() {
        if (soundManager == null) {
            synchronized (SoundManager.class) {
                if (soundManager == null) {
                    soundManager = new SoundManager();
                }
            }
        }
        return soundManager;
    }

    @SuppressWarnings("NewApi")
    private SoundManager() {
        AudioAttributes.Builder attrBuild = new AudioAttributes.Builder();
        attrBuild.setLegacyStreamType(AudioManager.STREAM_MUSIC);//音频类型
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(100)
                .setAudioAttributes(attrBuild.build())
                .build();
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    mSoundPool.play(sampleId, 1.0f, 1.0f,
                            1, 0, 1.0f);
                }
            }
        });
    }

    public void playSound(Context context, int res) {
        mSoundPool.load(context, res, 1);
    }

    public void playPassSound(Context context) {
        playSound(context, R.raw.sound_di);
    }

    public void playWarnSound(Context context) {
        playSound(context, R.raw.sound_du);
    }

    public void release() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }
}
