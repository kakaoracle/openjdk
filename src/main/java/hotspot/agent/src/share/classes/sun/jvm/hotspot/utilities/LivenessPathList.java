/*
 * Copyright (c) 2001, 2006, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.utilities;

import sun.jvm.hotspot.utilities.LivenessPath;

import java.util.*;

/** Simple container class for LivenessPaths */

public class LivenessPathList {
  public LivenessPathList() {
    list = new ArrayList();
  }

  public int size() {
    return list.size();
  }

  public sun.jvm.hotspot.utilities.LivenessPath get(int i) {
    return (sun.jvm.hotspot.utilities.LivenessPath) list.get(i);
  }

  void add(sun.jvm.hotspot.utilities.LivenessPath path) {
    list.add(path);
  }

  void remove(LivenessPath path) {
    list.remove(path);
  }

  private ArrayList list;
}
