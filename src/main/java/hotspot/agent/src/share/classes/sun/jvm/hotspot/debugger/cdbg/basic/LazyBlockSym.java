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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.debugger.cdbg.basic;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.debugger.cdbg.*;
import sun.jvm.hotspot.debugger.cdbg.basic.BasicCDebugInfoDataBase;
import sun.jvm.hotspot.debugger.cdbg.basic.BasicSym;
import sun.jvm.hotspot.debugger.cdbg.basic.ResolveListener;

public class LazyBlockSym extends BasicSym implements BlockSym {
  private Object key;

  public LazyBlockSym(Object key) {
    super(null);
    this.key = key;
  }

  public BlockSym asBlock()       { return this; }
  public boolean isLazy()         { return true; }

  public Object getKey()          { return key; }

  public BlockSym getParent()     { return null; }
  public long getLength()         { return 0; }
  public Address getAddress()     { return null; }
  public int getNumLocals()       { return 0; }
  public LocalSym getLocal(int i) { throw new RuntimeException("Should not call this"); }

  public void resolve(BasicCDebugInfoDataBase db, ResolveListener listener) {}
}
