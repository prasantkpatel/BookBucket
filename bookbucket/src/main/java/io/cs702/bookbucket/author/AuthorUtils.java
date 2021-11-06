package io.cs702.bookbucket.author;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthorUtils {

    public static Author parseAuthor(String jsonString) {
        try {
            JSONObject authorJson = new JSONObject(jsonString);

            Author author = new Author();
            author.setName(authorJson.optString("name"));
            author.setPersonalName(authorJson.optString("personal_name"));
            author.setId(authorJson.optString("key").replace("/authors/", ""));

            return author;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
