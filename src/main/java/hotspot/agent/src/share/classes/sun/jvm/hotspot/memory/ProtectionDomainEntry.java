/*
 * Copyright (c) 2001, 2013, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.memory;

import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.memory.ProtectionDomainCacheEntry;
import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.*;

public class ProtectionDomainEntry extends VMObject {
  private static AddressField nextField;
  private static AddressField pdCacheField;

  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) {
    Type type = db.lookupType("ProtectionDomainEntry");

    nextField = type.getAddressField("_next");
    pdCacheField = type.getAddressField("_pd_cache");
  }

  public ProtectionDomainEntry(Address addr) {
    super(addr);
  }

  public ProtectionDomainEntry next() {
    return (ProtectionDomainEntry) VMObjectFactory.newObject(ProtectionDomainEntry.class, nextField.getValue(addr));
  }

  public Oop protectionDomain() {
    ProtectionDomainCacheEntry pd_cache = (ProtectionDomainCacheEntry)
      VMObjectFactory.newObject(ProtectionDomainCacheEntry.class, pdCacheField.getValue(addr));
    return pd_cache.protectionDomain();
  }
}
