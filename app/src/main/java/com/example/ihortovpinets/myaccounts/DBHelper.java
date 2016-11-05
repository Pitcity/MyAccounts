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

    final static int DB_VER = 1;
    final static String DB_NAME = "MyAccounts1.db";
    final String TABLE_NAME1 = "Accounts";
    final String TABLE_NAME2 = "Deals";
    final String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME1+
            "(AccName VARCHAR PRIMARY KEY, " +
            "    deposit DOUBLE, " +
            "    description VARCHAR," +
            "    isOuter BOOLEAN);";
    final String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME2+
            "( _id INTEGER PRIMARY KEY , "+
            " todo TEXT)";
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
    public void fillDb(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("INSERT INTO Accounts VALUES('The first acc',2000,'lalala','false');");
        sqLiteDatabase.execSQL("INSERT INTO Accounts VALUES('The second acc',7000,'lalala','false');");
        sqLiteDatabase.execSQL("INSERT INTO Accounts VALUES('The third acc',5000,'lalala','true');");
    }
}
