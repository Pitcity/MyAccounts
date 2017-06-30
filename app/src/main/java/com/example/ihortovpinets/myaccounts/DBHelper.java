package com.example.ihortovpinets.myaccounts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by IhorTovpinets on 05.11.2016.
 */

class DBHelper extends SQLiteOpenHelper {

	private final static int DB_VER = 5; //5 for testing, 2 for state version

	private final static String DB_NAME = "MyAccounts.db";
	private final static String TABLE_ACCOUNTS_NAME = "Accounts";
	private final static String TABLE_DEALS_NAME = "Deals";

	private SQLiteDatabase mDatabase = null;

	DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
		mDatabase = this.getReadableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(TABLE_ACCOUNTS_CREATE);
		sqLiteDatabase.execSQL(TABLE_DEALS_CREATE);
		fillDb(sqLiteDatabase);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		//sqLiteDatabase.execSQL(ALTER_TABLE1.replace("[table_name]", TABLE_DEALS_NAME));
		//sqLiteDatabase.execSQL(TABLE_DEALS_CREATE);
		//sqLiteDatabase.execSQL(INSERT_INTO.replace("[table_name]", TABLE_DEALS_NAME));
		//sqLiteDatabase.execSQL(DROP);
		sqLiteDatabase.execSQL(TABLE_ACCOUNTS_DROP);
		sqLiteDatabase.execSQL(TABLE_DEALS_DROP);
		onCreate(sqLiteDatabase);
	}

	private void fillDb(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL("INSERT INTO Accounts(AccName, deposit, description, isOuter) VALUES('FirstAcc',2000,'lalala','false');");
		sqLiteDatabase.execSQL("INSERT INTO Accounts(AccName, deposit, description, isOuter) VALUES('SecondAcc',7000,'lalala','false');");
		sqLiteDatabase.execSQL("INSERT INTO Accounts(AccName, deposit, description, isOuter) VALUES('ThirdAcc',5000,'lalala','false');");
		sqLiteDatabase.execSQL("INSERT INTO Deals (seller, buyer, note, sum, date) VALUES('FirstAcc','SecondAcc','commentFor first deal',1000,'19-вер.-2016');");
	}

	void addAccountToDB(Account acc) {
		mDatabase.execSQL(ADD_ACCOUNT_PATTERN
				.replace("[NAME]", acc.getName())
				.replace("[DEPOSIT]", Double.toString(acc.getDeposit()))
				.replace("[DESCR]", acc.getDescription())
				.replace("[IS_OUTER]", Boolean.toString(acc.isOuter))
		);
	}

	void addDealToDB(Deal deal) {
		mDatabase.execSQL(ADD_DEAL_PATTERN.replace("[SELLER]", deal.getSeller().getName())
				.replace("[BUYER]", deal.getBuyer().getName())
				.replace("[NOTE]", deal.getNote())
				.replace("[SUM]", Double.toString(deal.getSum()))
				.replace("[DATE]", Long.toString(deal.getDateLong()))
		);
	}

	ArrayList<Account> getAccListFromDB() {
		Cursor resultSet = mDatabase.rawQuery("SELECT * FROM Accounts", null);//// TODO: 13.05.2017 replace * with fields
		ArrayList<Account> myAccounts = new ArrayList<>();
		if (resultSet.moveToFirst()) {
			do {
				myAccounts.add(new Account(resultSet.getString(0), Double.valueOf(resultSet.getString(1)), resultSet.getString(2), Boolean.valueOf(resultSet.getString(3)), resultSet.getInt(4)));
			} while (resultSet.moveToNext());
		}
		resultSet.close();
		return myAccounts;
	}

	ArrayList<Deal> getDealListFromDB() {
		Cursor resultSet = mDatabase.rawQuery("SELECT * FROM Deals", null);//// TODO: 13.05.2017 replace * with fields
		ArrayList<Deal> myDeals = new ArrayList<>();
		if (resultSet.moveToFirst()) {
			do {
				myDeals.add(Deal.createDeal(new Account(resultSet.getString(1), true),
						new Account(resultSet.getString(0), true), resultSet.getString(2),
						Double.valueOf(resultSet.getString(3)), resultSet.getLong(4)));//// TODO: 13.05.2017 do smth with all that numbers -_-
			}
			while (resultSet.moveToNext());
		}
		resultSet.close();
		return myDeals;
	}

	void deleteAccFromBD(String nameOfAcc) {
		mDatabase.execSQL("DELETE FROM Accounts WHERE AccName='" + nameOfAcc + "'");
		updateDeals(mDatabase);
	}

	void updateAcc(Account acc) {
		mDatabase.execSQL(UPDATE_ACCOUNT
				.replace("[DEPOSIT]", Double.toString(acc.getDeposit()))
				.replace("[DESCR]", acc.getDescription())
				.replace("[IS_OUTER]", Boolean.toString(acc.isOuter))
				.replace("[ACC_NAME]", acc.getName())
		);
	}

	private void updateDeals(SQLiteDatabase sqLiteDatabase) { //// TODO: 13.05.2017 rewrite this as a query
		Cursor resultSet = sqLiteDatabase.rawQuery("Select * from Deals", null);
		ArrayList<Account> accs = getAccListFromDB();
		if (resultSet.moveToFirst()) {
			do {
				if (!(accs.contains(new Account(resultSet.getString(1), false)) || accs.contains(new Account(resultSet.getString(0), false))))
					sqLiteDatabase.execSQL("DELETE FROM Deals WHERE id = " + resultSet.getString(5) + ";");
			} while (resultSet.moveToNext());
		}
		resultSet.close();
	}

	ArrayList<DealDTO> getDealsByName(String asRole, String name) { //// TODO: 30.06.2017 for future ststas
		Cursor resultSet = mDatabase.rawQuery("SELECT seller, buyer, date, SUM(sum) FROM Deals  WHERE " + asRole + " ='" + name + "' GROUP BY date;", null);
		ArrayList<DealDTO> myDeals = new ArrayList<>();
		if (resultSet.moveToFirst()) {
			do {
				myDeals.add(new DealDTO(resultSet.getString(0), resultSet.getString(1),
						resultSet.getLong(2), Double.valueOf(resultSet.getString(3)), resultSet.getInt(5)));
			} while (resultSet.moveToNext());
		}
		resultSet.close();
		return myDeals;
	}

	ArrayList<DealDTO> getDealsByName(String name) {
		Cursor resultSet = mDatabase.rawQuery(GET_DEAL_FOR_ACC.replace("[name]", name), null);
		ArrayList<DealDTO> myDeals = new ArrayList<>();
		if (resultSet.moveToFirst()) {
			do {
				myDeals.add(new DealDTO(resultSet.getString(0), resultSet.getString(1),
						resultSet.getLong(2), Double.valueOf(resultSet.getString(3)), resultSet.getString(4), resultSet.getInt(5)));
			} while (resultSet.moveToNext());
		}
		resultSet.close();
		return myDeals;
	}

	public boolean checkExistence(String name) {
		return mDatabase.rawQuery(SELECT_ACCOUNT_WITH_NAME.replace("[name]", name), null).moveToFirst();
	}

	//@formatter:off

	private final String RENAME_TO_TMP = "" +
			"ALTER TABLE [table_name] " +
			" RENAME TO TMP";

	private final String TRANPHER_DATA = "" +
			"INSERT INTO [table_name] (seller, buyer, note, sum)" +
			" SELECT seller, buyer, note, sum FROM TMP ";

	private final String DROP_TMP = "" +
			"DROP TABLE TMP";

	private final String SELECT_ACCOUNT_WITH_NAME = "" +
			"SELECT 1 " +
				"FROM " + TABLE_ACCOUNTS_NAME +
			" WHERE AccName = '[name]'";

	private final String GET_DEAL_FOR_ACC = "" +
			"SELECT " +
				"seller, " +
				"buyer, " +
				"date, " +
				"sum," +
				"note," +
				"id " +
			"FROM Deals " +
			"WHERE seller ='[name]' OR buyer ='[name]' " +
			"ORDER BY date ";

    private final String ADD_ACCOUNT_PATTERN = "" +
            "INSERT INTO " +
                "Accounts (AccName, deposit, description, isOuter) " +
            "VALUES(" +
                "'[NAME]',[DEPOSIT],'[DESCR]','[IS_OUTER]'" +
            ")";

    private final String ADD_DEAL_PATTERN = "" +
            "INSERT INTO " +
                "Deals (seller, buyer, note, sum, date) " +
            "VALUES(" +
                "'[SELLER]','[BUYER]','[NOTE]',[SUM],[DATE]" + //// TODO: 13.05.2017 make date as double not string
            ")";

    private final String TABLE_ACCOUNTS_CREATE = "" +
            "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNTS_NAME + "(" +
                "AccName VARCHAR, " +
                "deposit DOUBLE, " +
                "description VARCHAR," +
                "isOuter BOOLEAN, " +
				"AccountId INTEGER PRIMARY KEY AUTOINCREMENT" +
            ");";

    private final String TABLE_DEALS_CREATE = "" +
            "CREATE TABLE IF NOT EXISTS " + TABLE_DEALS_NAME + "(" +
                " seller VARCHAR, " +//// TODO: 30.06.2017 id of accs
                " buyer VARCHAR, " +
                " note VARCHAR, " +
                " sum DOUBLE, " +
                " date LONG," +
                " id INTEGER PRIMARY KEY AUTOINCREMENT" +
            ");";

    private final String TABLE_ACCOUNTS_DROP = "DROP TABLE IF EXISTS " + TABLE_ACCOUNTS_NAME;

    private final String TABLE_DEALS_DROP = "DROP TABLE IF EXISTS " + TABLE_DEALS_NAME;

    public final String UPDATE_ACCOUNT = "" +
            "UPDATE Accounts " +
            "SET " +
                "deposit = [DEPOSIT], " +
                "description = '[DESCR]', " +
                "isOuter = '[IS_OUTER]' " +
            "WHERE AccName = '[ACC_NAME]'";

    //@formatter:on
}
