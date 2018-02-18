package com.example.ihortovpinets.myaccounts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

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