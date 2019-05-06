package com.test.videocolorfilter;

import android.graphics.ColorMatrix;

/**
 * Created by liyang on 2019/5/5.
 */

public class ColorFilterMatrixUtil {

    private final float IDENTIFY_MATRIX[] = {
            1, 0, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 0, 1, 0
    };

    float mHueValue = 0;
    float mSaturationValue = 1;
    float mLightnessValue = 1;

    private ColorMatrix mCurrentColorFilter = new ColorMatrix(IDENTIFY_MATRIX);


    public ColorFilterMatrixUtil() {

    }

    public void setHue(float hue) {
        mHueValue = hue;
        setColorFilter(mHueValue, mSaturationValue, mLightnessValue);
    }

    public void setSaturation(float saturation) {
        mSaturationValue = saturation;
        setColorFilter(mHueValue, mSaturationValue, mLightnessValue);
    }

    public void setLightness(float lightness) {
        mLightnessValue = lightness;
        setColorFilter(mHueValue, mSaturationValue, mLightnessValue);
    }

    public final float[] getColorFilterArray() {
        return mCurrentColorFilter.getArray();
    }

    public final float[] getColorFilterArray16() {
        float[] colorMatrixArray = mCurrentColorFilter.getArray();
        float filter[] = new float[16];
        for (int i = 0; i < 20; i++) {
            int row = i / 5;
            int column = i % 5;
            if (column != 4) {
                filter[row * 4 + column] = colorMatrixArray[i];
            }
        }
        return filter;
    }

    private void setColorFilter(float mHueValue, float mSaturationValue, float mLightnessValue) {
        ColorMatrix mHueMatrix = new ColorMatrix();
        ColorMatrix mSaturationMatrix = new ColorMatrix();
        ColorMatrix mLightnessMatrix = new ColorMatrix();

        mHueMatrix.reset();
        mHueMatrix.setRotate(0, mHueValue);
        mHueMatrix.setRotate(1, mHueValue);
        mHueMatrix.setRotate(2, mHueValue);

        mSaturationMatrix.reset();
        mSaturationMatrix.setSaturation(mSaturationValue);

        mLightnessMatrix.reset();
        mLightnessMatrix.setScale(mLightnessValue, mLightnessValue, mLightnessValue, 1);

        mCurrentColorFilter.reset();
        mCurrentColorFilter.postConcat(mLightnessMatrix);
        mCurrentColorFilter.postConcat(mSaturationMatrix);
        mCurrentColorFilter.postConcat(mHueMatrix);

    }
}
