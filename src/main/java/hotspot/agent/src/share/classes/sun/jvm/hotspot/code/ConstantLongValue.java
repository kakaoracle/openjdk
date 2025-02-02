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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.code;

import sun.jvm.hotspot.code.DebugInfoReadStream;
import sun.jvm.hotspot.code.ScopeValue;

import java.io.*;

/** A ConstantLongValue describes a constant long; i.e., the
    corresponding logical entity is either a source constant or its
    computation has been constant-folded. */

public class ConstantLongValue extends ScopeValue {
  private long value;

  public ConstantLongValue(long value) {
    this.value = value;
  }

  public boolean isConstantLong() {
    return true;
  }

  public long getValue() {
    return value;
  }

  /** Serialization of debugging information */
  ConstantLongValue(DebugInfoReadStream stream) {
    value = stream.readLong();
  }

  // FIXME: not yet implementable
  // void write_on(DebugInfoWriteStream* stream);

  // Printing

  public void print() {
    printOn(System.out);
  }

  public void printOn(PrintStream tty) {
    tty.print(value);
  }
}
