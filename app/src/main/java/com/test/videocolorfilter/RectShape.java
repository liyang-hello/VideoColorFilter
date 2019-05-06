package com.test.videocolorfilter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by liyang on 2019/5/5.
 */

public class RectShape {

    private float width = 2;
    private float height = 2;

    private int mTextureId = 0;
    private int mProgram = -1;
    private int mPositionHandle;
    private int mTexCoordHandle;
    private int mMVPHandle;
    private int mColorFilterHandle;
    private FloatBuffer mVertices, mTexCoord;
    private float[] mModelProjection = new float[16];
    private float[] mColorFilterArray = new float[16];

    private final static String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "attribute vec4 a_Position;\n" +
                    "attribute vec2 aTexCoor;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform float uAngle;\n" +
                    "void main(){\n" +
                    "    gl_Position = uMVPMatrix * a_Position;\n" +
                    "    vTextureCoord = aTexCoor;\n" +
                    "}\n";
    private final static String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision highp float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES uTexture;\n" +
                    "uniform mat4 uColorFilterMatrix;\n" +
                    "void main() {\n" +
                    "     vec4 val = texture2D(uTexture, vTextureCoord); \n" +
                    "     gl_FragColor = val*uColorFilterMatrix; \n" +
                    "}\n";

    public RectShape() {
        init();
    }


    protected void init() {

        Matrix.setIdentityM(mModelProjection, 0);
        Matrix.setIdentityM(mColorFilterArray, 0);

        float[] vertices = new float[]{
                -width / 2, -height / 2, 0,
                width / 2, height / 2, 0,
                -width / 2, height / 2, 0,
                width / 2, height / 2, 0,
                -width / 2, -height / 2, 0,
                width / 2, -height / 2, 0,
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertices = vbb.asFloatBuffer();
        mVertices.put(vertices);
        mVertices.position(0);

        // create program
        mProgram = GLUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        mMVPHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mColorFilterHandle = GLES20.glGetUniformLocation(mProgram, "uColorFilterMatrix");
        //setup buffers for the texture 2D coordinates
        float[] texCoord = new float[]{
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
        };

        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoord.length * 4);
        cbb.order(ByteOrder.nativeOrder()); //set the byte order
        mTexCoord = cbb.asFloatBuffer(); //convert to byte buffer
        mTexCoord.put(texCoord); //load data to byte buffer
        mTexCoord.position(0); //set the start point

    }

    public void setTextureId(int textureId) {
        mTextureId = textureId;
    }

    public void draw() {
        draw(mTextureId);
    }

    public void draw(int textureId) {
//        Log.d("RectShape", "draw "+ textureId);
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(mMVPHandle, 1, false, mModelProjection, 0);
        GLES20.glUniformMatrix4fv(mColorFilterHandle, 1, false, mColorFilterArray, 0);

        if (textureId > 0) {
            // render texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        }
        // draw triangles
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTexCoord);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertices.capacity() / 3);

    }

    public void setColorFilterArray(float filter[]) {
        System.arraycopy(filter, 0, mColorFilterArray, 0, 16);
    }


    public void release() {
        if (mProgram >= 0) {
            GLES20.glDeleteProgram(mProgram);
            mProgram = -1;
        }
    }

}
