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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.ui.action;

import com.sun.java.swing.action.ActionManager;
import sun.jvm.hotspot.ui.action.*;
import sun.jvm.hotspot.ui.action.FindAction;
import sun.jvm.hotspot.ui.action.FindClassesAction;
import sun.jvm.hotspot.ui.action.FindCrashesAction;
import sun.jvm.hotspot.ui.action.InspectAction;
import sun.jvm.hotspot.ui.action.MemoryAction;
import sun.jvm.hotspot.ui.action.ShowAction;
import sun.jvm.hotspot.ui.action.ThreadInfoAction;

/**
 * An application specific implementation of an ActionManager
 */
public class HSDBActionManager extends ActionManager {

    public static ActionManager getInstance() {
        if (manager == null) {
            manager = new HSDBActionManager();
        }
        return manager;
    }

    protected void addActions() {
        // actions for ObjectHistogramPanel
        addAction(sun.jvm.hotspot.ui.action.FindAction.VALUE_COMMAND, new FindAction());
        addAction(sun.jvm.hotspot.ui.action.ShowAction.VALUE_COMMAND, new ShowAction());

        // Actions for Java Threads Panel
        addAction(sun.jvm.hotspot.ui.action.InspectAction.VALUE_COMMAND, new InspectAction());
        addAction(sun.jvm.hotspot.ui.action.MemoryAction.VALUE_COMMAND, new MemoryAction());
        addAction(sun.jvm.hotspot.ui.action.ThreadInfoAction.VALUE_COMMAND, new ThreadInfoAction());
        addAction(sun.jvm.hotspot.ui.action.FindCrashesAction.VALUE_COMMAND, new FindCrashesAction());
        addAction(JavaStackTraceAction.VALUE_COMMAND, new JavaStackTraceAction());

        // Action for ClassBrowserPanel
        addAction(sun.jvm.hotspot.ui.action.FindClassesAction.VALUE_COMMAND, new FindClassesAction());
    }
}
