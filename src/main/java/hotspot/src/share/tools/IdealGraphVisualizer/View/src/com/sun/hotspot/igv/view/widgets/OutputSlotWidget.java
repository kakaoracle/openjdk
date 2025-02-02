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
package hotspot.src.share.tools.IdealGraphVisualizer.View.src.com.sun.hotspot.igv.view.widgets;

import com.sun.hotspot.igv.graph.OutputSlot;
import com.sun.hotspot.igv.view.DiagramScene;
import java.awt.Point;
import java.util.List;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Thomas Wuerthinger
 */
public class OutputSlotWidget extends SlotWidget {

    private OutputSlot outputSlot;

    public OutputSlotWidget(OutputSlot slot, DiagramScene scene, Widget parent, FigureWidget fw) {
        super(slot, scene, parent, fw);
        outputSlot = slot;
        init();
        getFigureWidget().getRightWidget().addChild(this);
    }

    public OutputSlot getOutputSlot() {
        return outputSlot;
    }

    protected Point calculateRelativeLocation() {
        if (getFigureWidget().getBounds() == null) {
            return new Point(0, 0);
        }

        double x = this.getFigureWidget().getBounds().width;
        List<OutputSlot> slots = outputSlot.getFigure().getOutputSlots();
        assert slots.contains(outputSlot);
        return new Point((int) x, (int) (calculateRelativeY(slots.size(), slots.indexOf(outputSlot))));
    }
}
