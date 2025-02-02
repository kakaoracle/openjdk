/*
 * Copyright (c) 2000, 2008, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.oops;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.oops.OopField;

// The class for an oop field simply provides access to the value.
public class NarrowOopField extends OopField {
  public NarrowOopField(FieldIdentifier id, long offset, boolean isVMField) {
    super(id, offset, isVMField);
  }

  public NarrowOopField(sun.jvm.hotspot.types.OopField vmField, long startOffset) {
    super(new NamedFieldIdentifier(vmField.getName()), vmField.getOffset() + startOffset, true);
  }

  public NarrowOopField(InstanceKlass holder, int fieldArrayIndex) {
    super(holder, fieldArrayIndex);
  }

  public sun.jvm.hotspot.oops.Oop getValue(sun.jvm.hotspot.oops.Oop obj) {
    return obj.getHeap().newOop(getValueAsOopHandle(obj));
  }

  /** Debugging support */
  public OopHandle getValueAsOopHandle(sun.jvm.hotspot.oops.Oop obj) {
    return obj.getHandle().getCompOopHandleAt(getOffset());
  }

  public void setValue(Oop obj) throws MutationException {
    // Fix this: setOopAt is missing in Address
  }
}
