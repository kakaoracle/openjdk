/*
 * Copyright (c) 2000, 2001, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.debugger.win32.coff;

// FIXME: NOT FINISHED

import sun.jvm.hotspot.debugger.win32.coff.COFFException;
import sun.jvm.hotspot.debugger.win32.coff.DataDirectory;
import sun.jvm.hotspot.debugger.win32.coff.DebugDirectory;
import sun.jvm.hotspot.debugger.win32.coff.ExportDirectoryTable;

/** Models the information stored in the data directories portion of
    the optional header of a Portable Executable file. FIXME: the
    DataDirectory objects are less than useful; need to bring up more
    of the data structures defined in the documentation. (Some of the
    descriptions are taken directly from Microsoft's documentation and
    are copyrighted by Microsoft.) */

public interface OptionalHeaderDataDirectories {
  /** Export Table address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getExportTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Returns the Export Table, or null if none was present. */
  public ExportDirectoryTable getExportDirectoryTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Import Table address and size */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getImportTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Resource Table address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getResourceTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Exception Table address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getExceptionTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Attribute Certificate Table address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getCertificateTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Base Relocation Table address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getBaseRelocationTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Debug data starting address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getDebug() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Returns the Debug Directory, or null if none was present. */
  public DebugDirectory getDebugDirectory() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Architecture-specific data address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getArchitecture() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Relative virtual address of the value to be stored in the global
      pointer register. Size member of this structure must be set to
      0. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getGlobalPtr() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Thread Local Storage (TLS) Table address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getTLSTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Load Configuration Table address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getLoadConfigTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Bound Import Table address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getBoundImportTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Import Address Table address and size. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getImportAddressTable() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** Address and size of the Delay Import Descriptor. */
  public sun.jvm.hotspot.debugger.win32.coff.DataDirectory getDelayImportDescriptor() throws sun.jvm.hotspot.debugger.win32.coff.COFFException;

  /** COM+ Runtime Header address and size */
  public DataDirectory getCOMPlusRuntimeHeader() throws COFFException;
}
