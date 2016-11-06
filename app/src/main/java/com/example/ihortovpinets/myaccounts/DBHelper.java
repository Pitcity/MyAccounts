package com.example.ihortovpinets.myaccounts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by IhorTovpinets on 05.11.2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    final static int DB_VER = 3;
    final static String DB_NAME = "MyAccounts1.db";
    final String TABLE_NAME1 = "Accounts";
    final String TABLE_NAME2 = "Deals";
    final String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME1+ //Accs
            "(AccName VARCHAR PRIMARY KEY, " +
            "    deposit DOUBLE, " +
            "    description VARCHAR," +
            "    isOuter BOOLEAN);";
    final String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME2+ //Deals buyer, seller, deal1.getNote(), deal1.getSum(), deal1.getDate()
            "("+
            " seller VARCHAR, "+
            " buyer VARCHAR, " +
            " note VARCHAR, " +
            " sum DOUBLE, " +
            " date VARCHAR)";
    final String DROP_TABLE1 = "DROP TABLE IF EXISTS "+TABLE_NAME1;
    final String DROP_TABLE2 = "DROP TABLE IF EXISTS "+TABLE_NAME2;
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE1);
        sqLiteDatabase.execSQL(CREATE_TABLE2);
        fillDb(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE1);
        sqLiteDatabase.execSQL(DROP_TABLE2);

        onCreate(sqLiteDatabase);
    }

    public void addAccountToDB(Account acc) {
        this.getReadableDatabase().execSQL("INSERT INTO Accounts VALUES('"+
                            acc.getName() + "',"
                + acc.getDeposit() + ",'"
                + acc.getDescription() + "','"
                + acc.isOuter+"');");
    }

    public ArrayList<Account> getAccListFromDB() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor resultSet =  sqLiteDatabase.rawQuery("Select * from Accounts",null);
        resultSet.moveToFirst();
        ArrayList<Account> myAccounts = new ArrayList<>();
        myAccounts.add(new Account(resultSet.getString(0), Double.valueOf(resultSet.getString(1)), resultSet.getString(2), Boolean.getBoolean(resultSet.getString(3))));
        do {
            resultSet.moveToNext();
            myAccounts.add(new Account(resultSet.getString(0), Double.valueOf(resultSet.getString(1)), resultSet.getString(2), Boolean.getBoolean(resultSet.getString(3))));
        } while (!resultSet.isLast());

        return myAccounts;
    }

    public ArrayList<Deal> getDealListFromDB() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor resultSet =  sqLiteDatabase.rawQuery("Select * from Deals",null);
        resultSet.moveToFirst();
        ArrayList<Deal> myDeals = new ArrayList<>();
        myDeals.add(Deal.createDeal(new Account(resultSet.getString(0),true), new Account(resultSet.getString(1),true), resultSet.getString(2), Double.valueOf(resultSet.getString(3)),resultSet.getString(4)));
        do {
            resultSet.moveToNext();
            myDeals.add(Deal.createDeal(new Account(resultSet.getString(0),true), new Account(resultSet.getString(1),true), resultSet.getString(2), Double.valueOf(resultSet.getString(3)),resultSet.getString(4)));
        } while (!resultSet.isLast());

        return myDeals;
    }

    public void fillDb(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("INSERT INTO Accounts VALUES('The first acc',2000,'lalala','false');");
        sqLiteDatabase.execSQL("INSERT INTO Accounts VALUES('The second acc',7000,'lalala','false');");
        sqLiteDatabase.execSQL("INSERT INTO Accounts VALUES('The third acc',5000,'lalala','true');");
    }

    public void updateAcc(Account acc) {
        this.getReadableDatabase().execSQL("UPDATE Accounts " +
                "SET deposit="+acc.getDeposit()+", description='"+acc.getDescription()+"', isOuter = '" + acc.isOuter +
                "' WHERE AccName = '"+ acc.getName() +"';");
    }

    public void addDealToDB(Deal deal) {
        this.getReadableDatabase().execSQL("INSERT INTO Deals VALUES('"+
                deal.getSeller().getName() + "','"
                + deal.getBuyer().getName() + "','"
                + deal.getNote() + "',"
                + Double.valueOf(deal.getSum()) + ",'"
                + deal.getDate()+"');");
    }
}
