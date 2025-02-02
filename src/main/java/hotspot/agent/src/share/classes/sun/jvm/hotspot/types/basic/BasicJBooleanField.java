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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.types.basic;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.types.*;
import sun.jvm.hotspot.types.basic.BasicField;
import sun.jvm.hotspot.types.basic.BasicTypeDataBase;

/** A specialization of Field which represents a field containing a
    Java boolean value (in either a C/C++ data structure or a Java
    object) and which adds typechecked getValue() routines returning
    booleans. */

public class BasicJBooleanField extends BasicField implements JBooleanField {
  public BasicJBooleanField(BasicTypeDataBase db, Type containingType, String name, Type type,
                            boolean isStatic, long offset, Address staticFieldAddress) {
    super(db, containingType, name, type, isStatic, offset, staticFieldAddress);

    if (!type.equals(db.getJBooleanType())) {
      throw new WrongTypeException("Type of a BasicJBooleanField must be db.getJBooleanType()");
    }
  }

  /** The field must be nonstatic and the type of the field must be a
      Java boolean, or a WrongTypeException will be thrown. */
  public boolean getValue(Address addr) throws UnmappedAddressException, UnalignedAddressException, WrongTypeException {
    return getJBoolean(addr);
  }

  /** The field must be static and the type of the field must be a
      Java boolean, or a WrongTypeException will be thrown. */
  public boolean getValue() throws UnmappedAddressException, UnalignedAddressException, WrongTypeException {
    return getJBoolean();
  }
}
