package com.common.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author liyaotang
 * @date 2018/8/30
 */
public class FileUtil {

    /** 在根目录创建文件fileName，并且写入bytes */
    static public void createFileWriteByte(String fileName, byte[] bytes) {

        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        File file = new File(Environment.getExternalStorageDirectory(),
                fileName);
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            file.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * 用当前时间生成文件名，文件名格式yyyyMMdd_HHmmsss，/directory/yyyyMMdd_HHmmsss.suffix
     * @param suffix 文件后缀，可以为空
     * @param directory 文件所在文件夹名字，如果为空，则默认在根目录，如/storage/emulated/0/。
     *                  不需要包含根目录，如/storage/emulated/0/dir/sub_dir，只需要传入"/dir/sub_dir"
     * @param isPath 是否返回文件绝对地址，true，返回文件绝对地址，false，返回文件名
     * @return 文件地址或者文件名
     */
    static public String getFileNameByDate(String suffix, String directory, boolean isPath) {

        // 由日期创建文件名
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmsss", Locale.getDefault());
        String dateString = format.format(date);
        String fileName = dateString + "." + suffix;

        // 只返回文件名
        if (!isPath) return fileName;


        // 获取根目录
        String rootDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 拼接目录
        if (directory != null && !directory.equals("")) {
            // 加上文件分隔符，即斜杆
            if (!rootDirectory.endsWith(File.separator) &&
                    !directory.startsWith(File.separator)) {
                rootDirectory = rootDirectory + File.separator + directory;
            }
            else {
                rootDirectory = rootDirectory + directory;
                rootDirectory.replace(File.separator, File.separator + File.separator);
            }

            // 如果文件夹不存在, 则创建
            File fileDirectory = new File(rootDirectory);
            if (!fileDirectory.exists())
                // 创建失败则返回null
                if (!fileDirectory.mkdirs())
                    return null;
        }


        // 完整路径
        File file = new File(rootDirectory, fileName);
        String filePath = null;
        try {
            filePath = file.getCanonicalPath();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }

    /**
     * 获取文件名。文件路径，去除文件夹，去除文件格式后缀，得到文件名
     * @param filePath 文件路径
     * @return
     */
    public static String getFileNameInPath(String filePath) {
        if (filePath == null || filePath.equals("")) return  null;

        File file = new File(filePath);
        // 包含了文件格式后缀
        String name = file.getName();
        String[] strs = name.split("\\.");

        return strs[0];
    }

    /**
     * 在根目录创建文件夹path，并把绝对地址返回
     * @param directory 根目录下的目录地址
     * @return 文件夹的绝对地址，包含根目录在前头，
     */
    public static String createDirectoryInRootPath(String directory) {
        if (directory == null || directory.equals("")) return null;
        // 检测字符合法性
        ;;;

        // 获取根目录
        String rootDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 拼接目录，加上必要的文件分隔符，去除重复的文件分隔符
        if (!rootDirectory.endsWith(File.separator) &&
                !directory.startsWith(File.separator)) {
            rootDirectory = rootDirectory + File.separator + directory;
        }
        else {
            rootDirectory = rootDirectory + directory;
            rootDirectory.replace(File.separator, File.separator + File.separator);
        }

        // 如果文件夹不存在, 则创建
        File fileDirectory = new File(rootDirectory);
        if (!fileDirectory.exists())
            // 创建失败则返回null
            if (!fileDirectory.mkdirs())
                return null;
        return rootDirectory;
    }

    public static void copyFileUsingFileStreams(File source, File dest)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    public static String getPathFromUri(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 根据Uri获取文件的绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param fileUri
     */
    @TargetApi(19)
    public static String getFileAbsolutePath(Context context, Uri fileUri) {
        if (context == null || fileUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
