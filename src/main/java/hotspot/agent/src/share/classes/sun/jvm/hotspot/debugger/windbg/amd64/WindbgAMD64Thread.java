/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.debugger.windbg.amd64;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.debugger.amd64.*;
import sun.jvm.hotspot.debugger.windbg.*;
import sun.jvm.hotspot.debugger.windbg.amd64.WindbgAMD64ThreadContext;

class WindbgAMD64Thread implements ThreadProxy {
  private WindbgDebugger debugger;
  private long           sysId;
  private boolean        gotID;
  private long           id;

  // The address argument must be the address of the OSThread::_thread_id
  WindbgAMD64Thread(WindbgDebugger debugger, Address addr) {
    this.debugger = debugger;
    this.sysId    = (long)addr.getCIntegerAt(0, 4, true);
    gotID         = false;
  }

  WindbgAMD64Thread(WindbgDebugger debugger, long sysId) {
    this.debugger = debugger;
    this.sysId    = sysId;
    gotID         = false;
  }

  public ThreadContext getContext() throws IllegalThreadStateException {
    long[] data = debugger.getThreadIntegerRegisterSet(getThreadID());
    WindbgAMD64ThreadContext context = new WindbgAMD64ThreadContext(debugger);
    for (int i = 0; i < data.length; i++) {
      context.setRegister(i, data[i]);
    }
    return context;
  }

  public boolean canSetContext() throws DebuggerException {
    return false;
  }

  public void setContext(ThreadContext thrCtx)
    throws IllegalThreadStateException, DebuggerException {
    throw new DebuggerException("Unimplemented");
  }

  public boolean equals(Object obj) {
    if ((obj == null) || !(obj instanceof WindbgAMD64Thread)) {
      return false;
    }

    return (((WindbgAMD64Thread) obj).getThreadID() == getThreadID());
  }

  public int hashCode() {
    return (int) getThreadID();
  }

  public String toString() {
    return Long.toString(getThreadID());
  }

  /** Retrieves the thread ID of this thread by examining the Thread
      Information Block. */
  private long getThreadID() {
    if (!gotID) {
       id = debugger.getThreadIdFromSysId(sysId);
    }

    return id;
  }
}
