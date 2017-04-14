package com.alcatel_lucent.server_automation.java_course_server;

import static java.nio.file.Files.lines;
import static java.util.stream.Collectors.toList;

import static spark.Spark.get;
import static spark.Spark.port;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.util.streamex.StreamEx;

import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Server {

  private static <K,V> Entry<K,V> pair(K key, V value) {
    return new SimpleEntry<>(key, value);
  }
  
  private static String[] splitInTwo(String s) {
    return new String[] {s.substring(0, s.lastIndexOf(':')), s.substring(s.lastIndexOf(':') + 1)};
  }
  
  public static void main(String[] args) throws IOException {
    port(Integer.valueOf(System.getenv().getOrDefault("PORT", "8080")));
    Spark.staticFileLocation("/public");
    Map<String, Object> resources = StreamEx.of(
            pair("presentations", lines(Paths.get("src/main/resources/presentations.list")).map(Server::splitInTwo).map(Presentation::new).collect(toList())),
            pair("tasks", new Gson().fromJson(new FileReader("src/main/resources/java_base.txt"), new TypeToken<List<Task>>(){}.getType()))
    ).toMap(Entry::getKey, Entry::getValue);
    get("/", (req, res) -> new ModelAndView(resources, "index.ftl"), new FreeMarkerEngine());
  }
}
