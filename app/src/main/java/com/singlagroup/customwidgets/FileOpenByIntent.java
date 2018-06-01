package com.singlagroup.customwidgets;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

/**
 * Created by rakes on 03-Aug-17.
 */

public class FileOpenByIntent {

    public static void FileOpen(Context context,String path,Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Add in case of if We get Uri from fileProvider.
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if (path.toString().contains(".doc") || path.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(path.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(path.toString().contains(".ppt") || path.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(path.toString().contains(".xls") || path.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(path.toString().contains(".zip") || path.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if(path.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if(path.toString().contains(".wav") || path.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(path.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(path.toString().contains(".jpg") || path.toString().contains(".jpeg") || path.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(path.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(path.toString().contains(".3gp") || path.toString().contains(".mpg") || path.toString().contains(".mpeg") || path.toString().contains(".mpe") || path.toString().contains(".mp4") || path.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static String FileType(String path){
        String Result = "";
        if (path.contains(".doc") || path.contains(".docx")) {
            // Word document
            Result = "Document File";
        } else if(path.contains(".pdf")) {
            // PDF file
            Result = "PDF File";
        } else if(path.contains(".ppt") || path.contains(".pptx")) {
            // Powerpoint file
            Result = "PPT File";
        } else if(path.contains(".xls") || path.contains(".xlsx")) {
            // Excel file
            Result = "Excel File";
        } else if(path.contains(".zip") || path.contains(".rar")) {
            // WAV audio file
            Result = "Zip File";
        } else if(path.contains(".rtf")) {
            // RTF file
            Result = "RTF File";
        } else if(path.contains(".wav") || path.contains(".mp3")) {
            // WAV audio file
            Result = "WAV File";
        } else if(path.contains(".gif")) {
            // GIF file
            Result = "GIF Image File";
        } else if(path.contains(".jpg") || path.contains(".jpeg") || path.contains(".png")) {
            // JPG file
            Result = "Image File";
        } else if(path.contains(".txt")) {
            // Text file
            Result = "Text File";
        } else if(path.contains(".3gp") || path.contains(".mpg") || path.contains(".mpeg") || path.contains(".mpe") || path.contains(".mp4") || path.contains(".avi")) {
            // Video files
            Result = "Video File";
        } else {
            Result = "Other File";
        }
        return Result;
    }
}
