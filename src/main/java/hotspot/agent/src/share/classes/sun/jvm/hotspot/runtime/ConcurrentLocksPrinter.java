/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

import java.io.*;
import java.util.*;
import sun.jvm.hotspot.memory.*;
import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.runtime.JavaThread;
import sun.jvm.hotspot.runtime.VM;

public class ConcurrentLocksPrinter {
    private Map locksMap = new HashMap(); // <JavaThread, List<Oop>>

    public ConcurrentLocksPrinter() {
        fillLocks();
    }

    public void print(sun.jvm.hotspot.runtime.JavaThread jthread, PrintStream tty) {
        List locks = (List) locksMap.get(jthread);
        tty.println("Locked ownable synchronizers:");
        if (locks == null || locks.isEmpty()) {
            tty.println("    - None");
        } else {
            for (Iterator itr = locks.iterator(); itr.hasNext();) {
                Oop oop = (Oop) itr.next();
                tty.println("    - <" + oop.getHandle() + ">, (a " +
                       oop.getKlass().getName().asString() + ")");
            }
        }
    }

    //-- Internals only below this point
    private sun.jvm.hotspot.runtime.JavaThread getOwnerThread(Oop oop) {
        Oop threadOop = OopUtilities.abstractOwnableSynchronizerGetOwnerThread(oop);
        if (threadOop == null) {
            return null;
        } else {
            return OopUtilities.threadOopGetJavaThread(threadOop);
        }
    }

    private void fillLocks() {
        sun.jvm.hotspot.runtime.VM vm = VM.getVM();
        SystemDictionary sysDict = vm.getSystemDictionary();
        Klass absOwnSyncKlass = sysDict.getAbstractOwnableSynchronizerKlass();
        ObjectHeap heap = vm.getObjectHeap();
        // may be not loaded at all
        if (absOwnSyncKlass != null) {
            heap.iterateObjectsOfKlass(new DefaultHeapVisitor() {
                    public boolean doObj(Oop oop) {
                        JavaThread thread = getOwnerThread(oop);
                        if (thread != null) {
                            List locks = (List) locksMap.get(thread);
                            if (locks == null) {
                                locks = new LinkedList();
                                locksMap.put(thread, locks);
                            }
                            locks.add(oop);
                        }
                        return false;
                    }

                }, absOwnSyncKlass, true);
        }
    }
}
