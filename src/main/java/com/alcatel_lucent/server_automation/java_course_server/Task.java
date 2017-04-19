package com.alcatel_lucent.server_automation.java_course_server;

import static java.util.Optional.ofNullable;

import org.apache.commons.lang3.StringEscapeUtils;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;


public class Task {

  private final String id;
  private final String name;
  private final String group;
  private final String description;
  private final String package_name;
  private final String test_name;
  private final String class_name;
  private final String code;
  private final String info;

  public Task(String id, String name, String group, String description, String package_name, String test_name, String class_name, String code, String info) {
    this.id = id;
    this.name = name;
    this.group = group;
    this.description = description;
    this.package_name = package_name;
    this.test_name = test_name;
    this.class_name = class_name;
    this.code = code;
    this.info = info;
  }
  
  public String getId() {
    return id;
  }

  public String getPackage_name() {
    return package_name;
  }

  public String getTest_name() {
    return test_name;
  }

  public String getClass_name() {
    return class_name;
  }
  
  public String getName() {
    return name;
  }

  public String getGroup() {
    return group;
  }

  public String getDescription() {
    return description;
  }

  public String getCode() {
    return code;
  }

  public String getInfo() {
    return ofNullable(info).map(s -> HtmlRenderer.builder().build().render(Parser.builder().build().parse(s))).map(StringEscapeUtils::escapeHtml4).orElse(null);
  }
  
}
