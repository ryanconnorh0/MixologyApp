package com.example.ryan.mixologyapp;

import android.database.sqlite.SQLiteDatabase;

import org.jsoup.*;
import org.jsoup.nodes.Document;

public class WebScrape {
    public static final String URL_PREFIX = "http://www.cocktaildb.com/recipe_detail?id=";

    public static final int RECIPE_MIN_ID = 1;
    public static final int RECIPE_MAX_ID = 4758;

    public static void main(String[] args) {
        try {
            for (int id = RECIPE_MIN_ID; id <= RECIPE_MAX_ID; id++){
                String url = URL_PREFIX + Integer.toString(id);
                Document doc = Jsoup.connect(url).get();

                Recipe recipe = new Recipe();
                //recipe.parseFromHTML(doc);

                System.out.println(recipe);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void populate(MixDBHelper db){
        try {
            int[] ids = {1, 1, 1}; //ingredient, serving size, direction
            for (int drink_id = RECIPE_MIN_ID; drink_id <= RECIPE_MAX_ID; drink_id++){
                String url = URL_PREFIX + Integer.toString(drink_id);
                Document doc = Jsoup.connect(url).get();

                Recipe recipe = new Recipe();
                ids = recipe.parseFromHTML(doc, db, ids, drink_id);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
