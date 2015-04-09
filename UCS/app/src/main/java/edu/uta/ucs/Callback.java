package edu.uta.ucs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by arunk_000 on 4/9/2015.
 */
public interface Callback{
    void onResult(JSONObject result) throws JSONException;
}
