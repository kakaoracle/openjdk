package hotspot.test.testlibrary.ctw.test;/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.Charset;

import com.oracle.java.testlibrary.JDKToolFinder;
import com.oracle.java.testlibrary.OutputAnalyzer;

public abstract class CtwTest {
    protected final String[] shouldContain;
    protected CtwTest(String[] shouldContain) {
        this.shouldContain = shouldContain;
    }

    public void run(String[] args) throws Exception {
        if (args.length == 0) {
            throw new Error("args is empty");
        }
        switch (args[0]) {
            case "prepare":
                prepare();
                break;
            case "check":
                check(args);
                break;
            default:
                throw new Error("unregonized action -- " + args[0]);
        }
    }

    protected void prepare() throws Exception { }

    protected void check(String[] args) throws Exception  {
        if (args.length < 2) {
            throw new Error("logfile isn't specified");
        }
        String logfile = args[1];
        try (BufferedReader r = Files.newBufferedReader(Paths.get(logfile),
                Charset.defaultCharset())) {
            OutputAnalyzer output = readOutput(r);
           for (String test : shouldContain) {
                output.shouldContain(test);
            }
        }
    }

    private static OutputAnalyzer readOutput(BufferedReader reader)
            throws IOException {
        StringBuilder builder = new StringBuilder();
        String eol = String.format("%n");
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(eol);
        }
        return new OutputAnalyzer(builder.toString(), "");
    }

    protected void dump(OutputAnalyzer output, String name) {
        try (Writer w = new FileWriter(name + ".out")) {
            String s = output.getStdout();
            w.write(s, s.length(), 0);
        } catch (IOException io) {
            io.printStackTrace();
        }
        try (Writer w = new FileWriter(name + ".err")) {
            String s = output.getStderr();
            w.write(s, s.length(), 0);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    protected ProcessBuilder createJarProcessBuilder(String... command)
            throws Exception {
        String javapath = JDKToolFinder.getJDKTool("jar");

        ArrayList<String> args = new ArrayList<>();
        args.add(javapath);
        Collections.addAll(args, command);

        return new ProcessBuilder(args.toArray(new String[args.size()]));
    }
}
