package com.pdgc.general.util.json;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Implementation of {@link JsonObjectFactory} which uses json.org library
 * 
 * @author Vishal Raut
 */
public class JsonOrgJsonObjectFactory implements JsonObjectFactory {

	@Override
	public JsonObject createJsonObject(String jsonString) {
		return new JsonOrgJsonObject(new JSONObject(jsonString));
	}

	private static class JsonOrgJsonObject implements JsonObject {

		private JSONObject jsonObject;
		private Map<String, JsonObject> jsonObjectByKeys = new HashMap<>();

		private JsonOrgJsonObject(JSONObject jsonObject) {
			this.jsonObject = jsonObject;
		}

		@Override
		public Boolean getBoolean(String key) {
			return has(key) ? jsonObject.getBoolean(key) : null;
		}

		@Override
		public Integer getInt(String key) {
			return has(key) ? jsonObject.getInt(key) : null;
		}

		@Override
		public Long getLong(String key) {
			return has(key) ? jsonObject.getLong(key) : null;
		}

		@Override
		public Double getDouble(String key) {
			return has(key) ? jsonObject.getDouble(key) : null;
		}

		@Override
		public String getString(String key) {
			return has(key) ? jsonObject.getString(key) : null;
		}
		
		
		@Override
		public JsonObject getJsonObject(String key) {
			if (has(key)) {
				if (!jsonObjectByKeys.containsKey(key)) {
					JsonOrgJsonObject child = new JsonOrgJsonObject((JSONObject) jsonObject.get(key));
					jsonObjectByKeys.put(key, child);
					return child;
				}
				return jsonObjectByKeys.get(key);
			} else {
				return null;
			}
		}

		@Override
		public JsonArray getJsonArray(String key) {
			return has(key) ? new JsonOrgJsonArray(jsonObject.getJSONArray(key)) : null;
		}

		@Override
		public boolean has(String key) {
			return jsonObject.has(key);
		}

	}

	private static class JsonOrgJsonArray implements JsonArray {

		private WeakReference<JSONArray> jsonArray;

		private JsonOrgJsonArray(JSONArray jsonArray) {
			this.jsonArray = new WeakReference<JSONArray>(jsonArray);
		}

		@Override
		public Iterator<Object> iterator() {
			List<Object> arrayList = new ArrayList<>();
			jsonArray.get().forEach(o -> {
				if (o instanceof JSONObject) {
					arrayList.add(new JsonOrgJsonObject((JSONObject) o));
				} else {
					arrayList.add(o);
				}
			});
			return arrayList.iterator();
		}

	}

}
