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
package hotspot.src.share.tools.IdealGraphVisualizer.Bytecodes.src.com.sun.hotspot.igv.bytecodes;

import com.sun.hotspot.igv.data.InputBytecode;
import com.sun.hotspot.igv.data.InputGraph;
import com.sun.hotspot.igv.data.InputNode;
import com.sun.hotspot.igv.data.Properties;
import com.sun.hotspot.igv.data.Properties.StringPropertyMatcher;
import java.awt.Image;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 *
 * @author Thomas Wuerthinger
 */
public class BytecodeNode extends AbstractNode {

    private Set<InputNode> nodes;

    public BytecodeNode(InputBytecode bytecode, InputGraph graph, String bciValue) {

        super(Children.LEAF);
        this.setDisplayName(bytecode.getBci() + " " + bytecode.getName());

        bciValue = bytecode.getBci() + " " + bciValue;
        bciValue = bciValue.trim();

        Properties.PropertySelector<InputNode> selector = new Properties.PropertySelector<InputNode>(graph.getNodes());
        StringPropertyMatcher matcher = new StringPropertyMatcher("bci", bciValue);
        List<InputNode> nodeList = selector.selectMultiple(matcher);
        if (nodeList.size() > 0) {
            nodes = new HashSet<InputNode>();
            for (InputNode n : nodeList) {
                nodes.add(n);
            }
            this.setDisplayName(this.getDisplayName() + " (" + nodes.size() + " nodes)");
        }
    }

    @Override
    public Image getIcon(int i) {
        if (nodes != null) {
            return Utilities.loadImage("com/sun/hotspot/igv/bytecodes/images/link.gif");
        } else {
            return Utilities.loadImage("com/sun/hotspot/igv/bytecodes/images/bytecode.gif");
        }
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public Action[] getActions(boolean b) {
        return new Action[]{(Action) SelectBytecodesAction.findObject(SelectBytecodesAction.class, true)};
    }

    @Override
    public Action getPreferredAction() {
        return (Action) SelectBytecodesAction.findObject(SelectBytecodesAction.class, true);
    }

    @Override
    public <T extends Node.Cookie> T getCookie(Class<T> aClass) {
        if (aClass == SelectBytecodesCookie.class && nodes != null) {
            return (T) (new SelectBytecodesCookie(nodes));
        }
        return super.getCookie(aClass);
    }
}
