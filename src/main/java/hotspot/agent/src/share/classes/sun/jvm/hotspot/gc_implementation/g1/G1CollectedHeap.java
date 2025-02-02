/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.gc_implementation.g1;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import sun.jvm.hotspot.debugger.Address;
import sun.jvm.hotspot.gc_interface.CollectedHeapName;
import sun.jvm.hotspot.memory.MemRegion;
import sun.jvm.hotspot.memory.SharedHeap;
import sun.jvm.hotspot.memory.SpaceClosure;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.runtime.VMObjectFactory;
import sun.jvm.hotspot.types.AddressField;
import sun.jvm.hotspot.types.CIntegerField;
import sun.jvm.hotspot.types.Type;
import sun.jvm.hotspot.types.TypeDataBase;

// Mirror class for G1CollectedHeap.

public class G1CollectedHeap extends SharedHeap {
    // HeapRegionSeq _seq;
    static private long hrsFieldOffset;
    // MemRegion _g1_committed;
    static private long g1CommittedFieldOffset;
    // size_t _summary_bytes_used;
    static private CIntegerField summaryBytesUsedField;
    // G1MonitoringSupport* _g1mm;
    static private AddressField g1mmField;
    // MasterOldRegionSet _old_set;
    static private long oldSetFieldOffset;
    // MasterHumongousRegionSet _humongous_set;
    static private long humongousSetFieldOffset;

    static {
        VM.registerVMInitializedObserver(new Observer() {
                public void update(Observable o, Object data) {
                    initialize(VM.getVM().getTypeDataBase());
                }
            });
    }

    static private synchronized void initialize(TypeDataBase db) {
        Type type = db.lookupType("G1CollectedHeap");

        hrsFieldOffset = type.getField("_hrs").getOffset();
        g1CommittedFieldOffset = type.getField("_g1_committed").getOffset();
        summaryBytesUsedField = type.getCIntegerField("_summary_bytes_used");
        g1mmField = type.getAddressField("_g1mm");
        oldSetFieldOffset = type.getField("_old_set").getOffset();
        humongousSetFieldOffset = type.getField("_humongous_set").getOffset();
    }

    public long capacity() {
        Address g1CommittedAddr = addr.addOffsetTo(g1CommittedFieldOffset);
        MemRegion g1Committed = new MemRegion(g1CommittedAddr);
        return g1Committed.byteSize();
    }

    public long used() {
        return summaryBytesUsedField.getValue(addr);
    }

    public long n_regions() {
        return hrs().length();
    }

    private HeapRegionSeq hrs() {
        Address hrsAddr = addr.addOffsetTo(hrsFieldOffset);
        return (HeapRegionSeq) VMObjectFactory.newObject(HeapRegionSeq.class,
                                                         hrsAddr);
    }

    public G1MonitoringSupport g1mm() {
        Address g1mmAddr = g1mmField.getValue(addr);
        return (G1MonitoringSupport) VMObjectFactory.newObject(G1MonitoringSupport.class, g1mmAddr);
    }

    public HeapRegionSetBase oldSet() {
        Address oldSetAddr = addr.addOffsetTo(oldSetFieldOffset);
        return (HeapRegionSetBase) VMObjectFactory.newObject(HeapRegionSetBase.class,
                                                             oldSetAddr);
    }

    public HeapRegionSetBase humongousSet() {
        Address humongousSetAddr = addr.addOffsetTo(humongousSetFieldOffset);
        return (HeapRegionSetBase) VMObjectFactory.newObject(HeapRegionSetBase.class,
                                                             humongousSetAddr);
    }

    private Iterator<HeapRegion> heapRegionIterator() {
        return hrs().heapRegionIterator();
    }

    public void heapRegionIterate(SpaceClosure scl) {
        Iterator<HeapRegion> iter = heapRegionIterator();
        while (iter.hasNext()) {
            HeapRegion hr = iter.next();
            scl.doSpace(hr);
        }
    }

    public CollectedHeapName kind() {
        return CollectedHeapName.G1_COLLECTED_HEAP;
    }

    public G1CollectedHeap(Address addr) {
        super(addr);
    }
}
