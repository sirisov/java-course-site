package com.alcatel_lucent.server_automation.java_course_server;

import static java.nio.file.Files.lines;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;

import static com.diffplug.common.base.Errors.rethrow;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.util.streamex.StreamEx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

public class Server {

  private static final String MY_IP = "10\\.97\\.24\\.20|0:0:0:0:0:0:0:1|127\\.0\\.0\\.1";
  private static final Logger LOG = LoggerFactory.getLogger(Server.class);
  private static final String RES_DIR = "src/main/resources/";
  private static final String PRESENTATIONS_FILE = "presentations.list";
  private static final String TASKS_FILE = "tasks.json";
  private static final String RESP_NAME = "response.json";
  private static final Path RES_PATH = Paths.get(RES_DIR);
  private static final Map<String, Function<Path, Object>> PATH_TO_LIST = new ConcurrentHashMap<>();
  private static final Map<String, Object> RESOURCES = new ConcurrentHashMap<>();
  private static final Map<String, Map<String, String>> RESPONSES = new ConcurrentHashMap<>();
  private static final File RESP_FILE = RES_PATH.resolve(RESP_NAME).toFile();
  private static final Gson GSON = new Gson();
  private static WatchKey watch;
  
  static {
    try {
      RESP_FILE.createNewFile();
      if (RESP_FILE.length() > 0){
        RESPONSES.putAll(GSON.fromJson(new FileReader(RESP_FILE), new TypeToken<Map<String, Map<String, String>>>(){}.getType()));
      }
    } catch (IOException ex) {
      LOG.warn("Can't create file " + RESP_FILE, ex);
    }
    PATH_TO_LIST.put(RESP_NAME, rethrow().wrapFunction(path -> new Gson().fromJson(new FileReader(path.toFile()), new TypeToken<Map<String, Map<String, String>>>(){}.getType())));
    PATH_TO_LIST.put(PRESENTATIONS_FILE, rethrow().wrapFunction(path -> lines(path).map(Server::splitInTwo).map(Presentation::new).collect(toList())));
    PATH_TO_LIST.put(TASKS_FILE, rethrow().wrapFunction(path -> new Gson().<List<Task>>fromJson(new FileReader(path.toFile()), new TypeToken<List<Task>>(){}.getType())
                                                                .stream().collect(Collectors.groupingBy(Task::getGroup))));
  }
  
  private static <K,V> Entry<K,V> pair(K key, V value) {
    return new SimpleEntry<>(key, value);
  }
  
  private static String[] splitInTwo(String s) {
    return new String[] {s.substring(0, s.lastIndexOf(':')), s.substring(s.lastIndexOf(':') + 1)};
  }
  
  private static void reloadResource(Path resPath) {
    RESOURCES.put(resPath.toFile().getName().replaceAll("\\.[^\\.]+$", ""), PATH_TO_LIST.get(resPath.toFile().getName()).apply(resPath));
  }
  
  private static Map<String, Object> getResources() {
    List<WatchEvent<?>> pollEvents = watch.pollEvents();
    pollEvents.stream()
              .filter(e -> e.kind() == ENTRY_MODIFY)
              .map(e -> e.context()).map(Path.class::cast)
              .filter(p -> p.toString().equals(PRESENTATIONS_FILE) || p.toString().equals(TASKS_FILE) || p.toString().equalsIgnoreCase(RESP_NAME))
              .map(RES_PATH::resolve)
              .forEach(Server::reloadResource);
    return RESOURCES;
  }
  
  private static String renderFTL(String ftl) {
    return new FreeMarkerEngine().render(new ModelAndView(getResources(), ftl));
  }
  
  public static void main(String[] args) throws IOException {
    reloadResource(Paths.get(RES_DIR + PRESENTATIONS_FILE));
    reloadResource(Paths.get(RES_DIR + TASKS_FILE));
    watch = RES_PATH.register(FileSystems.getDefault().newWatchService(), ENTRY_MODIFY);
    port(Integer.valueOf(System.getenv().getOrDefault("PORT", "8080")));
    Spark.staticFileLocation("/public");
    get("/", (req, res) -> renderFTL("index.ftl"));
    get("/codes", (req, res) -> req.ip().matches(MY_IP) ? renderFTL("codes.ftl") : halt(403, "Access Forbidden").body());
    post("/code/:id", (req, res) -> {
      LOG.info(req.ip() + " --> " + req.params(":id"));
      LOG.info(req.body());
      Task task = StreamEx.ofValues(((Map<String, List<Task>>)getResources().get("tasks"))).flatCollection(l -> l).findFirst(t -> t.getId().equals(req.params(":id"))).get();
      String result = TestRunner.run(task, req.body());
      if (ofNullable(new JsonParser().parse(result).getAsJsonObject().get("test")).map(JsonElement::getAsString).filter("success"::equals).isPresent()) {
        RESPONSES.computeIfAbsent(req.params(":id"), k -> new ConcurrentHashMap<>()).put(req.ip(), req.body());
        synchronized(RESP_FILE) {
          try (JsonWriter jw = new JsonWriter(new FileWriter(RESP_FILE))) {
            GSON.toJson(RESPONSES, new TypeToken<Map<String, Map<String, String>>>(){}.getType(), jw);
          }
        }
      }
      return result;
    });
  }
}
