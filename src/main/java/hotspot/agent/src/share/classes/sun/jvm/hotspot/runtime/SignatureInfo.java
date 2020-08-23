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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.runtime;

import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.runtime.BasicType;
import sun.jvm.hotspot.runtime.BasicTypeSize;
import sun.jvm.hotspot.runtime.SignatureIterator;

public abstract class SignatureInfo extends SignatureIterator {
  protected boolean hasIterated; // need this because iterate cannot be called in constructor (set is virtual!)
  protected int     size;
  protected int     type;        // BasicType

  protected void lazyIterate() {
    if (!hasIterated) {
      iterate();
      hasIterated = true;
    }
  }

  protected abstract void set(int size, int /*BasicType*/ type);

  public void doBool()                     { set(sun.jvm.hotspot.runtime.BasicTypeSize.getTBooleanSize(), sun.jvm.hotspot.runtime.BasicType.getTBoolean()); }
  public void doChar()                     { set(sun.jvm.hotspot.runtime.BasicTypeSize.getTCharSize(),    sun.jvm.hotspot.runtime.BasicType.getTChar());    }
  public void doFloat()                    { set(sun.jvm.hotspot.runtime.BasicTypeSize.getTFloatSize(),   sun.jvm.hotspot.runtime.BasicType.getTFloat());   }
  public void doDouble()                   { set(sun.jvm.hotspot.runtime.BasicTypeSize.getTDoubleSize(),  sun.jvm.hotspot.runtime.BasicType.getTDouble());  }
  public void doByte()                     { set(sun.jvm.hotspot.runtime.BasicTypeSize.getTByteSize(),    sun.jvm.hotspot.runtime.BasicType.getTByte());    }
  public void doShort()                    { set(sun.jvm.hotspot.runtime.BasicTypeSize.getTShortSize(),   sun.jvm.hotspot.runtime.BasicType.getTShort());   }
  public void doInt()                      { set(sun.jvm.hotspot.runtime.BasicTypeSize.getTIntSize(),     sun.jvm.hotspot.runtime.BasicType.getTInt());     }
  public void doLong()                     { set(sun.jvm.hotspot.runtime.BasicTypeSize.getTLongSize(),    sun.jvm.hotspot.runtime.BasicType.getTLong());    }
  public void doVoid()                     { set(sun.jvm.hotspot.runtime.BasicTypeSize.getTVoidSize(),    sun.jvm.hotspot.runtime.BasicType.getTVoid());    }
  public void doObject(int begin, int end) { set(sun.jvm.hotspot.runtime.BasicTypeSize.getTObjectSize(),  sun.jvm.hotspot.runtime.BasicType.getTObject());  }
  public void doArray(int begin, int end)  { set(BasicTypeSize.getTArraySize(),   sun.jvm.hotspot.runtime.BasicType.getTArray());   }

  public SignatureInfo(Symbol signature) {
    super(signature);

    type = BasicType.getTIllegal();
  }

  public int size() { lazyIterate(); return size; }
  public int type() { lazyIterate(); return type; }
}
