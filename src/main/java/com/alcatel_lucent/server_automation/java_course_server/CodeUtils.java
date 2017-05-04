package com.alcatel_lucent.server_automation.java_course_server;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CodeUtils {
  
  private static final JavaCompiler JC = ToolProvider.getSystemJavaCompiler();
  private static final StandardJavaFileManager FM = JC.getStandardFileManager(null, null, null);
  private static final Pattern NAME_PATTERN = Pattern.compile("^public (?:abstract )?(?:class|interface|enum) (\\w+) .*");
  
  private static void compileClass(Iterable<? extends JavaFileObject> jfo, File outDir) throws CompilationException {
    StringWriter sw = new StringWriter();
    JC.getTask(sw, null, null, asList("-d", outDir.toString(), "-cp", System.getProperty("java.class.path") + File.pathSeparator + outDir.getPath()), null, jfo).call();
    sw.flush();
    if (!sw.toString().isEmpty()) {
      throw new CompilationException(sw.toString());
    } 
  }
  
  private static Class<?> loadClass(String name, ClassLoader cl) throws CompilationException {
    try {
      return cl.loadClass(name);
    } catch (ClassNotFoundException ex) {
      throw new CompilationException(ex.toString());
    }
  }
  
  public static void compileClass(String source, File outDir) throws CompilationException {
    String name = NAME_PATTERN.matcher(Stream.of(source.split("\n")).filter(NAME_PATTERN.asPredicate()).findFirst().get()).replaceAll("$1");
    compileClass(singletonList(new JavaSourceFromString(name, source)), outDir);
  }
  
  public static Class<?> compileAndLoad(String name, File source, File outDir, ClassLoader cl) throws CompilationException {
    compileClass(FM.getJavaFileObjects(source), outDir);
    return loadClass(name, cl);
  }
  
  public static Class<?> compileAndLoad(String name, String source, File outDir, ClassLoader cl) throws CompilationException {
    compileClass(singletonList(new JavaSourceFromString(name, source)), outDir);
    return loadClass(name, cl);
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
