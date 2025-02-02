/*
 * Copyright (c) 2000, 2002, Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.runtime.VMObject;
import sun.jvm.hotspot.types.*;

public class VirtualSpace extends VMObject {
  private static AddressField lowField;
  private static AddressField highField;
  private static AddressField lowBoundaryField;
  private static AddressField highBoundaryField;

  static {
    sun.jvm.hotspot.runtime.VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) {
    Type type = db.lookupType("VirtualSpace");

    lowField          = type.getAddressField("_low");
    highField         = type.getAddressField("_high");
    lowBoundaryField  = type.getAddressField("_low_boundary");
    highBoundaryField = type.getAddressField("_high_boundary");
  }

  public VirtualSpace(Address addr) {
    super(addr);
  }

  public Address low()                          { return lowField.getValue(addr);          }
  public Address high()                         { return highField.getValue(addr);         }
  public Address lowBoundary()                  { return lowBoundaryField.getValue(addr);  }
  public Address highBoundary()                 { return highBoundaryField.getValue(addr); }

  /** Testers (all sizes are byte sizes) */
  public long committedSize()                   { return high().minus(low());                                    }
  public long reservedSize()                    { return highBoundary().minus(lowBoundary());                    }
  public long uncommittedSize()                 { return reservedSize() - committedSize();                       }
  public boolean contains(Address addr)         { return (low().lessThanOrEqual(addr) && addr.lessThan(high())); }
}
