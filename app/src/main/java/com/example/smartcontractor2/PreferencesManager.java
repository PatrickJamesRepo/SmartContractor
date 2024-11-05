package com.example.smartcontractor2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {
    private static final String TAG = "PreferencesManager";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "CategoriesPrefs";
    private static final String KEY_CATEGORIES = "categories";
    private static final String KEY_DARK_MODE = "DARK_MODE"; // Key for dark mode preference

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Saves a list of Category objects to SharedPreferences.
     *
     * @param categories The list of categories to save.
     */
    public void saveCategories(List<Category> categories) {
        JSONArray jsonArray = new JSONArray();
        for (Category category : categories) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", category.getName());
                jsonObject.put("formula", category.getFormula());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "Error serializing category: " + category.getName(), e);
            }
        }
        editor.putString(KEY_CATEGORIES, jsonArray.toString());
        editor.apply();
    }

    /**
     * Retrieves the list of Category objects from SharedPreferences.
     *
     * @return A list of categories or null if not found.
     */
    public List<Category> getCategories() {
        String json = sharedPreferences.getString(KEY_CATEGORIES, null);
        if (json != null) {
            List<Category> categories = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String formula = jsonObject.getString("formula");
                    categories.add(new Category(name, formula));
                }
                return categories;
            } catch (JSONException e) {
                Log.e(TAG, "Error deserializing categories", e);
            }
        }
        return null;
    }

    /**
     * Retrieves a specific Category by name.
     *
     * @param name The name of the category to retrieve.
     * @return The Category object or null if not found.
     */
    public Category getCategoryByName(String name) {
        List<Category> categories = getCategories();
        if (categories != null) {
            for (Category category : categories) {
                if (category.getName().equals(name)) {
                    return category;
                }
            }
        }
        return null;
    }

    // New methods for managing dark mode preference

    public void setDarkMode(boolean isDarkMode) {
        editor.putBoolean(KEY_DARK_MODE, isDarkMode);
        editor.apply();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }
}
