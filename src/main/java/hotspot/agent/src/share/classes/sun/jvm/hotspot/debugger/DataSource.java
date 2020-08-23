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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.debugger;

import java.io.*;

/** An abstraction which represents a seekable data source.
    RandomAccessFile can be trivially mapped to this; in addition, we
    can support an adapter for addresses, so we can parse DLLs
    directly out of the remote process's address space.  This class is
    used by the Windows COFF and Posix ELF implementations. */

public interface DataSource {
  public byte  readByte()       throws IOException;
  public short readShort()      throws IOException;
  public int   readInt()        throws IOException;
  public long  readLong()       throws IOException;
  public int   read(byte[] b)   throws IOException;
  public void  seek(long pos)   throws IOException;
  public long  getFilePointer() throws IOException;
  public void  close()          throws IOException;
}
