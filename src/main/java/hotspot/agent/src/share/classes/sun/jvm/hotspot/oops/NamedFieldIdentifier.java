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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.oops;

import sun.jvm.hotspot.oops.FieldIdentifier;

import java.io.*;

// A NamedFieldIdentifier describes a field in an Oop with a name
public class NamedFieldIdentifier extends FieldIdentifier {

  public NamedFieldIdentifier(String name) {
    this.name = name;
  }

  private String name;

  public String getName() { return name; }

  public void printOn(PrintStream tty) {
    tty.print(" - " + getName() + ":\t");
  }

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof NamedFieldIdentifier)) {
      return false;
    }

    return ((NamedFieldIdentifier) obj).getName().equals(name);
  }

  public int hashCode() {
    return name.hashCode();
  }
};
