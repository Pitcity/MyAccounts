package com.example.ihortovpinets.myaccounts.Service;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.example.ihortovpinets.myaccounts.DBHelper;
import com.example.ihortovpinets.myaccounts.DTO.AccountDto;
import com.example.ihortovpinets.myaccounts.DTO.DealDTO;
import com.example.ihortovpinets.myaccounts.Entity.Account;
import com.example.ihortovpinets.myaccounts.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class SyncService {

	public static String address;
	Context mContext;

	public SyncService(Context context) {
		mContext = context;
	}

	public interface RequestListener {
		void onResponse();
		void onFailure();
	}

	public void requestAccounts(View view, RequestListener rl) {
		OkHttpClient client = new OkHttpClient();
		SyncService.address = ((EditText) view.findViewById(R.id.adress)).getText().toString();//todo make smth with this ip
		final Request request = new Request.Builder()
				.url(SyncService.address + "/getAccListAll").method("POST", new RequestBody() {
					@Override
					public MediaType contentType() {
						return MediaType.parse("application/json");
					}

					@Override
					public void writeTo(BufferedSink sink) throws IOException {

					}
				})
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				System.out.println("ressult :  fail" + request.toString() + "\n");
				rl.onFailure();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					throw new IOException("Unexpected code " + response);
				} else {
					String str = response.body().string();
					final ArrayList<Account> myAccounts = new ArrayList<Account>();
					try {
						JSONArray jArray = new JSONArray(str);
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject obj = jArray.optJSONObject(i);
							Account acc = new Account(obj.getString("id"), obj.getString("name"), obj.getDouble("deposit"), obj.getString("description"), obj.getBoolean("isOuter"));
							myAccounts.add(acc);

							System.out.println("ressult : succ" + acc.toString());
						}
						new DBHelper(mContext).updateAccountsWithServer(myAccounts);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					rl.onResponse();
				}
			}
		});
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
							if (!(db.isDealWithIdExists(deal.getId()))) {
								db.addDealToDB(deal);
								System.out.println("ressult : succ" + deal.toString());
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void saveDeal(DealDTO dealDTO) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), dealDTO.toString());

		final Request request = new Request.Builder()
				.url(address + "/addDeal").post(requestBody)
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
					System.out.println("ressult : succ" + dealDTO.toString());
				}
			}
		});
	}

	public void updateAcc(AccountDto accountDto) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), accountDto.toString());

		final Request request = new Request.Builder()
				.url(address + "/updateAcc").post(requestBody)
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
					System.out.println("ressult : succ" + accountDto.toString());
				}
			}
		});
	}

	public void saveAccount(AccountDto accountDto) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), accountDto.toString());

		final Request request = new Request.Builder()
				.url(address + "/addAcc").post(requestBody)
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
					System.out.println("ressult : succ" + accountDto.toString());
				}
			}
		});
	}

	public void deleteAcc(String accountId) {
		OkHttpClient client = new OkHttpClient();
		final Request request = new Request.Builder()
				.url(address + "/deleteAcc_" + accountId).method("GET", null)
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
					final ArrayList<Account> myAccounts = new ArrayList<Account>();
					try {
						JSONArray jArray = new JSONArray(str);
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject obj = jArray.optJSONObject(i);
							Account acc = new Account(obj.getString("id"), obj.getString("name"), obj.getDouble("deposit"), obj.getString("description"), obj.getBoolean("isOuter"));
							myAccounts.add(acc);

							System.out.println("ressult : succ" + acc.toString());
						}
						new DBHelper(mContext).updateAccountsWithServer(myAccounts);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
