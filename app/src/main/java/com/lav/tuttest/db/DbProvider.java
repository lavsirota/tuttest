package com.lav.tuttest.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class DbProvider extends ContentProvider {
	
	public static String PROVIDER_AUTHORITIES = "com.lav.tuttest.db";

    private static final int TIMELIST = 100;
	private static final int TIMELIST_ID = 101;

	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
    	uriMatcher.addURI(PROVIDER_AUTHORITIES, "timelist", TIMELIST);
		uriMatcher.addURI(PROVIDER_AUTHORITIES, "timelist/#", TIMELIST_ID);
    }
    
	private SQLiteDatabase mDb;
	
	
	@Override
	public boolean onCreate() {
		mDb = DbHelper.getInstance(getContext().getApplicationContext()).getWritableDatabase();
		return (mDb != null);
	}


	@Override
	public final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
		
		SQLiteQueryBuilder queryBuider = new SQLiteQueryBuilder();
		String groupBy = null;
		String having = null;
		Cursor cursor;

		String parameter1;
		
		switch (uriMatcher.match(uri)) {
		
			case TIMELIST:
				queryBuider.setTables(DbHelper.Timelist.TABLE_NAME);
				break;

			case TIMELIST_ID:
				parameter1 = uri.getPathSegments().get(1);
				queryBuider.setTables(DbHelper.Timelist.TABLE_NAME);
				queryBuider.appendWhere(DbHelper.Timelist.DC.ID + "=" + parameter1);
				break;

			default:
				return null;
		}
		
		cursor = queryBuider.query(mDb, projection, selection, selectionArgs, groupBy, having, sort);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}
			
	
	@Override
	public String getType(Uri uri) {
		return null;
	}

	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		String tableName;
		
		switch (uriMatcher.match(uri)) {
		
		case TIMELIST:
			tableName = DbHelper.Timelist.TABLE_NAME;
			break;
			
		default:
			return 0;
		}
		
		int retVal = mDb.delete(tableName, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return retVal;
	}

	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		if (uri == null) {
			return null;
		}
		
		String tableName;
		
		switch (uriMatcher.match(uri)) {
		
		case TIMELIST:
			tableName = DbHelper.Timelist.TABLE_NAME;
			break;
			
		default:
			return null;
		}
		
		long rowId = 0;
		
		try {
			rowId = mDb.insert(tableName, null, values);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (rowId > 0) {
			Uri newUri = ContentUris.withAppendedId(uri, rowId);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}
		else {
			throw new SQLException("Failed to insert row into " + uri.getQuery());
		}
	}

	
	@Override
	public int update(Uri uri, ContentValues inValues, String selection, String[] selectionArgs) {
		
		String tableName;
		
		switch (uriMatcher.match(uri)) {
		
		case TIMELIST:
			tableName = DbHelper.Timelist.TABLE_NAME;
			break;
			
		default:
			return 0;
		}
		
		ContentValues values = new ContentValues(inValues);
		int retVal = mDb.update(tableName, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return retVal;
	}
}
