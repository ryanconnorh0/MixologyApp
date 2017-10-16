package com.example.ryan.mixologyapp;

import android.database.sqlite.SQLiteDatabase;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Recipe {
    public String name;
    public ArrayList<RecipeInstruction> instructions; // measures and ingredients in the order appearing in the recipe

    public Recipe(){
        instructions = new ArrayList<>();
    }

    public int[] parseFromHTML(Document doc, MixDBHelper db, int[] ids, int drink_id){
        this.name = doc.select("h2").html(); //Drink name
        db.insertDrink(this.name); //Inserts drink name into database, once per function call

        Elements directionElements = doc.select(".recipeDirection");
        Elements measureElements = doc.select(".recipeMeasure");

        Elements divs = doc.select("div");
        ArrayList<Element> recipeInfo = new ArrayList<>();
        for (Element div : divs){
            if (div.hasClass("recipeDirection") || div.hasClass("recipeMeasure")){
                recipeInfo.add(div);
            }
        }

        int step_order = 1; //To give correct ordering to a recipe
        for (Element elem : recipeInfo){
            // remove extraneous data wrapped in parentheses
            String text = elem.text();
            text = text.substring(0, (text.contains("(") ? text.indexOf("(") : text.length())).trim();

            RecipeInstruction.TYPE type;
            if (elem.hasClass("recipeDirection")){
                //type = RecipeInstruction.TYPE.DIRECTION;
                int direction_id;

                if(db.getDirection(text) != 0){
                    direction_id = (int)db.getDirection(text);
                }
                else {
                    db.insertDirection(text); //Insert direction into db
                    direction_id = ids[2];
                    ids[2]++; //increment direction_id_fk
                }

                db.insertRecipe(step_order, drink_id, 0, 0, direction_id);
            }
            else {
                //type = RecipeInstruction.TYPE.MEASURE;
                String instruction = elem.toString();
                int i = instruction.indexOf('>');
                int j = instruction.indexOf("<a");
                String serving = instruction.substring(i+1,j-1); //instruction is a serving size and measurement, j-1 MAY BE ERROR

                int serve_id, ingredient_id;

                serving = serving.trim();
                String measurement = serving; //will need to modify this to parse measurement or change the database

                /*realistically we'll need to check if serving already exists
                * if so db.insertRecipe(...,will use old id for ingredient,...)
                * also will not increment ids[1]
                * */
                if(db.getServing(measurement) != 0){ //If serving exists in database
                    serve_id = (int)db.getServing(measurement); //Set id to existing serving
                }
                else {
                    db.insertServing(measurement); //Insert serving into db
                    serve_id = ids[1]; //Set new serving id
                    ids[1]++; //increment serving_id_fk
                }

                instruction = instruction.substring(j);
                i = instruction.indexOf('>');
                j = instruction.indexOf("</a>");
                String ingredient = instruction.substring(i+1,j);
                ingredient = ingredient.trim();

                /*realistically we'll need to check if ingredient already exists
                * if so db.insertRecipe(...,will use old id for ingredient,...)
                * also will not increment ids[0]
                * */
                if(db.getIngredient(ingredient) != 0){
                    ingredient_id = (int)db.getIngredient(ingredient);
                }
                else {
                    db.insertIngredient(ingredient); //Insert ingredient into db
                    ingredient_id = ids[0];
                    ids[0]++; //increment ingredient_id_fk
                }

                db.insertRecipe(step_order, drink_id, ingredient_id, serve_id, 0);
            }
            //this.instructions.add(new RecipeInstruction(text, type));
            step_order++;
        }

        return ids;
    }

    public String toString(){
        String ret = "";
        for (RecipeInstruction rec : this.instructions){
            ret += rec.toString() + ", ";
        }
        return ret;
    }
}