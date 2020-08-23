/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.io.*;
import java.util.*;

import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.oops.CIntField;
import sun.jvm.hotspot.oops.Klass;
import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.oops.OopField;
import sun.jvm.hotspot.oops.Symbol;
import sun.jvm.hotspot.utilities.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.memory.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.*;

// ArrayKlass is the abstract class for all array classes

public class ArrayKlass extends sun.jvm.hotspot.oops.Klass {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type          = db.lookupType("ArrayKlass");
    dimension          = new sun.jvm.hotspot.oops.CIntField(type.getCIntegerField("_dimension"), 0);
    higherDimension    = new MetadataField(type.getAddressField("_higher_dimension"), 0);
    lowerDimension     = new MetadataField(type.getAddressField("_lower_dimension"), 0);
    vtableLen          = new sun.jvm.hotspot.oops.CIntField(type.getCIntegerField("_vtable_len"), 0);
    componentMirror    = new sun.jvm.hotspot.oops.OopField(type.getOopField("_component_mirror"), 0);
    javaLangCloneableName = null;
    javaLangObjectName = null;
    javaIoSerializableName = null;
  }

  public ArrayKlass(Address addr) {
    super(addr);
  }

  private static sun.jvm.hotspot.oops.CIntField dimension;
  private static MetadataField  higherDimension;
  private static MetadataField  lowerDimension;
  private static CIntField vtableLen;
  private static OopField componentMirror;

  public sun.jvm.hotspot.oops.Klass getJavaSuper() {
    SystemDictionary sysDict = VM.getVM().getSystemDictionary();
    return sysDict.getObjectKlass();
  }

  public long  getDimension()       { return         dimension.getValue(this); }
  public sun.jvm.hotspot.oops.Klass getHigherDimension() { return (sun.jvm.hotspot.oops.Klass) higherDimension.getValue(this); }
  public sun.jvm.hotspot.oops.Klass getLowerDimension()  { return (sun.jvm.hotspot.oops.Klass) lowerDimension.getValue(this); }
  public long  getVtableLen()       { return         vtableLen.getValue(this); }
  public Oop getComponentMirror() { return         componentMirror.getValue(this); }

  // constant class names - javaLangCloneable, javaIoSerializable, javaLangObject
  // Initialized lazily to avoid initialization ordering dependencies between ArrayKlass and SymbolTable
  private static sun.jvm.hotspot.oops.Symbol javaLangCloneableName;
  private static sun.jvm.hotspot.oops.Symbol javaLangObjectName;
  private static sun.jvm.hotspot.oops.Symbol javaIoSerializableName;
  private static sun.jvm.hotspot.oops.Symbol javaLangCloneableName() {
    if (javaLangCloneableName == null) {
      javaLangCloneableName = VM.getVM().getSymbolTable().probe("java/lang/Cloneable");
    }
    return javaLangCloneableName;
  }

  private static sun.jvm.hotspot.oops.Symbol javaLangObjectName() {
    if (javaLangObjectName == null) {
      javaLangObjectName = VM.getVM().getSymbolTable().probe("java/lang/Object");
    }
    return javaLangObjectName;
  }

  private static sun.jvm.hotspot.oops.Symbol javaIoSerializableName() {
    if (javaIoSerializableName == null) {
      javaIoSerializableName = VM.getVM().getSymbolTable().probe("java/io/Serializable");
    }
    return javaIoSerializableName;
  }

  public int getClassStatus() {
     return JVMDIClassStatus.VERIFIED | JVMDIClassStatus.PREPARED | JVMDIClassStatus.INITIALIZED;
  }

  public long computeModifierFlags() {
     return JVM_ACC_ABSTRACT | JVM_ACC_FINAL | JVM_ACC_PUBLIC;
  }

  public long getArrayHeaderInBytes() {
    return Bits.maskBits(getLayoutHelper() >> LH_HEADER_SIZE_SHIFT, 0xFF);
  }

  public int getLog2ElementSize() {
    return Bits.maskBits(getLayoutHelper() >> LH_LOG2_ELEMENT_SIZE_SHIFT, 0xFF);
  }

  public int getElementType() {
    return Bits.maskBits(getLayoutHelper() >> LH_ELEMENT_TYPE_SHIFT, 0xFF);
  }

  boolean computeSubtypeOf(Klass k) {
    // An array is a subtype of Serializable, Clonable, and Object
    Symbol name = k.getName();
    if (name != null && (name.equals(javaIoSerializableName()) ||
                         name.equals(javaLangCloneableName()) ||
                         name.equals(javaLangObjectName()))) {
      return true;
    } else {
      return false;
    }
  }

  public void printValueOn(PrintStream tty) {
    tty.print("ArrayKlass");
  }

  public void iterateFields(MetadataVisitor visitor) {
    super.iterateFields(visitor);
      visitor.doCInt(dimension, true);
    visitor.doMetadata(higherDimension, true);
    visitor.doMetadata(lowerDimension, true);
      visitor.doCInt(vtableLen, true);
      visitor.doOop(componentMirror, true);
    }
  }
