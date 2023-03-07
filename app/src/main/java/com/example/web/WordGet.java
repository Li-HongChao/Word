package com.example.web;

import com.example.entity.Word;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WordGet {
   public Word word;
   public String url;

    public Word getData() throws Exception {
        Word word = new Word();
        url = "https://v1.jinrishici.com/all.json";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        JSONObject json = new JSONObject(response.body().string());
        word.setAuthor(json.getString("author"));
        word.setContent(json.getString("content"));
        word.setOrigin(json.getString("origin"));
        word.setCategory(json.getString("category"));
        return word;
    }
}
