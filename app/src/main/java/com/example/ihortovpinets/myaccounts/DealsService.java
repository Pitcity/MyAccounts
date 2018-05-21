package com.example.ihortovpinets.myaccounts;

import android.content.Context;

import com.example.ihortovpinets.myaccounts.Entity.Account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

class DealsService {

    static String address;
    Context mContext;

    public DealsService(Context context) {
        mContext = context;
    }

    public void requestDeals(String accountId) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(address + "/dealsFroAcc_" + accountId).method("GET", null)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("ressult :  fail" + request.toString() + "\n");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String str = response.body().string();
                    DBHelper db = new DBHelper(mContext);
                    try {
                        JSONArray jArray = new JSONArray(str);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject obj = jArray.optJSONObject(i);
                            DealDTO deal = new DealDTO(obj.getString("seller"), obj.getString("buyer"), obj.getLong("date"), obj.getDouble("sum"), obj.getString("note"), obj.getString("id"));
                            db.addDealToDB(deal);

                            System.out.println("ressult : succ" + deal.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
