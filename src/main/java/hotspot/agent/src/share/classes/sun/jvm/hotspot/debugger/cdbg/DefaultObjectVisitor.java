/*
 * Copyright (c) 2001, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.debugger.cdbg;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.debugger.cdbg.FieldIdentifier;
import sun.jvm.hotspot.debugger.cdbg.ObjectVisitor;
import sun.jvm.hotspot.debugger.cdbg.Type;

/** Implementation of the ObjectVisitor interface with all methods
    empty */

public class DefaultObjectVisitor implements ObjectVisitor {
  public void enterType(Type type, Address objectAddress) {}
  public void exitType() {}
  public void doBit(sun.jvm.hotspot.debugger.cdbg.FieldIdentifier f, long val) {}
  public void doInt(sun.jvm.hotspot.debugger.cdbg.FieldIdentifier f, long val) {}
  public void doEnum(sun.jvm.hotspot.debugger.cdbg.FieldIdentifier f, long val, String enumName) {}
  public void doFloat(sun.jvm.hotspot.debugger.cdbg.FieldIdentifier f, float val) {}
  public void doDouble(sun.jvm.hotspot.debugger.cdbg.FieldIdentifier f, double val) {}
  public void doPointer(sun.jvm.hotspot.debugger.cdbg.FieldIdentifier f, Address val) {}
  public void doArray(sun.jvm.hotspot.debugger.cdbg.FieldIdentifier f, Address val) {}
  public void doRef(sun.jvm.hotspot.debugger.cdbg.FieldIdentifier f, Address val) {}
  public void doCompound(FieldIdentifier f, Address addressOfEmbeddedCompoundObject) {}
}
