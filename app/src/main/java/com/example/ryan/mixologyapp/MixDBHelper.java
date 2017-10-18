package com.example.ryan.mixologyapp;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

/**
 * Created by Ryan on 10/4/2017.
 */

public class MixDBHelper extends SQLiteOpenHelper{

    /*Database defined as constants*/
    public static final String DATABASE_NAME = "Mixology.db";
    private static final int DATABASE_VERSION = 1;

    public static final String RECIPE_TABLE_NAME = "recipe";
    public static final String RECIPE_COLUMN_ID = "_id";
    public static final String RECIPE_COLUMN_STEP_ORDER = "steporder";
    public static final String RECIPE_COLUMN_DRINK_FK = "dfk";
    public static final String RECIPE_COLUMN_INGREDIENT_FK = "ifk";
    public static final String RECIPE_COLUMN_SERVING_FK = "sfk";
    public static final String RECIPE_COLUMN_DIRECTION_FK = "dirfk";

    public static final String DRINK_TABLE_NAME = "drink";
    public static final String DRINK_COLUMN_ID = "_id";
    public static final String DRINK_COLUMN_NAME = "name";

    public static final String DIRECTION_TABLE_NAME = "direction";
    public static final String DIRECTION_COLUMN_ID = "_id";
    public static final String DIRECTION_COLUMN_STRING = "string";

    public static final String INGREDIENT_TABLE_NAME = "ingredient";
    public static final String INGREDIENT_COLUMN_ID = "_id";
    public static final String INGREDIENT_COLUMN_NAME = "name";


    public static final String SERVING_TABLE_NAME = "serving";
    public static final String SERVING_COLUMN_ID = "_id";
    public static final String SERVING_COLUMN_MEASUREMENT = "measurement";

    //NEED TO MAKE MYBAR AND FAVORITES TABLES


    /*Constructor*/
    public MixDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RECIPE_TABLE_NAME + "(" +
                RECIPE_COLUMN_ID + " INT PRIMARY KEY, " +
                RECIPE_COLUMN_STEP_ORDER + " INT, " +
                RECIPE_COLUMN_DRINK_FK + " INT, " +
                RECIPE_COLUMN_INGREDIENT_FK + " INT, " +
                RECIPE_COLUMN_SERVING_FK + " INT, " +
                RECIPE_COLUMN_DIRECTION_FK + " INT, " +
                "FOREIGN KEY (" + RECIPE_COLUMN_DRINK_FK + ") REFERENCES " + DRINK_TABLE_NAME + "(" + DRINK_COLUMN_ID + ")" +
                "FOREIGN KEY (" + RECIPE_COLUMN_INGREDIENT_FK + ") REFERENCES " + INGREDIENT_TABLE_NAME + "(" + INGREDIENT_COLUMN_ID + ")" +
                "FOREIGN KEY (" + RECIPE_COLUMN_SERVING_FK + ") REFERENCES " + SERVING_TABLE_NAME + "(" + SERVING_COLUMN_ID + ")" +
                "FOREIGN KEY (" + RECIPE_COLUMN_DIRECTION_FK + ") REFERENCES " + DIRECTION_TABLE_NAME + "(" + DIRECTION_COLUMN_ID + ") )"
        );

        db.execSQL("CREATE TABLE " + DRINK_TABLE_NAME + "(" +
                DRINK_COLUMN_ID + " INT PRIMARY KEY, " +
                DRINK_COLUMN_NAME + " TEXT)"
        );

        db.execSQL("CREATE TABLE " + DIRECTION_TABLE_NAME + "(" +
                DIRECTION_COLUMN_ID + " INT PRIMARY KEY, " +
                DIRECTION_COLUMN_STRING + " TEXT)"
        );

        db.execSQL("CREATE TABLE " + INGREDIENT_TABLE_NAME + "(" +
                INGREDIENT_COLUMN_ID + " INT PRIMARY KEY, " +
                INGREDIENT_COLUMN_NAME + " TEXT)"
        );

        db.execSQL("CREATE TABLE " + SERVING_TABLE_NAME + "(" +
                SERVING_COLUMN_ID + " INT PRIMARY KEY, " +
                SERVING_COLUMN_MEASUREMENT + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*Will need to do at later*/
    }

    /*Insertion functions*/
    public boolean insertDrink(String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DRINK_COLUMN_NAME, name);
        db.insert(DRINK_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean insertDirection(String instruction) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DIRECTION_COLUMN_STRING, instruction);
        db.insert(DIRECTION_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean insertIngredient(String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INGREDIENT_COLUMN_NAME, name);
        db.insert(INGREDIENT_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean insertServing(String measurement) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SERVING_COLUMN_MEASUREMENT, measurement);
        db.insert(SERVING_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean insertRecipe(int step_order, int drink_id, int ingredient_id, int serving_id, int direction_id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECIPE_COLUMN_STEP_ORDER, step_order);
        contentValues.put(RECIPE_COLUMN_DRINK_FK, drink_id);
        contentValues.put(RECIPE_COLUMN_INGREDIENT_FK, ingredient_id);
        contentValues.put(RECIPE_COLUMN_SERVING_FK, serving_id);
        contentValues.put(RECIPE_COLUMN_DIRECTION_FK, direction_id);
        db.insert(RECIPE_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    /*Get functions*/
    public Cursor getDrink(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + DRINK_TABLE_NAME + " WHERE " +
                DRINK_COLUMN_ID + "=?", new String[] { Integer.toString(id) } );
        return res;
    }
    public Cursor getAllDrinks() { //will need to alphabetize
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + DRINK_TABLE_NAME, null );
        return res;
    }
    public Cursor getRecipe(int drink_id) { //order by steporder
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + RECIPE_TABLE_NAME + " WHERE " +
                RECIPE_COLUMN_DRINK_FK + "=?", new String[] { Integer.toString(drink_id) } );
        return res;
    }

    /*These get functions help eliminate redundancies in the database*/
    public long getDirection(String direction){
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT ROWID FROM direction WHERE string LIKE '" + direction + "'";
        Cursor res = db.rawQuery(Query, null);

        if(res.getCount() <= 0){
            res.close();
            return 0;
        };

        res.moveToFirst();
        long id = res.getLong(res.getColumnIndex("rowid"));
        res.close();
        return id;
    }


    public long getServing(String measurement){
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT ROWID FROM serving WHERE measurement LIKE '" + measurement + "'";
        Cursor res = db.rawQuery(Query, null);

        if(res.getCount() <= 0){
            res.close();
            return 0;
        };

        res.moveToFirst();
        long id = res.getLong(res.getColumnIndex("rowid"));
        res.close();
        return id;
    }

    public long getIngredient(String ingredient){
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT ROWID FROM ingredient WHERE name LIKE '" + ingredient + "'";
        Cursor res = db.rawQuery(Query, null);

        if(res.getCount() <= 0){
            res.close();
            return 0;
        };

        res.moveToFirst();
        long id = res.getLong(res.getColumnIndex("rowid"));
        res.close();
        return id;
    }

    /*May need to add update and delete functions at a later date*/

}
