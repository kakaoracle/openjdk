/*
 * Copyright (c) 2002, 2008, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.debugger.windbg;

import java.util.List;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.debugger.cdbg.*;
import sun.jvm.hotspot.debugger.windbg.WindbgAddress;
import sun.jvm.hotspot.debugger.windbg.WindbgOopHandle;

/** An extension of the JVMDebugger interface with a few additions to
    support 32-bit vs. 64-bit debugging as well as features required
    by the architecture-specific subpackages. */

public interface WindbgDebugger extends JVMDebugger {
  public String       addressValueToString(long address) throws DebuggerException;
  public boolean      readJBoolean(long address) throws DebuggerException;
  public byte         readJByte(long address) throws DebuggerException;
  public char         readJChar(long address) throws DebuggerException;
  public double       readJDouble(long address) throws DebuggerException;
  public float        readJFloat(long address) throws DebuggerException;
  public int          readJInt(long address) throws DebuggerException;
  public long         readJLong(long address) throws DebuggerException;
  public short        readJShort(long address) throws DebuggerException;
  public long         readCInteger(long address, long numBytes, boolean isUnsigned)
    throws DebuggerException;
  public sun.jvm.hotspot.debugger.windbg.WindbgAddress readAddress(long address) throws DebuggerException;
  public sun.jvm.hotspot.debugger.windbg.WindbgAddress readCompOopAddress(long address) throws DebuggerException;
  public sun.jvm.hotspot.debugger.windbg.WindbgAddress readCompKlassAddress(long address) throws DebuggerException;
  public WindbgOopHandle readOopHandle(long address) throws DebuggerException;
  public WindbgOopHandle readCompOopHandle(long address) throws DebuggerException;

  // The returned array of register contents is guaranteed to be in
  // the same order as in the DbxDebugger for Solaris/x86 or amd64; that is,
  // the indices match those in debugger/x86/X86ThreadContext.java or
  // debugger/amd64/AMD64ThreadContext.java.
  public long[]       getThreadIntegerRegisterSet(long threadId) throws DebuggerException;
  public Address      newAddress(long value) throws DebuggerException;

  public long         getThreadIdFromSysId(long sysId) throws DebuggerException;
  // Support for the CDebugger interface. Retrieves the thread list of
  // the target process as a List of ThreadProxy objects.
  public List/*<ThreadProxy>*/ getThreadList() throws DebuggerException;

  // Support for the CDebugger interface. Retrieves a List of the
  // loadobjects in the target process.
  public List/*<LoadObject>*/ getLoadObjectList() throws DebuggerException;

  // NOTE: this interface implicitly contains the following methods:
  // From the Debugger interface via JVMDebugger
  //   public void attach(int processID) throws DebuggerException;
  //   public void attach(String executableName, String coreFileName) throws DebuggerException;
  //   public boolean detach();
  //   public Address parseAddress(String addressString) throws NumberFormatException;
  //   public long getAddressValue(Address addr) throws DebuggerException;
  //   public String getOS();
  //   public String getCPU();
  // From the SymbolLookup interface via Debugger and JVMDebugger
  //   public Address lookup(String objectName, String symbol);
  //   public OopHandle lookupOop(String objectName, String symbol);
  // From the JVMDebugger interface
  //   public void configureJavaPrimitiveTypeSizes(long jbooleanSize,
  //                                               long jbyteSize,
  //                                               long jcharSize,
  //                                               long jdoubleSize,
  //                                               long jfloatSize,
  //                                               long jintSize,
  //                                               long jlongSize,
  //                                               long jshortSize);
  // From the ThreadAccess interface via Debugger and JVMDebugger
  //   public ThreadProxy getThreadForIdentifierAddress(Address addr);

  public int getAddressSize();
}
