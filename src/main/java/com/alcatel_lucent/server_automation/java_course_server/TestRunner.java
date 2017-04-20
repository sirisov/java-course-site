package com.alcatel_lucent.server_automation.java_course_server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import com.alcatel_lucent.server_automation.java_course_server.CodeCompiler.CompilationException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TestRunner {

  private static final SecurityManager DENY_EXIT_SM = new DenyExitSecurityManager();
  private static final File TEST_CLASS_DIR = new File("src/test/java");
  
  public static String run(Task task, String code) {
    JsonObject jo = new JsonObject();
    if (code.contains("System.exit(")) {
      jo.addProperty("compilation", "error");
      jo.addProperty("message", "No way to send System.exit(). Sorry.");
      return new Gson().toJson(jo);
    }
    File outDir = new File("target" + File.separator + "compiled" + File.separator + UUID.randomUUID().toString());
    outDir.mkdirs();
    try {
      URLClassLoader cl = new URLClassLoader(new URL[] {outDir.toURI().toURL()});
      Class<?> classToTest = CodeCompiler.compileClass(task.getPackage_name() + '.' + task.getClass_name(), code, outDir, cl);
      jo.addProperty("compilation", "success");
      TestListenerAdapter tla = new TestListenerAdapter();
      TestNG testng = new TestNG();
      testng.setOutputDirectory(outDir.getPath());
      Class<?> testClass = CodeCompiler.compileClass(task.getPackage_name() + '.' + task.getTest_name(), TEST_CLASS_DIR.toPath().resolve(task.getPackage_name().replace('.', File.separatorChar) + File.separatorChar + task.getTest_name() + ".java").toFile() , outDir, cl);
      testng.setTestClasses(new Class[] { testClass });
      testng.addListener(tla);
      testng.run();
      jo.addProperty("short", "Passed: " + tla.getPassedTests().size() + ", Failed: " + tla.getFailedTests().size() + ", Skipped: " + tla.getSkippedTests().size());
      if (!tla.getFailedTests().isEmpty() || !tla.getSkippedTests().isEmpty()) {
        jo.addProperty("test", "failed");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        tla.getFailedTests().forEach(tr -> {
          ps.println(tr.getName());
          if (tr.getThrowable() != null) {
            if (tr.getThrowable().getClass().equals(AssertionError.class)) {
              ps.println(tr.getThrowable().getMessage());
            } else {
              tr.getThrowable().printStackTrace(ps);
            }
          }
          ps.println();
        });
        jo.addProperty("result", baos.toString());
      } else {
        jo.addProperty("test", "success");
      }
    } catch (CompilationException ex) {
      jo.addProperty("compilation", "error");
      jo.addProperty("message", ex.toString());
    } catch (Exception ex) {
      jo.addProperty("test", "error");
      jo.addProperty("message", ex.toString());
    }
    try {
      FileUtils.deleteDirectory(outDir);
    } catch (IOException ex) {
      System.out.println("Can't delete directory:" + outDir.toString());
    }
    return new Gson().toJson(jo);
  }
  
  private static class DenyExitSecurityManager extends SecurityManager {
    @Override 
    public void checkExit(int status) {
      throw new SecurityException();
    }
  }
  
}
