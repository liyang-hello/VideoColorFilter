package com.test.videocolorfilter;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.widget.SeekBar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private GLSurfaceView mVideoView = null;
    private SeekBar seekBarHue;
    private SeekBar seekBarSaturation;
    private SeekBar seekBarLightness;

    private ColorFilterMatrixUtil mColorFilterMatrixUtil = new ColorFilterMatrixUtil();
    private RectShape mReactShape = null;
    private MediaPlayer mMediaPlayer = null;
    private Surface mSurface = null;
    private SurfaceTexture mSurfaceTexture = null;

    private boolean mFrameAvailable = false;

    private float mHueValue = 0;
    private float mSaturationValue = 1;
    private float mLightnessValue = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();

    }

    private void initView() {
        mVideoView = findViewById(R.id.video_view);
        seekBarHue = (SeekBar) findViewById(R.id.bar_hue);
        seekBarSaturation = (SeekBar) findViewById(R.id.bar_saturation);
        seekBarLightness = (SeekBar) findViewById(R.id.bar_lightness);
    }

    private void initData() {
        mVideoView.setEGLContextClientVersion(2);
        mVideoView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                mReactShape = new RectShape();
                int textureId = GLUtil.generateOESTexture();
                mSurfaceTexture = new SurfaceTexture(textureId);
                mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        mFrameAvailable = true;
                    }
                });
                mSurface = new Surface(mSurfaceTexture);
                mReactShape.setTextureId(textureId);
                setupPlayer();
            }

            @Override
            public void onSurfaceChanged(GL10 gl10, int i, int i1) {

            }

            @Override
            public void onDrawFrame(GL10 gl10) {
                if (mFrameAvailable) {
                    mSurfaceTexture.updateTexImage();
                    mFrameAvailable = false;
                }

                float[] colorFilter = mColorFilterMatrixUtil.getColorFilterArray16();
                mReactShape.setColorFilterArray(colorFilter);
                mReactShape.draw();
            }
        });
    }

    private void setListener() {
        seekBarHue.setOnSeekBarChangeListener(this);
        seekBarSaturation.setOnSeekBarChangeListener(this);
        seekBarLightness.setOnSeekBarChangeListener(this);
    }


    private void setupPlayer() {
        try {
            mMediaPlayer = MediaPlayer.create(this, R.raw.testfile);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setLooping(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mMediaPlayer.start();
            }
        });

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mHueValue = (seekBarHue.getProgress() - 128f) * 1.0f / 128f * 180;
        mSaturationValue = seekBarSaturation.getProgress() / 128f;
        mLightnessValue = seekBarLightness.getProgress() / 128f;

        mColorFilterMatrixUtil.setHue(mHueValue);
        mColorFilterMatrixUtil.setSaturation(mSaturationValue);
        mColorFilterMatrixUtil.setLightness(mLightnessValue);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
