package com.alcatel_lucent.server_automation.java_course_server;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CodeCompiler {
  
  private static final JavaCompiler JC = ToolProvider.getSystemJavaCompiler();
  private static final StandardJavaFileManager FM = JC.getStandardFileManager(null, null, null);
  
  private static Class<?> compileClass(String name, Iterable<? extends JavaFileObject> jfo, File outDir, ClassLoader cl) throws CompilationException {
    StringWriter sw = new StringWriter();
    JC.getTask(sw, null, null, asList("-d", outDir.toString(), "-cp", System.getProperty("java.class.path") + File.pathSeparator + outDir.getPath()), null, jfo).call();
    sw.flush();
    if (!sw.toString().isEmpty()) {
      throw new CompilationException(sw.toString());
    }
    try {
      return cl.loadClass(name);
    } catch (ClassNotFoundException ex) {
      throw new CompilationException(ex.toString());
    }
  }
  
  public static Class<?> compileClass(String name, File source, File outDir, ClassLoader cl) throws CompilationException {
    return compileClass(name, FM.getJavaFileObjects(source), outDir, cl);
  }
  
  public static Class<?> compileClass(String name, String source, File outDir, ClassLoader cl) throws CompilationException {
    return compileClass(name, singletonList(new JavaSourceFromString(name, source)), outDir, cl);
  }
  
  public static class CompilationException extends Exception {
    
    public CompilationException(String message) {
      super(message);
    }
    
  }

  private static class JavaSourceFromString extends SimpleJavaFileObject {

    final String code;

    private JavaSourceFromString(String name, String code) {
      super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
            Kind.SOURCE);
      this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return code;
    }
  }
}
