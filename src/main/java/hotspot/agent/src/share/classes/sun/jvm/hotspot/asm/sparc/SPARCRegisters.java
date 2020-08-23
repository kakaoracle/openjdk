/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.asm.sparc;

import sun.jvm.hotspot.asm.sparc.SPARCRegister;
import sun.jvm.hotspot.utilities.*;

public class SPARCRegisters {

  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister G0;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister G1;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister G2;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister G3;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister G4;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister G5;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister G6;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister G7;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister O0;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister O1;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister O2;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister O3;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister O4;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister O5;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister O6;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister O7;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister L0;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister L1;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister L2;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister L3;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister L4;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister L5;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister L6;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister L7;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister I0;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister I1;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister I2;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister I3;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister I4;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister I5;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister I6;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister I7;

  private static String registerNames[];
  public static final int NUM_REGISTERS = 32;
  private static sun.jvm.hotspot.asm.sparc.SPARCRegister registers[];

  static {
     G0 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(0);
     G1 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(1);
     G2 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(2);
     G3 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(3);
     G4 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(4);
     G5 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(5);
     G6 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(6);
     G7 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(7);
     O0 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(8);
     O1 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(9);
     O2 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(10);
     O3 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(11);
     O4 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(12);
     O5 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(13);
     O6 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(14);
     O7 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(15);
     L0 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(16);
     L1 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(17);
     L2 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(18);
     L3 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(19);
     L4 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(20);
     L5 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(21);
     L6 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(22);
     L7 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(23);
     I0 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(24);
     I1 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(25);
     I2 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(26);
     I3 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(27);
     I4 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(28);
     I5 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(29);
     I6 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(30);
     I7 = new sun.jvm.hotspot.asm.sparc.SPARCRegister(31);
     registerNames = new String[NUM_REGISTERS];
     registerNames[G0.getNumber()] = "%g0";
     registerNames[G1.getNumber()] = "%g1";
     registerNames[G2.getNumber()] = "%g2";
     registerNames[G3.getNumber()] = "%g3";
     registerNames[G4.getNumber()] = "%g4";
     registerNames[G5.getNumber()] = "%g5";
     registerNames[G6.getNumber()] = "%g6";
     registerNames[G7.getNumber()] = "%g7";
     registerNames[O0.getNumber()] = "%o0";
     registerNames[O1.getNumber()] = "%o1";
     registerNames[O2.getNumber()] = "%o2";
     registerNames[O3.getNumber()] = "%o3";
     registerNames[O4.getNumber()] = "%o4";
     registerNames[O5.getNumber()] = "%o5";
     registerNames[O6.getNumber()] = "%sp";
     registerNames[O7.getNumber()] = "%o7";
     registerNames[I0.getNumber()] = "%i0";
     registerNames[I1.getNumber()] = "%i1";
     registerNames[I2.getNumber()] = "%i2";
     registerNames[I3.getNumber()] = "%i3";
     registerNames[I4.getNumber()] = "%i4";
     registerNames[I5.getNumber()] = "%i5";
     registerNames[I6.getNumber()] = "%fp";
     registerNames[I7.getNumber()] = "%i7";
     registerNames[L0.getNumber()] = "%l0";
     registerNames[L1.getNumber()] = "%l1";
     registerNames[L2.getNumber()] = "%l2";
     registerNames[L3.getNumber()] = "%l3";
     registerNames[L4.getNumber()] = "%l4";
     registerNames[L5.getNumber()] = "%l5";
     registerNames[L6.getNumber()] = "%l6";
     registerNames[L7.getNumber()] = "%l7";
     registers = (new sun.jvm.hotspot.asm.sparc.SPARCRegister[] {
            G0, G1, G2, G3, G4, G5, G6, G7, O0, O1,
            O2, O3, O4, O5, O6, O7, L0, L1, L2, L3,
            L4, L5, L6, L7, I0, I1, I2, I3, I4, I5,
            I6, I7
        });
  }

  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister FP = I6;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister SP = O6;

  // Interpreter frames

  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister Lesp        = L0; // expression stack pointer
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister Lbcp        = L1; // pointer to next bytecode
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister Lmethod     = L2;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister Llocals     = L3;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister Lmonitors   = L4;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister Lbyte_code  = L5;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister Lscratch    = L5;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister Lscratch2   = L6;
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister LcpoolCache = L6; // constant pool cache

  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister OparamAddr       = O0; // Callers Parameter area address
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister IsavedSP         = I5; // Saved SP before bumping for locals
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister IsizeCalleeParms = I4; // Saved size of Callee parms used to pop arguments
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister IdispatchAddress = I3; // Register which saves the dispatch address for each bytecode
  public static final sun.jvm.hotspot.asm.sparc.SPARCRegister IdispatchTables  = I2; // Base address of the bytecode dispatch tables


  /** Prefer to use this instead of the constant above */
  public static int getNumRegisters() {
    return NUM_REGISTERS;
  }


  public static String getRegisterName(int regNum) {
    if (regNum < 0 || regNum >= NUM_REGISTERS) {
      return "[Illegal register " + regNum + "]";
    }

    if (Assert.ASSERTS_ENABLED) {
      Assert.that(regNum > -1 && regNum < NUM_REGISTERS, "invalid integer register number!");
    }

    return registerNames[regNum];
  }

  public static SPARCRegister getRegister(int regNum) {
    if (Assert.ASSERTS_ENABLED) {
      Assert.that(regNum > -1 && regNum < NUM_REGISTERS, "invalid integer register number!");
    }

    return registers[regNum];
  }
}
