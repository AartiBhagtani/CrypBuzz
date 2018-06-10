package com.example.navin.cryptbuzz;

class Coins{
    String title,description;
    public Coins(String title, String description) {
        super();
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }
}