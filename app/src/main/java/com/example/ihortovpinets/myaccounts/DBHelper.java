package com.example.ihortovpinets.myaccounts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ihortovpinets.myaccounts.DTO.DealDTO;
import com.example.ihortovpinets.myaccounts.Entity.Account;
import com.example.ihortovpinets.myaccounts.Entity.Deal;
import com.example.ihortovpinets.myaccounts.Service.SyncService;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    final static int DB_VER = 2;

    private final static String DB_NAME = "MyAccounts.db";
    private final static String TABLE_ACCOUNTS_NAME = "tblAccounts";
    private final static String TABLE_DEALS_NAME = "tblDeals";
    private static final String TABLE_QUANT_TYPES_NAME = "tblQuantityTypes";
    private static final String TABLE_COMP_TYPES_NAME = "tblComponentsTypes";
    private static final String TABLE_COMP_CAT_NAME = "tblComponentsCategories";
    private static final String TABLE_PRODUCTION_NAME = "tblProduction";
    private static final String TABLE_DEALS_COMPONENTS_NAME = "tblDealComponents";

	private SQLiteDatabase mDatabase = null;
	private Context mContext;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
		mContext = context;
		mDatabase = this.getReadableDatabase();
	}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_ACCOUNTS_CREATE);
        sqLiteDatabase.execSQL(TABLE_DEALS_CREATE);
        sqLiteDatabase.execSQL(TABLE_COMPONENT_CATEGORIES_CREATE);
        sqLiteDatabase.execSQL(TABLE_COMPONENT_TYPES_CREATE);
        sqLiteDatabase.execSQL(TABLE_DEALS_COMPONENT_CREATE);
        sqLiteDatabase.execSQL(TABLE_PRODUCTION_CREATE);
        sqLiteDatabase.execSQL(TABLE_QUANTITY_TYPES_CREATE);
        fillDb(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(TABLE_DROP.replace("[table_name]", TABLE_ACCOUNTS_NAME));
        sqLiteDatabase.execSQL(TABLE_DROP.replace("[table_name]", TABLE_DEALS_NAME));
        sqLiteDatabase.execSQL(TABLE_DROP.replace("[table_name]", TABLE_COMP_CAT_NAME));
        sqLiteDatabase.execSQL(TABLE_DROP.replace("[table_name]", TABLE_COMP_TYPES_NAME));
        sqLiteDatabase.execSQL(TABLE_DROP.replace("[table_name]", TABLE_DEALS_COMPONENTS_NAME));
        sqLiteDatabase.execSQL(TABLE_DROP.replace("[table_name]", TABLE_PRODUCTION_NAME));
        sqLiteDatabase.execSQL(TABLE_DROP.replace("[table_name]", TABLE_QUANT_TYPES_NAME));
        onCreate(sqLiteDatabase);
    }


    private void fillDb(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_ACCOUNTS_NAME + " VALUES('FirstAcc',2000,'lalala','false');");
//        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_ACCOUNTS_NAME + " VALUES('SecondAcc',7000,'lalala','false');");
//        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_ACCOUNTS_NAME + " VALUES('ThirdAcc',5000,'lalala','false');");
//        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_DEALS_NAME + " (seller, buyer, note, sum, date) VALUES('FirstAcc','SecondAcc','commentFor first deal',1000,'19-вер.-2016');");
    }

    void addAccountToDB(Account acc) { //valid
        mDatabase.execSQL(ADD_ACCOUNT_PATTERN
                .replace("[accountId]", acc.getAccountId())
                .replace("[NAME]", acc.getName())
                .replace("[DEPOSIT]", Double.toString(acc.getDeposit()))
                .replace("[DESCR]", acc.getDescription())
                .replace("[IS_OUTER]", Boolean.toString(acc.isOuter()))
        );
    }

    public void updateAccountsWithServer(ArrayList<Account>accounts) {
	    for (Account acc: accounts) {
	        if(isAccountWithNameExists(acc.getName()) || isAccountWithIdExists(acc.getAccountId())) {
                System.out.println("ressult : accIsExists" + acc.toString());
            } else {
	            addAccountToDB(acc);
                System.out.println("ressult : accAdded" + acc.toString());
	            new SyncService(mContext).requestDeals(acc.getAccountId());
            }
        }
    }

	void addDealToDB(Deal deal) {
		mDatabase.execSQL(ADD_DEAL_PATTERN.replace("[SELLER]", deal.getSeller().getName())
				.replace("[BUYER]", deal.getBuyer().getName())
				.replace("[NOTE]", deal.getNote())
				.replace("[SUM]", Double.toString(deal.getSum()))
				.replace("[DATE]", Long.toString(deal.getDateLong()))
				.replace("[id]", deal.getDealId())
		);
	}

    public void addDealToDB(DealDTO deal) {
        mDatabase.execSQL(ADD_DEAL_PATTERN.replace("[SELLER]", deal.getSeller())
                .replace("[BUYER]", deal.getBuyer())
                .replace("[NOTE]", deal.getNote())
                .replace("[SUM]", Double.toString(deal.getSum()))
                .replace("[DATE]", deal.getDateMilis().toString())
				.replace("[id]", deal.getId())
        );
    }


    public ArrayList<Account> getAccListFromDB() {
	    return getAccListFromDB(true);
    }

    ArrayList<Account> getAccListFromDB(boolean selectActive) {
        Cursor resultSet = mDatabase.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS_NAME + (selectActive ? " where syncFlag != 9 and isOuter == 'false' " : ""), null);//// TODO: 13.05.2017 replace * with fields
        ArrayList<Account> myAccounts = new ArrayList<>();
        if (resultSet.moveToFirst()) {
            do {
                myAccounts.add(new Account(resultSet.getString(0), resultSet.getString(1),
                        Double.valueOf(resultSet.getString(2)), resultSet.getString(3), Boolean.valueOf(resultSet.getString(4)), resultSet.getInt(5)));
            } while (resultSet.moveToNext());
        }
        resultSet.close();
        return myAccounts;
    }

    ArrayList<Deal> getDealListFromDB() {
        Cursor resultSet = mDatabase.rawQuery("SELECT * FROM " + TABLE_DEALS_NAME, null);//// TODO: 13.05.2017 replace * with fields
        ArrayList<Deal> myDeals = new ArrayList<>();
        if (resultSet.moveToFirst()) {
            do {
                myDeals.add(Deal.createDeal(new Account(resultSet.getString(1), true),
                        new Account(resultSet.getString(0), true), resultSet.getString(2),
                        Double.valueOf(resultSet.getString(3)), resultSet.getLong(4),resultSet.getInt(5)));//// TODO: 13.05.2017 do smth with all that numbers -_-
            }
            while (resultSet.moveToNext());
        }
        resultSet.close();
        return myDeals;
    }

    @Deprecated
    void deleteAccFromBD(String nameOfAcc) {
        mDatabase.execSQL("UPDATE " + TABLE_ACCOUNTS_NAME + " SET syncFlag = 9 WHERE AccName='" + nameOfAcc + "'");
        updateDeals(mDatabase);
    }

    void deleteAccById(String accId) {
        mDatabase.execSQL("UPDATE " + TABLE_ACCOUNTS_NAME + " SET syncFlag = 9 WHERE accountId='" + accId + "'");
        updateDeals(mDatabase);
    }

	public Account getAccIdByName(String name) {
		Cursor resultSet = mDatabase.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS_NAME + " WHERE AccName like '" + name + "'", null);//// TODO: 13.05.2017 replace * with fields
		Account acc = null;
		if (resultSet.moveToFirst()) {
				acc = new Account(resultSet.getString(0), resultSet.getString(1),
						Double.valueOf(resultSet.getString(2)), resultSet.getString(3), Boolean.valueOf(resultSet.getString(4)), resultSet.getInt(5));
		}
		resultSet.close();
		return acc;
	}

	void updateAcc(Account acc) {
		mDatabase.execSQL(UPDATE_ACCOUNT
				.replace("[accountId]", acc.getAccountId())
                .replace("[DEPOSIT]", Double.toString(acc.getDeposit()))
				.replace("[DESCR]", acc.getDescription())
				.replace("[IS_OUTER]", Boolean.toString(acc.isOuter()))
				.replace("[ACC_NAME]", acc.getName())
		);
	}

	private void updateDeals(SQLiteDatabase sqLiteDatabase) { //// TODO: 13.05.2017 rewrite this as a query
		Cursor resultSet = sqLiteDatabase.rawQuery("Select * from " + TABLE_DEALS_NAME, null);
		ArrayList<Account> accs = getAccListFromDB();
		if (resultSet.moveToFirst()) {
			do {
				if (!(accs.contains(new Account(resultSet.getString(1), false)) || accs.contains(new Account(resultSet.getString(0), false))))
					sqLiteDatabase.execSQL("update " + TABLE_DEALS_NAME + " set syncFlag = 9 WHERE id = '" + resultSet.getString(5) + "';");
			} while (resultSet.moveToNext());
		}
		resultSet.close();
	}

	public boolean isDealWithIdExists(String id) {
		return mDatabase.rawQuery(SELECT_DEAL_WITH_ID.replace("[id]", id), null).moveToFirst();
	}

	ArrayList<DealDTO> getDealsByName(String name) {
		Cursor resultSet = mDatabase.rawQuery(GET_DEAL_FOR_ACC.replace("[name]", name), null);
		ArrayList<DealDTO> myDeals = new ArrayList<>();
		if (resultSet.moveToFirst()) {
			do {
				myDeals.add(new DealDTO(resultSet.getString(0), resultSet.getString(1),
						resultSet.getLong(2), Double.valueOf(resultSet.getString(3)), resultSet.getString(4), resultSet.getString(5)));
			} while (resultSet.moveToNext());
		}
		resultSet.close();
		return myDeals;
	}

	public void clearDb() {
		mDatabase.beginTransaction();
		mDatabase.execSQL(TABLE_CLEAR.replace("[table_name]",TABLE_DEALS_NAME));
		mDatabase.execSQL(TABLE_CLEAR.replace("[table_name]",TABLE_ACCOUNTS_NAME));
		mDatabase.setTransactionSuccessful();
		mDatabase.endTransaction();
	}

    public boolean isAccountWithNameExists(String name) {
        return mDatabase.rawQuery(SELECT_ACCOUNT_WITH_NAME.replace("[name]", name), null).moveToFirst();
    }

    public boolean isAccountWithIdExists(String id) {
        return mDatabase.rawQuery(SELECT_ACCOUNT_WITH_ID.replace("[id]", id), null).moveToFirst();
    }
	//@formatter:off

    private final String TABLE_DROP = "DROP TABLE IF EXISTS [table_name]";

    private final String TABLE_CLEAR = "DELETE FROM [table_name]";

	private final String RENAME_TO_TMP = "" +
			"ALTER TABLE [table_name] " +
			" RENAME TO TMP";

	private final String TRANPHER_DATA = "" +
			"INSERT INTO [table_name] (seller, buyer, note, sum)" +
			" SELECT seller, buyer, note, sum FROM TMP ";

	private final String DROP_TMP = "" +
			"DROP TABLE TMP";

	private static final String SELECT_ACCOUNT_WITH_NAME = "" +
			"SELECT 1 " +
				"FROM " + TABLE_ACCOUNTS_NAME +
			" WHERE AccName = '[name]'";

    private static final String SELECT_ACCOUNT_WITH_ID = "" +
            "SELECT 1 " +
                "FROM " + TABLE_ACCOUNTS_NAME +
            " WHERE accountId = '[id]'";

     private static final String SELECT_DEAL_WITH_ID = "" +
            "SELECT 1 " +
                "FROM " + TABLE_DEALS_NAME +
            " WHERE id = '[id]'";

	private static final String GET_DEAL_FOR_ACC = "" +
			"SELECT " +
				"seller, " +
				"buyer, " +
				"date, " +
				"sum," +
				"note," +
				"id," +
                "syncFlag " +
			"FROM " + TABLE_DEALS_NAME + " " +
			"WHERE seller ='[name]' OR buyer ='[name]' " +
			"ORDER BY date ";

    private static final String ADD_ACCOUNT_PATTERN = "" +
            "INSERT INTO " + TABLE_ACCOUNTS_NAME +
            " (accountId, AccName, deposit, description, isOuter, syncFlag) " +
            "VALUES(" +
            "'[accountId]','[NAME]',[DEPOSIT],'[DESCR]','[IS_OUTER]', 2" +
            ")";

    private static final String ADD_DEAL_PATTERN = "" +
            "INSERT INTO " + TABLE_DEALS_NAME +
            " (id, seller, buyer, note, sum, date, syncFlag) " +
            "VALUES(" +
            "'[id]','[SELLER]','[BUYER]','[NOTE]',[SUM],'[DATE]', 2" + //// TODO: 13.05.2017 make date as double not string
            ")";

    private static final String TABLE_ACCOUNTS_CREATE = "" +
            "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNTS_NAME + "(" +
            "accountId VARCHAR PRIMARY KEY, " +
            "AccName VARCHAR, " +
            "deposit DOUBLE, " +
            "description VARCHAR," +
            "isOuter BOOLEAN, " +
            "syncFlag INT " +
            ");";

    private static final String TABLE_DEALS_CREATE = "" +
            "CREATE TABLE IF NOT EXISTS " + TABLE_DEALS_NAME + "(" +
            " seller VARCHAR, " +
            " buyer VARCHAR, " +
            " note VARCHAR, " +
            " sum DOUBLE, " +
            " date LONG," +
            "id VARCHAR PRIMARY KEY, " +
            "syncFlag INT " +
            ");";

    public static final String UPDATE_ACCOUNT = "" +
            "UPDATE " + TABLE_ACCOUNTS_NAME +
            " SET " +
            "deposit = [DEPOSIT], " +
            "description = '[DESCR]', " +
            "isOuter = '[IS_OUTER]', " +
            "AccName = '[ACC_NAME]', " +
            "syncFlag = 1 " +
            "WHERE accountId = '[accountId]'";

    private static final String TABLE_COMPONENT_TYPES_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_COMP_TYPES_NAME + " ( TypeId VARCHAR PRIMARY KEY, CategoryId VARCHAR, TypeTitle VARCHAR)";

    private static final String TABLE_COMPONENT_CATEGORIES_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_COMP_CAT_NAME + "( CategoryId VARCHAR PRIMARY KEY, CaegoryTitle VARCHAR)";

    private static final String TABLE_PRODUCTION_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTION_NAME + "(ProductionId VARCHAR PRIMARY KEY, CategoryId VARCHAR, TypeId VARCHAR, ProductionTitle VARCHAR)";

    private static final String TABLE_DEALS_COMPONENT_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_DEALS_COMPONENTS_NAME + "( DealId VARCHAR, ProductId VARCHAR, Price DOUBLE, Quantity DOUBLE, QuantityType INTEGER)";

    private static final String TABLE_QUANTITY_TYPES_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_QUANT_TYPES_NAME + "( QuantityTypeId INTEGER PRIMARY KEY, QuantityTypeTitle VARCHAR)";
    //@formatter:on
}
