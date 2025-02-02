/*
 * Copyright (c) 2002, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.ui.tree;

import java.util.*;
import java.io.*;
import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.ui.tree.FieldTreeNodeAdapter;
import sun.jvm.hotspot.ui.tree.OopTreeNodeAdapter;
import sun.jvm.hotspot.ui.tree.RootTreeNodeAdapter;
import sun.jvm.hotspot.ui.tree.SimpleTreeNode;
import sun.jvm.hotspot.utilities.*;

/** Who directly points to this node. */

public class RevPtrsTreeNodeAdapter extends sun.jvm.hotspot.ui.tree.FieldTreeNodeAdapter {
  private static FieldIdentifier fid = new NamedFieldIdentifier("_revPtrs");

  private List children;

  public RevPtrsTreeNodeAdapter(Oop oop) {
    this(oop, false);
  }

  public RevPtrsTreeNodeAdapter(Oop oop, boolean treeTableMode) {
    super(fid, treeTableMode);
    children = VM.getVM().getRevPtrs().get(oop);
  }

  public int getChildCount() {
    return children != null ? children.size() : 0;
  }

  public sun.jvm.hotspot.ui.tree.SimpleTreeNode getChild(int index) {
    LivenessPathElement lpe = (LivenessPathElement)children.get(index);
    IndexableFieldIdentifier ifid = new IndexableFieldIdentifier(index);
    Oop oop = lpe.getObj();
    if (oop != null) {
      return new OopTreeNodeAdapter(oop, ifid, getTreeTableMode());
    } else {
      NamedFieldIdentifier nfi = (NamedFieldIdentifier)lpe.getField();
      return new RootTreeNodeAdapter(nfi.getName(), ifid, getTreeTableMode());
    }
  }

  public boolean isLeaf() {
    return false;
  }

  public int getIndexOfChild(SimpleTreeNode child) {
    FieldIdentifier id = ((FieldTreeNodeAdapter) child).getID();
    IndexableFieldIdentifier ifid = (IndexableFieldIdentifier)id;
    return ifid.getIndex();
  }

  public String getName()  { return "<<Reverse pointers>>"; }
  public String getValue() { return ""; }
}
