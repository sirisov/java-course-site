package com.alcatel_lucent.server_automation.java_course_server;

import static java.nio.file.Files.lines;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.stream.Collectors.toList;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import static com.diffplug.common.base.Errors.rethrow;

import java.io.FileReader;
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
import com.google.gson.reflect.TypeToken;

public class Server {

  private static final Logger LOG = LoggerFactory.getLogger(Server.class);
  private static final String RES_DIR = "src/main/resources/";
  private static final String PRESENTATIONS_FILE = "presentations.list";
  private static final String TASKS_FILE = "tasks.json";
  private static final Path RES_PATH = Paths.get(RES_DIR);
  private static final Map<String, Function<Path, Object>> PATH_TO_LIST = new ConcurrentHashMap<>();
  private static final Map<String, Object> RESOURCES = new ConcurrentHashMap<>();
  private static WatchKey watch;
  
  static {
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
  
  private static Map<String, Object> getResources() throws IOException {
    List<WatchEvent<?>> pollEvents = watch.pollEvents();
    pollEvents.stream()
              .filter(e -> e.kind() == ENTRY_MODIFY)
              .map(e -> e.context()).map(Path.class::cast)
              .map(RES_PATH::resolve)
              .forEach(Server::reloadResource);
    return RESOURCES;
  }
  
  public static void main(String[] args) throws IOException {
    reloadResource(Paths.get(RES_DIR + PRESENTATIONS_FILE));
    reloadResource(Paths.get(RES_DIR + TASKS_FILE));
    watch = RES_PATH.register(FileSystems.getDefault().newWatchService(), ENTRY_MODIFY);
    port(Integer.valueOf(System.getenv().getOrDefault("PORT", "8080")));
    Spark.staticFileLocation("/public");
    get("/", (req, res) -> new ModelAndView(getResources(), "index.ftl"), new FreeMarkerEngine());
    post("/code/:id", (req, res) -> {
      LOG.info(req.ip() + " --> " + req.params(":id"));
      LOG.info(req.body());
      Task task = StreamEx.ofValues(((Map<String, List<Task>>)getResources().get("tasks"))).flatCollection(l -> l).findFirst(t -> t.getId().equals(req.params(":id"))).get();
      return TestRunner.run(task, req.body());
    });
  }
}
