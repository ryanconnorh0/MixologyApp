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
                db.insertDirection(text); //Insert direction into db
                db.insertRecipe(step_order, drink_id, 0, 0, ids[2]);
                ids[2]++; //increment direction_id_fk
            }
            else {
                //type = RecipeInstruction.TYPE.MEASURE;
                String instruction = elem.toString();
                int i = instruction.indexOf('>');
                int j = instruction.indexOf("<a");
                String serving = instruction.substring(i+1,j-1); //instruction is a serving size and measurement, j-1 MAY BE ERROR

                //i = serving.indexOf("\n");
                serving = serving.trim();
                String quantity = serving; //will need to modify this to parse measurement or change the database
                //String quantity = serving.substring(0, i);
                //String measurement = serving.substring(i+1);

                /*realistically we'll need to check if serving already exists
                * if so db.insertRecipe(...,will use old id for ingredient,...)
                * also will not increment isd[1]
                * */
                db.insertServing(quantity,null); //Insert serving into db

                instruction = instruction.substring(j);
                i = instruction.indexOf('>');
                j = instruction.indexOf("</a>");
                String ingredient = instruction.substring(i+1,j);
                ingredient = ingredient.trim();

                /*realistically we'll need to check if ingredient already exists
                * if so db.insertRecipe(...,will use old id for ingredient,...)
                * also will not increment ids[0]
                * */
                db.insertIngredient(ingredient); //Insert ingredient into db

                db.insertRecipe(step_order, drink_id, ids[0], ids[1], 0);
                ids[0]++; //increment ingredient_id_fk
                ids[1]++; //increment serving_id_fk
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