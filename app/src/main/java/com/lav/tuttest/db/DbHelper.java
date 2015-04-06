package com.lav.tuttest.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.lav.tuttest.R;

import java.lang.reflect.Field;


public class DbHelper extends SQLiteOpenHelper implements BaseColumns {
	
	public final static int VERSION = 1;
	
	private static DbHelper instance = null;
	private static String mDbName;
	
	
	private DbHelper(Context context) {
		super(context, context.getString(R.string.db_name), null, VERSION);
		mDbName = context.getString(R.string.db_name);
    } 
	
	
	public static DbHelper getInstance(Context context) {
	    if (instance == null) {
	    	instance = new DbHelper(context.getApplicationContext());
	    }
	    return instance;
	}
	
	
	public final static class Timelist {
		
		public final static String TABLE_NAME = "timelist";
		
		public final static class DC {
			public final static String ID      = "_id";
			public final static String TIME    = "time";
	    }
		
		public final static class DT {
			public final static String ID_TYPE      = "INTEGER PRIMARY KEY AUTOINCREMENT";
			public final static String TIME_TYPE    = "TEXT";
		}
		
		public final static String KEY = null;
	}
	
	
	@Override
    public void onCreate(SQLiteDatabase db) {
    	if(!checkDataBase()) {
   			createTables(db);
    	}
    }
 
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		deleteTables(db);
		createTables(db);
	}
	
    
	public static void deleteTables(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + Timelist.TABLE_NAME);
		}
		catch (SQLException e)	{
			e.printStackTrace();
		}
    }
    
    
    private static boolean isTableExists(SQLiteDatabase db, String tableName) {
    	
	    if (tableName == null || db == null || !db.isOpen()) {
	        return false;
	    }
	    
	    Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "'", null);
	    
	    if (cursor != null) {
	    	if (cursor.moveToFirst()) {
	    		int count = cursor.getInt(0);
	    		cursor.close();
	    		return count > 0;
	    	}
	    	else {
	    		cursor.close();
	    		return false;
	    	}
	    }
	    else {
	    	return false;
	    }
	}
	

	private static void createTable(SQLiteDatabase db, String tableName, Field[] flds, Field[] tps, String foreign, String key) {

		if (!isTableExists(db, tableName)) {

			StringBuilder sb = new StringBuilder("CREATE TABLE " + tableName + " (");

			try {
				for (int i = 0; i < flds.length; i++) {


					sb.append(flds[i].get(DbHelper.class).toString()).append(" ").append(tps[i].get(DbHelper.class).toString());

					if (i != flds.length - 1) {
						sb.append(", ");
					}
				}
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			if (foreign == null) {
				if (key == null) {
					sb.append(")");
				}
				else {
					sb.append(", ").append(key).append(")");
				}
			}
			else {
				if (key == null) {
					sb.append(", ").append(foreign).append(")");
				}
				else {
					sb.append(", ").append(foreign).append(", ").append(key).append(")");
				}
			}

			try {
				db.execSQL(sb.toString());
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	private static void createTables(SQLiteDatabase db) {
		createTable(db, Timelist.TABLE_NAME, Timelist.DC.class.getFields(), Timelist.DT.class.getFields(), null, Timelist.KEY);
	}
	

	private static boolean checkDataBase() { 
		
        SQLiteDatabase checkDB = null;
 
        try {
            checkDB = SQLiteDatabase.openDatabase(mDbName, null, SQLiteDatabase.OPEN_READONLY);
        }
        finally {
		}
 
        if(checkDB != null) {
        	checkDB.close();
        }
        
        return (checkDB != null);
    }
}
