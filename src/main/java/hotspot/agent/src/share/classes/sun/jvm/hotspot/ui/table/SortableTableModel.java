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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.ui.table;

import sun.jvm.hotspot.ui.table.TableModelComparator;

import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 * A table model which stores its rows as a list. The elements
 * of the list may be sortable by column. The TableModelComparator
 * must be set for sorting to be enabled.
 */
public abstract class SortableTableModel extends AbstractTableModel {

    private TableModelComparator comparator;

    /**
     * All the rows are stored as a List.
     */
    protected List elements;

    /**
     * This comparator must be set.
     */
    public void setComparator(TableModelComparator comparator) {
        this.comparator = comparator;
    }

    public void sortByColumn(int column, boolean ascending) {
        comparator.addColumn(column);
        comparator.setAscending(ascending);

        Collections.sort(elements, comparator);

        fireTableChanged(new TableModelEvent(this));
    }

    public boolean isAscending() {
        return comparator.isAscending();
    }

    public int getColumn() {
        return comparator.getColumn();
    }

}
