package com.alcatel_lucent.server_automation.java_course_server;

import static java.util.Optional.ofNullable;

import org.apache.commons.lang3.StringEscapeUtils;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;


public class Task {

  private final String name;
  private final String group;
  private final String description;
  private final String code;
  private final String info;

  public Task(String name, String group, String description, String code, String info) {
    this.name = name;
    this.group = group;
    this.description = description;
    this.code = code;
    this.info = info;
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
