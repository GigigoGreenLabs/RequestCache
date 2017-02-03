package com.zireck.requestcache.library.cache.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.gson.reflect.TypeToken;
import com.zireck.requestcache.library.cache.RequestQueue;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.util.serializer.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;

import static com.zireck.requestcache.library.cache.sqlite.SqliteData.DATABASE_NAME;
import static com.zireck.requestcache.library.cache.sqlite.SqliteData.DATABASE_VERSION;
import static com.zireck.requestcache.library.cache.sqlite.SqliteData.Queries;
import static com.zireck.requestcache.library.cache.sqlite.SqliteData.TableData;

public class SqliteQueue extends SQLiteOpenHelper implements RequestQueue {

  private final SQLiteDatabase sqLiteDatabase;
  private final JsonSerializer jsonSerializer;
  private long currentId;
  private Cursor cursor;

  public SqliteQueue(Context context, JsonSerializer jsonSerializer) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.sqLiteDatabase = getWritableDatabase();
    this.jsonSerializer = jsonSerializer;
  }

  @Override public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(Queries.CREATE_QUEUE_TABLE);
  }

  @Override public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    // no-op
  }

  @Override public boolean isEmpty() {
    boolean isEmpty = true;
    Cursor cursor = sqLiteDatabase.rawQuery(Queries.SELECT_COUNT, null);
    if (cursor != null && cursor.moveToFirst()) {
      isEmpty = (cursor.getInt(0) == 0);
    }
    cursor.close();

    return isEmpty;
  }

  @Override public void add(RequestModel requestModel) {
    ContentValues contentValues = transform(requestModel);
    insertToDb(contentValues);
  }

  @Override public void add(List<RequestModel> requestModels) {
    for (RequestModel requestModel : requestModels) {
      ContentValues contentValues = transform(requestModel);
      insertToDb(contentValues);
    }
  }

  @Override public boolean hasNext() {
    return !cursor.isAfterLast();
  }

  @Override public RequestModel next() {
    RequestModel requestModel = loadCurrentElement();
    cursor.moveToNext();

    return requestModel;
  }

  @Override public void remove() {
    deleteById(currentId);
  }

  @Override public boolean clear() {
    return sqLiteDatabase.delete(TableData.TABLE_NAME, null, null) > 0;
  }

  @Override public void loadToMemory() {
    cursor = sqLiteDatabase.rawQuery(Queries.SELECT_REQUESTS, null);
    cursor.moveToFirst();
  }

  @Override public void persistToDisk() {
    currentId = -1;
    if (cursor != null) {
      cursor.close();
    }
  }

  private ContentValues transform(RequestModel requestModel) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(TableData.COLUMN_NAME_REQUEST, jsonSerializer.toJson(requestModel));

    return contentValues;
  }

  private long insertToDb(ContentValues contentValues) {
    return sqLiteDatabase.insert(TableData.TABLE_NAME, null, contentValues);
  }

  private RequestModel loadCurrentElement() {
    currentId = cursor.getInt(cursor.getColumnIndex(TableData._ID));
    String requestModelString =
        cursor.getString(cursor.getColumnIndex(TableData.COLUMN_NAME_REQUEST));
    Type requestModelType = new TypeToken<RequestModel>() {
    }.getType();

    return (RequestModel) jsonSerializer.fromJson(requestModelString, requestModelType);
  }

  private void deleteById(long id) {
    String whereClause = TableData._ID + " = ?";
    String[] whereArgs = { Long.toString(id) };
    sqLiteDatabase.delete(TableData.TABLE_NAME, whereClause, whereArgs);
  }
}
