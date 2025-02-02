/*
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.debugger.cdbg;

import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.debugger.cdbg.LoadObject;

// a comparator used to sort LoadObjects by base address

public class LoadObjectComparator implements Comparator {
   public int compare(Object o1, Object o2) {
      sun.jvm.hotspot.debugger.cdbg.LoadObject lo1 = (sun.jvm.hotspot.debugger.cdbg.LoadObject) o1;
      sun.jvm.hotspot.debugger.cdbg.LoadObject lo2 = (LoadObject) o2;
      Address base1 = lo1.getBase();
      Address base2 = lo2.getBase();
      long diff = base1.minus(base2);
      return (diff == 0)? 0 : ((diff < 0)? -1 : +1);
   }

   public boolean equals(Object o) {
      if (o == null || !(o instanceof LoadObjectComparator)) {
         return false;
      }
      return true;
   }
}
