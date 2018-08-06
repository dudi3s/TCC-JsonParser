/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eduardo.parser;

/**
 *
 * @author eduardo
 */
public class Tweet {

    private String location, id;

    public Tweet(String location, String text) {
        this.location = location;
        this.id = text;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Tweet{" + "location=" + location + ", id=" + id + '}';
    }
}
