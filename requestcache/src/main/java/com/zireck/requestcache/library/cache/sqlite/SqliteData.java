package com.zireck.requestcache.library.cache.sqlite;

import android.provider.BaseColumns;

class SqliteData {

  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "RequestQueueCache.db";

  public static class TableData implements BaseColumns {
    public static final String TABLE_NAME = "queue";
    public static final String COLUMN_NAME_REQUEST = "request";
  }

  public static class Queries {
    public static final String CREATE_QUEUE_TABLE = "CREATE TABLE " + TableData.TABLE_NAME + " (" +
        TableData._ID + " INTEGER PRIMARY KEY," +
        TableData.COLUMN_NAME_REQUEST + " TEXT)";
    public static final String SELECT_REQUESTS = "SELECT * FROM " + TableData.TABLE_NAME;
    public static final String SELECT_COUNT = "SELECT COUNT(*) FROM " + TableData.TABLE_NAME;
    public static final String DELETE_ROWS = "DELETE FROM " + TableData.TABLE_NAME;
  }
}
