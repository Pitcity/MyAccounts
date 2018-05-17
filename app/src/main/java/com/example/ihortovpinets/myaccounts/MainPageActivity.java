package com.example.ihortovpinets.myaccounts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okio.BufferedSink;

public class MainPageActivity extends AppCompatActivity {


    private ViewPager mViewPager;

    int mTabs[] = {AccountsFragment.ACCOUNTS_FRAGMENT_ID, StatsFragment.STATS_FRAGMENT_ID};
    ArrayList<String> mNames = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        mNames.add(getString(R.string.account_frg_name));
        mNames.add(getString(R.string.stats_frg_name));

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "TryIt");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            startActivity(new Intent(getApplicationContext(), TryItActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frg = null;
            switch (mTabs[position]) {
                case AccountsFragment.ACCOUNTS_FRAGMENT_ID:
                    frg = new AccountsFragment();
                    break;
                case StatsFragment.STATS_FRAGMENT_ID:
                    frg = new StatsFragment();// todo statistic from scratch
                    break;
            }
            return frg;
        }

        @Override
        public int getCount() {
            return mTabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mNames.get(position);
        }
    }
}