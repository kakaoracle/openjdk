package hotspot.test.runtime.RedefineObject;/*
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
import java.io.PrintWriter;
import com.oracle.java.testlibrary.*;

/*
 * Test to redefine java/lang/Object and verify that it doesn't crash on vtable
 * call on basic array type.
 * Test to redefine java/lang/ClassLoader and java/lang/reflect/Method to make
 * sure cached versions used afterward are the current version.
 *
 * @test
 * @bug 8005056
 * @bug 8009728
 * @library /testlibrary
 * @build Agent
 * @run main ClassFileInstaller Agent
 * @run main TestRedefineObject
 * @run main/othervm -javaagent:agent.jar -XX:TraceRedefineClasses=5 Agent
 */
public class TestRedefineObject {
    public static void main(String[] args) throws Exception  {

      PrintWriter pw = new PrintWriter("MANIFEST.MF");
      pw.println("Premain-Class: Agent");
      pw.println("Can-Retransform-Classes: true");
      pw.close();

      ProcessBuilder pb = new ProcessBuilder();
      pb.command(new String[] { JDKToolFinder.getJDKTool("jar"), "cmf", "MANIFEST.MF", "agent.jar", "Agent.class"});
      pb.start().waitFor();
    }
}
