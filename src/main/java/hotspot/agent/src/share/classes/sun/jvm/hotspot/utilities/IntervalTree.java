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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.utilities;

/** Derived from the example in Section 15.3 of CLR. */

import sun.jvm.hotspot.utilities.Interval;
import sun.jvm.hotspot.utilities.IntervalNode;
import sun.jvm.hotspot.utilities.RBColor;
import sun.jvm.hotspot.utilities.RBNode;
import sun.jvm.hotspot.utilities.RBTree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IntervalTree extends RBTree {
  private Comparator endpointComparator;

  /** This constructor takes only one comparator: one which operates
      upon the endpoints of the Intervals this tree will store. It
      constructs an internal "interval comparator" out of this one. */
  public IntervalTree(Comparator endpointComparator) {
    super(new IntervalComparator(endpointComparator));
    this.endpointComparator = endpointComparator;
  }

  public void insert(sun.jvm.hotspot.utilities.Interval interval, Object data) {
    sun.jvm.hotspot.utilities.IntervalNode node = new sun.jvm.hotspot.utilities.IntervalNode(interval, endpointComparator, data);
    insertNode(node);
  }

  /** Returns a List&lt;IntervalNode&gt; indicating which nodes'
      intervals were intersected by the given query interval. It is
      guaranteed that these nodes will be returned sorted by
      increasing low endpoint. */
  public List findAllNodesIntersecting(sun.jvm.hotspot.utilities.Interval interval) {
    List retList = new ArrayList();
    searchForIntersectingNodesFrom((sun.jvm.hotspot.utilities.IntervalNode) getRoot(), interval, retList);
    return retList;
  }

  public void print() {
    printOn(System.out);
  }

  public void printOn(PrintStream tty) {
    printFromNode(getRoot(), tty, 0);
  }

  //----------------------------------------------------------------------
  // Overridden internal functionality

  protected Object getNodeValue(sun.jvm.hotspot.utilities.RBNode node) {
    return ((sun.jvm.hotspot.utilities.IntervalNode) node).getInterval();
  }

  protected void verify() {
    super.verify();
    verifyFromNode(getRoot());
  }

  //----------------------------------------------------------------------
  // Internals only below this point
  //

  private void verifyFromNode(sun.jvm.hotspot.utilities.RBNode node) {
    if (node == null) {
      return;
    }

    // We already know that the red/black structure has been verified.
    // What we need to find out is whether this node has been updated
    // properly -- i.e., whether its notion of the maximum endpoint is
    // correct.
    sun.jvm.hotspot.utilities.IntervalNode intNode = (sun.jvm.hotspot.utilities.IntervalNode) node;
    if (!intNode.getMaxEndpoint().equals(intNode.computeMaxEndpoint())) {
      if (DEBUGGING && VERBOSE) {
        print();
      }
      throw new RuntimeException("Node's max endpoint was not updated properly");
    }
    if (!intNode.getMinEndpoint().equals(intNode.computeMinEndpoint())) {
      if (DEBUGGING && VERBOSE) {
        print();
      }
      throw new RuntimeException("Node's min endpoint was not updated properly");
    }

    verifyFromNode(node.getLeft());
    verifyFromNode(node.getRight());
  }

  static class IntervalComparator implements Comparator {
    private Comparator endpointComparator;

    public IntervalComparator(Comparator endpointComparator) {
      this.endpointComparator = endpointComparator;
    }

    public int compare(Object o1, Object o2) {
      sun.jvm.hotspot.utilities.Interval i1 = (sun.jvm.hotspot.utilities.Interval) o1;
      sun.jvm.hotspot.utilities.Interval i2 = (sun.jvm.hotspot.utilities.Interval) o2;
      return endpointComparator.compare(i1.getLowEndpoint(), i2.getLowEndpoint());
    }
  }

  private void searchForIntersectingNodesFrom(sun.jvm.hotspot.utilities.IntervalNode node,
                                              Interval interval,
                                              List resultList) {
    if (node == null) {
      return;
    }

    // Inorder traversal (very important to guarantee sorted order)

    // Check to see whether we have to traverse the left subtree
    sun.jvm.hotspot.utilities.IntervalNode left = (sun.jvm.hotspot.utilities.IntervalNode) node.getLeft();
    if ((left != null) &&
        (endpointComparator.compare(left.getMaxEndpoint(),
                                    interval.getLowEndpoint()) > 0)) {
      searchForIntersectingNodesFrom(left, interval, resultList);
    }

    // Check for intersection with current node
    if (node.getInterval().overlaps(interval, endpointComparator)) {
      resultList.add(node);
    }

    // Check to see whether we have to traverse the left subtree
    sun.jvm.hotspot.utilities.IntervalNode right = (sun.jvm.hotspot.utilities.IntervalNode) node.getRight();
    if ((right != null) &&
        (endpointComparator.compare(interval.getHighEndpoint(),
                                    right.getMinEndpoint()) > 0)) {
      searchForIntersectingNodesFrom(right, interval, resultList);
    }
  }

  /** Debugging */
  private void printFromNode(RBNode node, PrintStream tty, int indentDepth) {
    for (int i = 0; i < indentDepth; i++) {
      tty.print(" ");
    }

    tty.print("-");
    if (node == null) {
      tty.println();
      return;
    }

    tty.println(" " + node +
                " (min " + ((sun.jvm.hotspot.utilities.IntervalNode) node).getMinEndpoint() +
                ", max " + ((IntervalNode) node).getMaxEndpoint() + ")" +
                ((node.getColor() == RBColor.RED) ? " (red)" : " (black)"));
    if (node.getLeft()  != null) printFromNode(node.getLeft(),  tty, indentDepth + 2);
    if (node.getRight() != null) printFromNode(node.getRight(), tty, indentDepth + 2);
  }
}
