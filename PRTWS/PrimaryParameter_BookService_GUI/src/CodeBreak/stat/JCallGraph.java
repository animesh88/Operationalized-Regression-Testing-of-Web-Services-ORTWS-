/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package CodeBreak.stat;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassParser;

/**
 * Constructs a callgraph out of a JAR archive. Can combine multiple archives
 * into a single call graph.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
public class JCallGraph {

    public static void main(String[] args) {
//    File dir = new File("D:\\others\\RTST\\BookService\\BooKService\\test");
//            String[] children = dir.list();
        String arg = "D:\\others 1\\RTST\\BookService\\test\\BooKService.war";
        ClassParser cp;
        try {
            File f = new File(arg);

            if (!f.exists()) {
                System.err.println("Jar file " + arg + " does not exist");
            }

            JarFile jar = new JarFile(f);

            Enumeration<JarEntry> entries = jar.entries();
            File callGraphFolder = new File(f.getParent() + "//CallGraph//");
            callGraphFolder.mkdirs();
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(100);
            File callGraphFile = new File(f.getParent() + "//CallGraph//" + randomInt + ".txt");

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }

                if (!entry.getName().endsWith(".class")) {
                    continue;
                }

                cp = new ClassParser(arg, entry.getName());
                ClassVisitor visitor = new ClassVisitor(cp.parse(), callGraphFile.getAbsolutePath());
                visitor.start();
            }

        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void callGraphFile(String arg, int randomInt) {
        ClassParser cp;
        try {
            File f = new File(arg);

            if (!f.exists()) {
                System.err.println("Jar file " + arg + " does not exist");
            }

            JarFile jar = new JarFile(f);

            Enumeration<JarEntry> entries = jar.entries();
            File callGraphFolder = new File(f.getParent() + "//CallGraph//");
            callGraphFolder.mkdirs();
            File callGraphFile = new File(f.getParent() + "//CallGraph//" + randomInt + ".txt");

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }

                if (!entry.getName().endsWith(".class")) {
                    continue;
                }

                cp = new ClassParser(arg, entry.getName());
                ClassVisitor visitor = new ClassVisitor(cp.parse(), callGraphFile.getAbsolutePath());
                visitor.start();
            }

        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}