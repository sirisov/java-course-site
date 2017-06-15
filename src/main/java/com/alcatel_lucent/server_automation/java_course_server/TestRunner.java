package com.alcatel_lucent.server_automation.java_course_server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.LongMemberValue;

import com.alcatel_lucent.server_automation.java_course_server.CodeUtils.CompilationException;
import com.diffplug.common.base.Errors;
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
      URLClassLoader cl = new URLClassLoader(new URL[] {outDir.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
      if (task.preload != null) {
        for (String preload : task.preload) {
          CodeUtils.compileClass(preload, outDir);
        }
      }
      // Compile sent class
      Class<?> userClass = CodeUtils.compileAndLoad(task.getPackage_name() + '.' + task.getClass_name(), code, outDir, cl);
      // Compile tests for class
      //Class<?> testClass = CodeUtils.compileAndLoad(task.getPackage_name() + '.' + task.getTest_name(), TEST_CLASS_DIR.toPath().resolve(task.getPackage_name().replace('.', File.separatorChar) + File.separatorChar + task.getTest_name() + ".java").toFile() , outDir, cl);
      CodeUtils.compileClass(TEST_CLASS_DIR.toPath().resolve(task.getPackage_name().replace('.', File.separatorChar) + File.separatorChar + task.getTest_name() + ".java").toFile() , outDir);
      ClassPool pool = ClassPool.getDefault();
      ClassClassPath cp = new ClassClassPath(userClass);
      pool.insertClassPath(cp);
      CtClass ctClass = Errors.rethrow().get(() -> pool.get(task.getPackage_name() + '.' + task.getTest_name()));
      ClassFile classFile = ctClass.getClassFile();
      ConstPool constPool = classFile.getConstPool();
      AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
      Annotation a = new Annotation("org.testng.annotations.Test", constPool);
      a.addMemberValue("timeOut", new LongMemberValue(5_000L, constPool));
      attr.setAnnotation(a);
      classFile.addAttribute(attr);
      pool.removeClassPath(cp);
      Class testClass = ctClass.toClass(cl, userClass.getProtectionDomain());
      ctClass.detach();
      jo.addProperty("compilation", "success");
      TestListenerAdapter tla = new TestListenerAdapter();
      TestNG testng = new TestNG();
      testng.setOutputDirectory(outDir.getPath());
      testng.setTestClasses(new Class[] { testClass });
      testng.addListener(tla);
      synchronized (System.out) {
        PrintStream out = System.out;
        PrintStream err = System.err;
        try (OutputStream os = new ByteArrayOutputStream(); PrintStream sps = new PrintStream(os)) {
          System.setOut(sps);
          System.setErr(sps);
          testng.run();
          jo.addProperty("output", StringEscapeUtils.escapeHtml4(os.toString()));
        }
        System.setOut(out);
        System.setErr(err);
      }
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
        jo.addProperty("result", StringEscapeUtils.escapeHtml4(baos.toString()));
        ps.close();
      } else {
        jo.addProperty("test", "success");
      }
    } catch (CompilationException ex) {
      jo.addProperty("compilation", "error");
      jo.addProperty("message", StringEscapeUtils.escapeHtml4(ex.toString()));
    } catch (Exception ex) {
      ex.printStackTrace();
      jo.addProperty("test", "error");
      jo.addProperty("message", StringEscapeUtils.escapeHtml4(ex.toString()));
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
