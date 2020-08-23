/*
 * Copyright (c) 2002, 2004, Oracle and/or its affiliates. All rights reserved.
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
 *
 */

package hotspot.agent.src.share.classes.sun.jvm.hotspot.utilities;

import java.io.*;
import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.tools.jcore.*;
import sun.jvm.hotspot.utilities.SystemDictionaryHelper;

/**
   Class loader that loads classes from the process/core image
   of the debuggee.
*/

public class ProcImageClassLoader extends ClassLoader {
   public ProcImageClassLoader(ClassLoader parent) {
      super(parent);
   }

   public ProcImageClassLoader() {
      this(Thread.currentThread().getContextClassLoader());
   }

   protected Class findClass(String className) throws ClassNotFoundException {
      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         InstanceKlass klass = SystemDictionaryHelper.findInstanceKlass(className);
         ClassWriter cw = new ClassWriter(klass, bos);
         cw.write();
         byte[] buf = bos.toByteArray();
         return defineClass(className, buf, 0, buf.length);
      } catch (Exception e) {
         throw (ClassNotFoundException) new ClassNotFoundException().initCause(e);
      }
   }
}
