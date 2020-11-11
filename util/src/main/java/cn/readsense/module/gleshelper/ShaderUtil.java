package cn.readsense.module.gleshelper;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES30;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ShaderUtil {
    private static final String TAG = "ShaderUtil:GLES30";

    /**
     * 加载着色器方法
     */
    public static int loadShader(int shaderType, String source) {
        int shader = GLES30.glCreateShader(shaderType);//创建一个shader，并记录id
        if (shader != 0) {
            GLES30.glShaderSource(shader, source);//加载着色器源代码

            GLES30.glCompileShader(shader);//编译着色器源代码

            int[] compiled = new int[1];//获取着色器编译情况
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

            if (compiled[0] == GLES30.GL_FALSE) {
                llog("编译着色器代码失败! shaderType：" + shaderType);
                llog(GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * 创建着色器程序方法
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) return 0;//加载顶点着色器

        int pixelShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) return 0;//加载片元着色器

        int program = GLES30.glCreateProgram();//创建程序

        if (program != 0) {
            GLES30.glAttachShader(program, vertexShader);//向程序加载着色器
            checkGLError("glAttach_vertexShader");
            GLES30.glAttachShader(program, pixelShader);
            checkGLError("glAttach_pixelShader");

            GLES30.glLinkProgram(program);//链接程序

            int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES30.GL_TRUE) {//查看链接状态，若链接失败则报错并删除程序
                llog("link program failed！！");
                llog(GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * 检查每一步操作是否有错误的方法
     *
     * @param op
     */
    public static void checkGLError(String op) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            llog(op + ":glError " + error);
            throw new RuntimeException(op + ":glError " + error);
        }
    }

    /**
     * 从sh脚本中加载着色器内容的方法
     *
     * @param fname
     * @param r
     * @return
     */
    public static String loadFromAssetsFile(String fname, Resources r) {
        String result = null;
        try {
            InputStream inputStream = r.getAssets().open(fname);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = inputStream.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            result = new String(buff, StandardCharsets.UTF_8);
            result = result.replaceAll("\\r\\n", "\n");
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从sh脚本中加载着色器内容的方法
     */
    public static String loadFromRaw(Context context, int id) {
        String result = null;
        try {
            InputStream inputStream = context.getResources().openRawResource(id);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = inputStream.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            result = new String(buff, StandardCharsets.UTF_8);
            result = result.replaceAll("\\r\\n", "\n");
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] readRaw(Context context, String name) {
        try {
            InputStream inputStream = context.getAssets().open(name);
            int length = inputStream.available();
            byte[] data = new byte[length];
            inputStream.read(data);
            inputStream.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static void llog(String msg) {
        Log.e(TAG, msg);
    }
}