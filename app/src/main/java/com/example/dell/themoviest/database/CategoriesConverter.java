package com.example.dell.themoviest.database;


import android.arch.persistence.room.TypeConverter;

import com.example.dell.themoviest.model.Category;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class CategoriesConverter {
    @TypeConverter
    public static List<Category> fromString(String categoriesHolder)
    {
        Type listType = new TypeToken<List<Category>>()
        {}.getType();
        return new Gson().fromJson(categoriesHolder , listType);
    }

    @TypeConverter
    public static String fromArrayList(List<Category> categories)
    {
        Gson gson = new Gson();
        String json = gson.toJson(categories);
        return json;
    }
}
