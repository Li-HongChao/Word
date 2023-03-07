package com.example.web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PicGet {
    public Bitmap bitmap;
    public Bitmap getPic() throws IOException {
        String url ="https://api.dujin.org/bing/m.php";
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
        return bitmap;
    }
}
