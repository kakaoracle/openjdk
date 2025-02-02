/*
 * Copyright (c) 2002, 2012, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.debugger.linux;

import java.lang.reflect.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.debugger.linux.LinuxDebugger;
import sun.jvm.hotspot.debugger.linux.amd64.*;
import sun.jvm.hotspot.debugger.linux.ia64.*;
import sun.jvm.hotspot.debugger.linux.x86.*;
import sun.jvm.hotspot.debugger.linux.sparc.*;

class LinuxThreadContextFactory {
   static ThreadContext createThreadContext(LinuxDebugger dbg) {
      String cpu = dbg.getCPU();
      if (cpu.equals("x86")) {
         return new LinuxX86ThreadContext(dbg);
      } else if (cpu.equals("amd64")) {
         return new LinuxAMD64ThreadContext(dbg);
      } else if (cpu.equals("ia64")) {
         return new LinuxIA64ThreadContext(dbg);
      } else if (cpu.equals("sparc")) {
         return new LinuxSPARCThreadContext(dbg);
      } else  {
        try {
          Class tcc = Class.forName("sun.jvm.hotspot.debugger.linux." +
             cpu.toLowerCase() + ".Linux" + cpu.toUpperCase() +
             "ThreadContext");
          Constructor[] ctcc = tcc.getConstructors();
          return (ThreadContext)ctcc[0].newInstance(dbg);
        } catch (Exception e) {
          throw new RuntimeException("cpu " + cpu + " is not yet supported");
        }
      }
   }
}
