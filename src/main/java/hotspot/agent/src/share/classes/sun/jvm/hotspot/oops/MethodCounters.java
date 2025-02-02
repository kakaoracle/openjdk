/*
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
 *
 */

package hotspot.agent.src.share.classes.sun.jvm.hotspot.oops;

import java.io.*;
import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.oops.CIntField;
import sun.jvm.hotspot.oops.Metadata;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.*;
import sun.jvm.hotspot.utilities.*;

public class MethodCounters extends Metadata {
  public MethodCounters(Address addr) {
    super(addr);
  }

  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type      = db.lookupType("MethodCounters");

    interpreterInvocationCountField = new sun.jvm.hotspot.oops.CIntField(type.getCIntegerField("_interpreter_invocation_count"), 0);
    interpreterThrowoutCountField = new sun.jvm.hotspot.oops.CIntField(type.getCIntegerField("_interpreter_throwout_count"), 0);
    if (!VM.getVM().isCore()) {
      invocationCounter        = new sun.jvm.hotspot.oops.CIntField(type.getCIntegerField("_invocation_counter"), 0);
      backedgeCounter          = new sun.jvm.hotspot.oops.CIntField(type.getCIntegerField("_backedge_counter"), 0);
    }
  }

  private static sun.jvm.hotspot.oops.CIntField interpreterInvocationCountField;
  private static sun.jvm.hotspot.oops.CIntField interpreterThrowoutCountField;
  private static sun.jvm.hotspot.oops.CIntField invocationCounter;
  private static CIntField backedgeCounter;

  public int interpreterInvocationCount() {
    return (int) interpreterInvocationCountField.getValue(this);
  }

  public int interpreterThrowoutCount() {
    return (int) interpreterThrowoutCountField.getValue(this);
  }
  public long getInvocationCounter() {
    if (Assert.ASSERTS_ENABLED) {
      Assert.that(!VM.getVM().isCore(), "must not be used in core build");
    }
    return invocationCounter.getValue(this);
  }
  public long getBackedgeCounter() {
    if (Assert.ASSERTS_ENABLED) {
      Assert.that(!VM.getVM().isCore(), "must not be used in core build");
    }
    return backedgeCounter.getValue(this);
  }

  public void printValueOn(PrintStream tty) {
  }
}

