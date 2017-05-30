package com.example.ihortovpinets.myaccounts;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by itovp on 22.05.2017.
 */

public class StatsFragment extends Fragment {

	public static final int STATS_FRAGMENT_ID = R.id.stats_frg_id;
	private PieChart mMoneyPerAccChart;
	private ArrayList<Account> myAccounts;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.main_page_stats_tab, null);
		mMoneyPerAccChart = (PieChart) view.findViewById(R.id.stats_frg_all_accounts_pieChart);
		myAccounts = new DBHelper(getActivity()).getAccListFromDB();
		updatePieChart(mMoneyPerAccChart);
		return view;
	}

	private void updatePieChart(PieChart pc) {
		ArrayList<Entry> entries = new ArrayList<>();
		ArrayList<String> labels = new ArrayList<>();
		int i = 0;// TODO: 24.05.2017 try with streams 

		for (Account account : myAccounts) {
			entries.add(new Entry((float) account.getDeposit(), i));
			i++;
			labels.add(account.getName());
		}

		PieDataSet dataset = new PieDataSet(entries, "Accounts");
		dataset.setColors(ColorTemplate.COLORFUL_COLORS);
		PieData data = new PieData(labels, dataset);
		pc.setData(data);
	}
}
