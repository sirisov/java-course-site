package com.alcatel_lucent.server_automation.java_course_server;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

class PreloadDeserializer implements JsonDeserializer<List<String>>{

  @Override
  public List<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (json.isJsonArray()) {
      return context.deserialize(json, typeOfT);
    } else if (json.isJsonPrimitive()) {
      return Collections.singletonList(context.deserialize(json, String.class));
    } else {
      throw new IllegalArgumentException("Unparsable preload: {" + json.toString() + '}');
    }
  }

}
