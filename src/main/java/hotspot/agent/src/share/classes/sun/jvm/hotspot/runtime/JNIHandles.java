/*
 * Copyright (c) 2000, Oracle and/or its affiliates. All rights reserved.
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
import sun.jvm.hotspot.runtime.JNIHandleBlock;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.types.*;

public class JNIHandles {
  private static AddressField      globalHandlesField;
  private static AddressField      weakGlobalHandlesField;
  private static OopField          deletedHandleField;

  static {
    sun.jvm.hotspot.runtime.VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) {
    Type type = db.lookupType("JNIHandles");

    globalHandlesField = type.getAddressField("_global_handles");
    weakGlobalHandlesField = type.getAddressField("_weak_global_handles");
    deletedHandleField = type.getOopField("_deleted_handle");

  }

  public JNIHandles() {
  }

  public JNIHandleBlock globalHandles() {
    Address handleAddr  = globalHandlesField.getValue();
    if (handleAddr == null) {
      return null;
    }
    return new JNIHandleBlock(handleAddr);
  }

  public JNIHandleBlock weakGlobalHandles() {
    Address handleAddr  = weakGlobalHandlesField.getValue();
    if (handleAddr == null) {
      return null;
    }
    return new JNIHandleBlock(handleAddr);
  }

  public OopHandle deletedHandle() {
    return deletedHandleField.getValue();
  }

  public boolean isDeletedHandle(OopHandle handle) {
    return (handle != null && handle.equals(deletedHandle()));
  }

}
