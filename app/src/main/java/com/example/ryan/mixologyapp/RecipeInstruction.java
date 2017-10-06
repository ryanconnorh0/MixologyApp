package com.example.ryan.mixologyapp;

class RecipeInstruction {
    private String text;
    private TYPE type;

    public static enum TYPE { MEASURE, DIRECTION }

    public RecipeInstruction(String text, TYPE type){
        setText(text);
        setType(type);
    }

    // Sets
    public void setText(String text){ this.text = text; }
    public void setType(TYPE type){ this.type = type; }

    // Gets
    public String getText(){ return this.text; }
    public TYPE getType(){ return this.type; }

    public String toString(){
        return (this.type == TYPE.MEASURE ? "Measure" : "Direction") + ": " + this.text;
    }
}
