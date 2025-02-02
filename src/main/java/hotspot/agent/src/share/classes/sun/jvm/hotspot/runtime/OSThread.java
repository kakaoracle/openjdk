/*
 * Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.runtime;

import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.runtime.VMObject;
import sun.jvm.hotspot.types.*;

// The OSThread class holds OS-specific thread information.  It is equivalent
// to the sys_thread_t structure of the classic JVM implementation.
public class OSThread extends VMObject {
    private static JIntField interruptedField;
    private static Field threadIdField;
    static {
        sun.jvm.hotspot.runtime.VM.registerVMInitializedObserver(new Observer() {
            public void update(Observable o, Object data) {
                initialize(VM.getVM().getTypeDataBase());
            }
        });
    }

    private static synchronized void initialize(TypeDataBase db) {
        Type type = db.lookupType("OSThread");
        interruptedField = type.getJIntField("_interrupted");
        threadIdField = type.getField("_thread_id");
    }

    public OSThread(Address addr) {
        super(addr);
    }

    public boolean interrupted() {
        return ((int)interruptedField.getValue(addr)) != 0;
    }

    public int threadId() {
        return threadIdField.getJInt(addr);
    }

}
