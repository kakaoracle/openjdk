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
package hotspot.src.share.tools.IdealGraphVisualizer.View.src.com.sun.hotspot.igv.view.actions;

import com.sun.hotspot.igv.data.ChangedListener;
import com.sun.hotspot.igv.view.DiagramViewModel;
import com.sun.hotspot.igv.util.ContextAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Thomas Wuerthinger
 */
public final class PrevDiagramAction extends ContextAction<DiagramViewModel> implements ChangedListener<DiagramViewModel> {

    private DiagramViewModel model;

    public PrevDiagramAction() {
        this(Utilities.actionsGlobalContext());
    }

    public PrevDiagramAction(Lookup lookup) {
        putValue(Action.SHORT_DESCRIPTION, "Show previous graph of current group");
        putValue(Action.SMALL_ICON, new ImageIcon(Utilities.loadImage("com/sun/hotspot/igv/view/images/prev_diagram.png")));
    }

    public String getName() {
        return NbBundle.getMessage(PrevDiagramAction.class, "CTL_PrevDiagramAction");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public Class<DiagramViewModel> contextClass() {
        return DiagramViewModel.class;
    }

    @Override
    public void performAction(DiagramViewModel model) {
        int fp = model.getFirstPosition();
        int sp = model.getSecondPosition();
        if (fp != 0) {
            int nfp = fp - 1;
            int nsp = sp - 1;
            model.setPositions(nfp, nsp);
        }
    }

    @Override
    public void update(DiagramViewModel model) {
        super.update(model);

        if (this.model != model) {
            if (this.model != null) {
                this.model.getDiagramChangedEvent().removeListener(this);
            }

            this.model = model;
            if (this.model != null) {
                this.model.getDiagramChangedEvent().addListener(this);
            }
        }
    }

    @Override
    public boolean isEnabled(DiagramViewModel model) {
        return model.getFirstPosition() != 0;
    }

    public Action createContextAwareInstance(Lookup arg0) {
        return new PrevDiagramAction(arg0);
    }

    public void changed(DiagramViewModel source) {
        update(source);
    }
}
