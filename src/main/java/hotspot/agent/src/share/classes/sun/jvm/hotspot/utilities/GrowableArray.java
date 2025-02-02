/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.types.*;
import sun.jvm.hotspot.utilities.GenericGrowableArray;

public class GrowableArray<T> extends GenericGrowableArray {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type      = db.lookupType("GrowableArray<int>");
    dataField = type.getAddressField("_data");
  }

  private static AddressField dataField;

  private InstanceConstructor<T> virtualConstructor;

  public static <S> GrowableArray<S> create(Address addr, InstanceConstructor<S> v) {
    if (addr == null) return null;
    return new GrowableArray<S>(addr, v);
  }

  public T at(int i) {
    if (i < 0 || i >= length()) throw new ArrayIndexOutOfBoundsException(i);
    Address data = dataField.getValue(getAddress());
    Address addr = data.getAddressAt(i * VM.getVM().getAddressSize());
    if (addr == null) return null;
    return (T) virtualConstructor.instantiateWrapperFor(addr);
  }

  private GrowableArray(Address addr, InstanceConstructor<T> v) {
    super(addr);
    virtualConstructor = v;
  }
}
