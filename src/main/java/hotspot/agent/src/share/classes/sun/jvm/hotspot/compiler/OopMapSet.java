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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.compiler;

import java.util.*;

import sun.jvm.hotspot.code.*;
import sun.jvm.hotspot.compiler.OopMapStream;
import sun.jvm.hotspot.compiler.OopMapValue;
import sun.jvm.hotspot.compiler.OopMapVisitor;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.*;
import sun.jvm.hotspot.utilities.*;

public class OopMapSet extends VMObject {
  private static final boolean DEBUG = System.getProperty("sun.jvm.hotspot.compiler.OopMapSet.DEBUG") != null;

  private static CIntegerField omCountField;
  private static CIntegerField omSizeField;
  private static AddressField  omDataField;
  private static int REG_COUNT;
  private static int SAVED_ON_ENTRY_REG_COUNT;
  private static int C_SAVED_ON_ENTRY_REG_COUNT;
  private static class MyVisitor implements OopMapVisitor {
    private AddressVisitor addressVisitor;

    public MyVisitor(AddressVisitor oopVisitor) {
      setAddressVisitor(oopVisitor);
    }

    public void setAddressVisitor(AddressVisitor addressVisitor) {
      this.addressVisitor = addressVisitor;
    }

    public void visitOopLocation(Address oopAddr) {
      addressVisitor.visitAddress(oopAddr);
    }

    public void visitDerivedOopLocation(Address baseOopAddr, Address derivedOopAddr) {
      if (VM.getVM().isClientCompiler()) {
        Assert.that(false, "should not reach here");
      } else if (VM.getVM().isServerCompiler() &&
                 VM.getVM().useDerivedPointerTable()) {
        Assert.that(false, "FIXME: add derived pointer table");
      }
    }

    public void visitValueLocation(Address valueAddr) {
    }

    public void visitNarrowOopLocation(Address narrowOopAddr) {
      addressVisitor.visitCompOopAddress(narrowOopAddr);
    }
  }

  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static void initialize(TypeDataBase db) {
    Type type = db.lookupType("OopMapSet");

    omCountField  = type.getCIntegerField("_om_count");
    omSizeField   = type.getCIntegerField("_om_size");
    omDataField   = type.getAddressField("_om_data");

    if (!VM.getVM().isCore()) {
      REG_COUNT = db.lookupIntConstant("REG_COUNT").intValue();
      if (VM.getVM().isServerCompiler()) {
        SAVED_ON_ENTRY_REG_COUNT = (int) db.lookupIntConstant("SAVED_ON_ENTRY_REG_COUNT").intValue();
        C_SAVED_ON_ENTRY_REG_COUNT = (int) db.lookupIntConstant("C_SAVED_ON_ENTRY_REG_COUNT").intValue();
      }
    }
  }

  public OopMapSet(Address addr) {
    super(addr);
  }

  /** Returns the number of OopMaps in this OopMapSet */
  public long getSize() {
    return omCountField.getValue(addr);
  }

  /** returns the OopMap at a given index */
  public OopMap getMapAt(int index) {
    if (Assert.ASSERTS_ENABLED) {
      Assert.that((index >= 0) && (index <= getSize()),"bad index");
    }
    Address omDataAddr = omDataField.getValue(addr);
    Address oopMapAddr = omDataAddr.getAddressAt(index * VM.getVM().getAddressSize());
    if (oopMapAddr == null) {
      return null;
    }
    return new OopMap(oopMapAddr);
  }

  public OopMap findMapAtOffset(long pcOffset, boolean debugging) {
    int i;
    int len = (int) getSize();
    if (Assert.ASSERTS_ENABLED) {
      Assert.that(len > 0, "must have pointer maps");
    }

    // Scan through oopmaps. Stop when current offset is either equal or greater
    // than the one we are looking for.
    for (i = 0; i < len; i++) {
      if (getMapAt(i).getOffset() >= pcOffset) {
        break;
      }
    }

    if (!debugging) {
      if (Assert.ASSERTS_ENABLED) {
        Assert.that(i < len, "oopmap not found for pcOffset = " + pcOffset + "; len = " + len);
        Assert.that(getMapAt(i).getOffset() == pcOffset, "oopmap not found");
      }
    } else {
      if (i == len) {
        if (DEBUG) {
          System.out.println("can't find oopmap at " + pcOffset);
          System.out.print("Oopmap offsets are [ ");
          for (i = 0; i < len; i++) {
            System.out.print(getMapAt(i).getOffset());
          }
          System.out.println("]");
        }
        i = len - 1;
        return getMapAt(i);
      }
    }

    OopMap m = getMapAt(i);
    return m;
  }

  /** Visitation -- iterates through the frame for a compiled method.
      This is a very generic mechanism that requires the Address to be
      dereferenced by the callee. Other, more specialized, visitation
      mechanisms are given below. */
  public static void oopsDo(Frame fr, CodeBlob cb, RegisterMap regMap, AddressVisitor oopVisitor, boolean debugging) {
    allDo(fr, cb, regMap, new MyVisitor(oopVisitor), debugging);
  }

  /** Note that there are 4 required AddressVisitors: one for oops,
      one for derived oops, one for values, and one for dead values */
  public static void allDo(Frame fr, CodeBlob cb, RegisterMap regMap, OopMapVisitor visitor, boolean debugging) {
    if (Assert.ASSERTS_ENABLED) {
      CodeBlob tmpCB = VM.getVM().getCodeCache().findBlob(fr.getPC());
      Assert.that(tmpCB != null && cb.equals(tmpCB), "wrong codeblob passed in");
    }

    OopMapSet maps = cb.getOopMaps();
    OopMap map = cb.getOopMapForReturnAddress(fr.getPC(), debugging);
    if (Assert.ASSERTS_ENABLED) {
      Assert.that(map != null, "no ptr map found");
    }

    // handle derived pointers first (otherwise base pointer may be
    // changed before derived pointer offset has been collected)
    OopMapValue omv;
    {
      for (OopMapStream oms = new OopMapStream(map, OopMapValue.OopTypes.DERIVED_OOP_VALUE); !oms.isDone(); oms.next()) {
        if (VM.getVM().isClientCompiler()) {
          Assert.that(false, "should not reach here");
        }
        omv = oms.getCurrent();
        Address loc = fr.oopMapRegToLocation(omv.getReg(), regMap);
        if (loc != null) {
          Address baseLoc    = fr.oopMapRegToLocation(omv.getContentReg(), regMap);
          Address derivedLoc = loc;
          visitor.visitDerivedOopLocation(baseLoc, derivedLoc);
        }
      }
    }

    // We want narow oop, value and oop oop_types
    OopMapValue.OopTypes[] values = new OopMapValue.OopTypes[] {
      OopMapValue.OopTypes.OOP_VALUE, OopMapValue.OopTypes.VALUE_VALUE, OopMapValue.OopTypes.NARROWOOP_VALUE
    };

    {
      for (OopMapStream oms = new OopMapStream(map, values); !oms.isDone(); oms.next()) {
        omv = oms.getCurrent();
        Address loc = fr.oopMapRegToLocation(omv.getReg(), regMap);
        if (loc != null) {
          if (omv.getType() == OopMapValue.OopTypes.OOP_VALUE) {
            // This assert commented out because this will be useful
            // to detect in the debugging system
            // assert(Universe::is_heap_or_null(*loc), "found non oop pointer");
            visitor.visitOopLocation(loc);
          } else if (omv.getType() == OopMapValue.OopTypes.VALUE_VALUE) {
            visitor.visitValueLocation(loc);
          } else if (omv.getType() == OopMapValue.OopTypes.NARROWOOP_VALUE) {
            visitor.visitNarrowOopLocation(loc);
          }
        }
      }
    }
  }

  /** Update callee-saved register info for the following frame.
      Should only be called in non-core builds. */
  public static void updateRegisterMap(Frame fr, CodeBlob cb, RegisterMap regMap, boolean debugging) {
    if (Assert.ASSERTS_ENABLED) {
      Assert.that(!VM.getVM().isCore(), "non-core builds only");
    }

    if (!VM.getVM().isDebugging()) {
      if (Assert.ASSERTS_ENABLED) {
        OopMapSet maps = cb.getOopMaps();
        Assert.that((maps != null) && (maps.getSize() > 0), "found null or empty OopMapSet for CodeBlob");
      }
    } else {
      // Hack for some topmost frames that have been found with empty
      // OopMapSets. (Actually have not seen the null case, but don't
      // want to take any chances.) See HSDB.showThreadStackMemory().
      OopMapSet maps = cb.getOopMaps();
      if ((maps == null) || (maps.getSize() == 0)) {
        return;
      }
    }

    // Check if caller must update oop argument
    regMap.setIncludeArgumentOops(cb.callerMustGCArguments());

    int nofCallee = 0;
    Address[] locs = new Address[2 * REG_COUNT + 1];
    VMReg  [] regs = new VMReg  [2 * REG_COUNT + 1];
    // ("+1" because REG_COUNT might be zero)

    // Scan through oopmap and find location of all callee-saved registers
    // (we do not do update in place, since info could be overwritten)
    OopMap map  = cb.getOopMapForReturnAddress(fr.getPC(), debugging);
    if (Assert.ASSERTS_ENABLED) {
      Assert.that(map != null, "no ptr map found");
    }

    OopMapValue omv = null;
    for(OopMapStream oms = new OopMapStream(map, OopMapValue.OopTypes.CALLEE_SAVED_VALUE); !oms.isDone(); oms.next()) {
      omv = oms.getCurrent();
      if (Assert.ASSERTS_ENABLED) {
        Assert.that(nofCallee < 2 * REG_COUNT, "overflow");
      }
      regs[nofCallee] = omv.getContentReg();
      locs[nofCallee] = fr.oopMapRegToLocation(omv.getReg(), regMap);
      nofCallee++;
    }

    // Check that runtime stubs save all callee-saved registers
    // After adapter frames were deleted C2 doesn't use callee save registers at present
    if (Assert.ASSERTS_ENABLED) {
      if (VM.getVM().isServerCompiler()) {
        Assert.that(!cb.isRuntimeStub() ||
                    (nofCallee >= SAVED_ON_ENTRY_REG_COUNT || nofCallee >= C_SAVED_ON_ENTRY_REG_COUNT),
                    "must save all");
      }
    }

    // Copy found callee-saved register to reg_map
    for (int i = 0; i < nofCallee; i++) {
      regMap.setLocation(regs[i], locs[i]);
    }
  }
}
