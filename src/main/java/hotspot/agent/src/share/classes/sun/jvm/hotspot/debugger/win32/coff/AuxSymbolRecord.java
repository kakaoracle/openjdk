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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.debugger.win32.coff;

/** Describes an Auxiliary Symbol Record. Such records may follow a
    Symbol Table record. (Some of the descriptions are taken directly
    from Microsoft's documentation and are copyrighted by Microsoft.)  */

public interface AuxSymbolRecord {
  public static final int FUNCTION_DEFINITION   = 0;
  public static final int BF_EF_RECORD          = 1;
  public static final int WEAK_EXTERNAL         = 2;
  public static final int FILE                  = 3;
  public static final int SECTION_DEFINITION    = 4;

  /** Returns {@link #FUNCTION_DEFINITION}, {@link #BF_EF_RECORD},
      {@link #WEAK_EXTERNAL}, {@link #FILE}, or {@link
      #SECTION_DEFINITION}, indicating the concrete subtype of this
      interface. */
  public int getType();
}
