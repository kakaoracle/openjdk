package hotspot.test.compiler/*
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

/**
 * @test
 * @bug 6956668
 * @summary misbehavior of XOR operator (^) with int
 *
 * @run main/othervm -Xbatch Test6956668
 */


public class Test6956668 {

   public static int bitTest() {
      int result = 0;

      int testValue = 73;
      int bitCount = Integer.bitCount(testValue);

      if (testValue != 0) {
         int gap = Long.numberOfTrailingZeros(testValue);
         testValue >>>= gap;

         while (testValue != 0) {
            result++;

            if ((testValue ^= 0x1) != 0) {
               gap = Long.numberOfTrailingZeros(testValue);
               testValue >>>= gap;
            }
         }
      }

      if (bitCount != result) {
         System.out.println("ERROR: " + bitCount + " != " + result);
         System.exit(97);
      }

      return (result);
   }

   public static void main(String[] args) {
      for (int i = 0; i < 100000; i++) {
         int ct = bitTest();
      }
   }
}
