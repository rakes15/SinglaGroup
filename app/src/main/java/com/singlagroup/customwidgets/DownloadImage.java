package com.singlagroup.customwidgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import com.singlagroup.LoginActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.FileOutputStream;
import uploadimagesfiles.ImageUplodingAcitvity;

/**
 * Created by rakes on 06-Nov-17.
 */

public class DownloadImage {

    static String Path = "";

    public static String ImageDownload(final Context context, String ImageUrl, final String FileName){
        //Log.d(TAG,"ImageUrl:"+ImageUrl);
        Path = "";
        Picasso.with(context)
                .load(ImageUrl)
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              try {

                                  String root = Environment.getExternalStorageDirectory().toString();
                                  File myDir = new File(root + "/SinglaGroups/temp/");

                                  if (!myDir.exists()) {
                                      myDir.mkdirs();
                                  }

                                  String name = FileName + ".jpg";
                                  myDir = new File(myDir, name);
                                  String Msg = "Image already downloaded ";
                                  if (!myDir.exists()) {
                                      FileOutputStream out = new FileOutputStream(myDir);
                                      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                                      out.flush();
                                      out.close();
                                      Msg = "Image downloaded successfully";
                                  }else{
                                      myDir.delete();
                                      FileOutputStream out = new FileOutputStream(myDir);
                                      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                                      out.flush();
                                      out.close();
                                      Msg = "Existing downloaded image replaced";
                                  }
                                  Path = myDir.getAbsolutePath().toString();
                              } catch(Exception e){
                                  // some action
                                  MessageDialog.MessageDialog(context,"",""+e.toString());
                              }
                          }

                          @Override
                          public void onBitmapFailed(Drawable errorDrawable) {
                              MessageDialog.MessageDialog(context,"Failed",""+errorDrawable.toString());
                          }
                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {}
                      }
                );
        return Path;
    }
}
