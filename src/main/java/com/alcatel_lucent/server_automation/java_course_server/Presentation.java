package com.alcatel_lucent.server_automation.java_course_server;

public class Presentation {

  private String name;
  private String source;

  Presentation(String[] array) {
    this(array[0], array[1]);
  }
  
  public Presentation(String name, String source) {
    this.name = name;
    this.source = source;
  }
  
  public String getName() {
    return name;
  }

  public String getSource() {
    return source;
  }
  
}
