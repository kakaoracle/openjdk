/*
 * Copyright (c) 2008, Oracle and/or its affiliates. All rights reserved.
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
package hotspot.src.share.tools.IdealGraphVisualizer.Filter.src.com.sun.hotspot.igv.filter;

import com.sun.hotspot.igv.graph.Connection;
import com.sun.hotspot.igv.graph.Diagram;
import com.sun.hotspot.igv.graph.Figure;
import com.sun.hotspot.igv.graph.InputSlot;
import com.sun.hotspot.igv.graph.OutputSlot;
import com.sun.hotspot.igv.data.Properties;
import com.sun.hotspot.igv.data.Properties.PropertyMatcher;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Thomas Wuerthinger
 */
public class CombineFilter extends AbstractFilter {

    private List<CombineRule> rules;
    private String name;

    public CombineFilter(String name) {
        this.name = name;
        rules = new ArrayList<CombineRule>();
    }

    public String getName() {
        return name;
    }

    public void apply(Diagram diagram) {

        Properties.PropertySelector<Figure> selector = new Properties.PropertySelector<Figure>(diagram.getFigures());
        for (CombineRule r : rules) {

            List<Figure> list = selector.selectMultiple(r.getFirstMatcher());
            Set<Figure> figuresToRemove = new HashSet<Figure>();
            for (Figure f : list) {

                List<Figure> successors = new ArrayList<Figure>(f.getSuccessors());
                if (r.isReversed()) {
                    if (successors.size() == 1) {
                        Figure succ = successors.get(0);
                        InputSlot slot = null;

                        for (InputSlot s : succ.getInputSlots()) {
                            for (Connection c : s.getConnections()) {
                                if (c.getOutputSlot().getFigure() == f) {
                                    slot = s;
                                }
                            }
                        }

                        assert slot != null;
                        slot.setName(f.getProperties().get("dump_spec"));
                        if (f.getProperties().get("short_name") != null) {
                            slot.setShortName(f.getProperties().get("short_name"));
                        } else {
                            String s = f.getProperties().get("dump_spec");
                            if (s != null && s.length() <= 5) {
                                slot.setShortName(s);
                            }

                        }

                        for (InputSlot s : f.getInputSlots()) {
                            for (Connection c : s.getConnections()) {
                                Connection newConn = diagram.createConnection(slot, c.getOutputSlot());
                                newConn.setColor(c.getColor());
                                newConn.setStyle(c.getStyle());
                            }
                        }

                        figuresToRemove.add(f);
                    }
                } else {

                    for (Figure succ : successors) {
                        if (succ.getPredecessors().size() == 1) {
                            if (succ.getProperties().selectSingle(r.getSecondMatcher()) != null && succ.getOutputSlots().size() == 1) {


                                OutputSlot oldSlot = null;
                                for (OutputSlot s : f.getOutputSlots()) {
                                    for (Connection c : s.getConnections()) {
                                        if (c.getInputSlot().getFigure() == succ) {
                                            oldSlot = s;
                                        }
                                    }
                                }

                                assert oldSlot != null;

                                OutputSlot nextSlot = succ.getOutputSlots().get(0);
                                int pos = 0;
                                if (succ.getProperties().get("con") != null) {
                                    pos = Integer.parseInt(succ.getProperties().get("con"));
                                }
                                OutputSlot slot = f.createOutputSlot(pos);
                                slot.setName(succ.getProperties().get("dump_spec"));
                                if (succ.getProperties().get("short_name") != null) {
                                    slot.setShortName(succ.getProperties().get("short_name"));
                                } else {
                                    String s = succ.getProperties().get("dump_spec");
                                    if (s != null && s.length() <= 2) {
                                        slot.setShortName(s);
                                    } else {
                                        String tmpName = succ.getProperties().get("name");
                                        if (tmpName != null && tmpName.length() > 0) {
                                            slot.setShortName(tmpName.substring(0, 1));
                                        }
                                    }
                                }
                                for (Connection c : nextSlot.getConnections()) {
                                    Connection newConn = diagram.createConnection(c.getInputSlot(), slot);
                                    newConn.setColor(c.getColor());
                                    newConn.setStyle(c.getStyle());
                                }


                                figuresToRemove.add(succ);

                                if (oldSlot.getConnections().size() == 0) {
                                    f.removeSlot(oldSlot);
                                }
                            }
                        }
                    }
                }
            }

            diagram.removeAllFigures(figuresToRemove);
        }
    }

    public void addRule(CombineRule combineRule) {
        rules.add(combineRule);
    }

    public static class CombineRule {

        private PropertyMatcher first;
        private PropertyMatcher second;
        private boolean reversed;

        public CombineRule(PropertyMatcher first, PropertyMatcher second) {
            this(first, second, false);

        }

        public CombineRule(PropertyMatcher first, PropertyMatcher second, boolean reversed) {
            this.first = first;
            this.second = second;
            this.reversed = reversed;
        }

        public boolean isReversed() {
            return reversed;
        }

        public PropertyMatcher getFirstMatcher() {
            return first;
        }

        public PropertyMatcher getSecondMatcher() {
            return second;
        }
    }
}
