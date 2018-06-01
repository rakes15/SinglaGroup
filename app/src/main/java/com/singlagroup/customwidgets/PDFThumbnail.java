package com.singlagroup.customwidgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.FileProvider;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.singlagroup.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Created by rakes on 12-Oct-17.
 */

public class PDFThumbnail {
    private Context context;
    private static Random random;
    public static Bitmap GenerateImageFromPdf(Context context,String Path) {
        Bitmap bitmap=null;
        Uri pdfUri=null;
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            File file = new File(Path);
            if (file.exists())
            // So you have to use Provider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                pdfUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            }else{
                pdfUri = Uri.fromFile(file);
            }
            ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            bitmap = saveImage(bmp,file.getName());
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch(Exception e) {
            //todo with exception
        }
        return bitmap;
    }
    private static Bitmap saveImage(Bitmap bmp,String filename) {
        Bitmap bitmap=null;
        final String FOLDER = Environment.getExternalStorageDirectory() + "/SinglaGroups/temp";
        FileOutputStream out = null;
        random = new Random();
        try {
            File folder = new File(FOLDER);
            if(!folder.exists())
                folder.mkdirs();
                File file = new File(folder, filename+".png");
                out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                if (file.exists())
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                    bitmap.setDensity(200);
        } catch (Exception e) {
            //todo with exception
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //todo with exception
            }
        }
        return bitmap;
    }
    public static void DeleteFileOrDirectory(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            if(fileOrDirectory.listFiles()!=null) {
                for (File child : fileOrDirectory.listFiles())
                    DeleteFileOrDirectory(child);
                    fileOrDirectory.delete();
            }
    }

}
