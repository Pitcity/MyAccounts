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

    final static int DB_VER = 14;
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
            " date VARCHAR," +
            " id INTEGER PRIMARY KEY AUTOINCREMENT);";
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
        try {
            if (!Boolean.valueOf(resultSet.getString(3)))
            myAccounts.add(new Account(resultSet.getString(0), Double.valueOf(resultSet.getString(1)), resultSet.getString(2), Boolean.valueOf(resultSet.getString(3))));
            while (!resultSet.isLast()) {
                resultSet.moveToNext();
                if (!Boolean.valueOf(resultSet.getString(3)))
                myAccounts.add(new Account(resultSet.getString(0), Double.valueOf(resultSet.getString(1)), resultSet.getString(2), Boolean.valueOf(resultSet.getString(3))));
            }
        } catch (Exception e) {}
        return myAccounts;
    }

    public ArrayList<Deal> getDealListFromDB() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor resultSet = sqLiteDatabase.rawQuery("Select * from Deals", null);
        ArrayList<Deal> myDeals = new ArrayList<>();
        resultSet.moveToFirst();
        try {
            myDeals.add(Deal.createDeal(new Account(resultSet.getString(1), true),
                new Account(resultSet.getString(0), true), resultSet.getString(2),
                Double.valueOf(resultSet.getString(3)), resultSet.getString(4)));
            while (!resultSet.isLast()) {
                resultSet.moveToNext();
                myDeals.add(Deal.createDeal(new Account(resultSet.getString(1), true),
                    new Account(resultSet.getString(0), true), resultSet.getString(2),
                    Double.valueOf(resultSet.getString(3)), resultSet.getString(4)));
            }
        } catch (Exception e) {}
        return myDeals;
    }

    public void fillDb(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("INSERT INTO Accounts VALUES('FirstAcc',2000,'lalala','false');");
        sqLiteDatabase.execSQL("INSERT INTO Accounts VALUES('SecondAcc',7000,'lalala','false');");
        sqLiteDatabase.execSQL("INSERT INTO Accounts VALUES('ThirdAcc',5000,'lalala','false');");
        sqLiteDatabase.execSQL("INSERT INTO Deals (seller, buyer, note, sum, date) VALUES('FirstAcc','SecondAcc','commentFor first deal',1000,'19-вер.-2016');");
    }

    public void deleteAccFromBD(String nameOfAcc) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM Accounts WHERE AccName='" +nameOfAcc +"'");
        updateDeals(sqLiteDatabase);
    }

    public void updateAcc(Account acc) {
        this.getReadableDatabase().execSQL("UPDATE Accounts " +
                "SET deposit="+acc.getDeposit()+", description='"+acc.getDescription()+"', isOuter = '" + Boolean.valueOf(acc.isOuter) +
                "' WHERE AccName = '"+ acc.getName() +"';");
    }

    private void updateDeals(SQLiteDatabase sqLiteDatabase) {
        Cursor resultSet = sqLiteDatabase.rawQuery("Select * from Deals", null);
        ArrayList<Account> accs = getAccListFromDB();
        resultSet.moveToFirst();
        try {
            if (!(accs.contains(new Account(resultSet.getString(1),false))||accs.contains(new Account(resultSet.getString(0),false))))
                sqLiteDatabase.execSQL("DELETE FROM Deals WHERE id = " + resultSet.getString(5)+";");
            while (!resultSet.isLast()) {
                resultSet.moveToNext();
                if (!(accs.contains(new Account(resultSet.getString(1),false))||accs.contains(new Account(resultSet.getString(0),false))))
                    sqLiteDatabase.execSQL("DELETE FROM Deals WHERE id=" + resultSet.getString(5));
            }
        } catch (Exception e) {}
    }

    public void addDealToDB(Deal deal) {
        this.getReadableDatabase().execSQL("INSERT INTO Deals (seller, buyer, note, sum, date) VALUES('"+
                deal.getSeller().getName() + "','"
                + deal.getBuyer().getName() + "','"
                + deal.getNote() + "',"
                + Double.valueOf(deal.getSum()) + ",'"
                + deal.getDate()+"');");
    }
}
