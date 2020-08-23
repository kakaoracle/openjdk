/*
 * Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.oops;

import java.util.*;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.memory.*;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.oops.IntField;
import sun.jvm.hotspot.oops.LongField;
import sun.jvm.hotspot.oops.ObjArray;
import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.oops.OopField;
import sun.jvm.hotspot.oops.TypeArray;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.TypeDataBase;
import sun.jvm.hotspot.utilities.*;
import sun.jvm.hotspot.jdi.JVMTIThreadState;

/** A utility class encapsulating useful oop operations */

public class OopUtilities implements /* imports */ JVMTIThreadState {

  // FIXME: access should be synchronized and cleared when VM is
  // resumed
  // String fields
  private static sun.jvm.hotspot.oops.IntField offsetField;
  private static sun.jvm.hotspot.oops.IntField countField;
  private static sun.jvm.hotspot.oops.OopField valueField;
  // ThreadGroup fields
  private static sun.jvm.hotspot.oops.OopField threadGroupParentField;
  private static sun.jvm.hotspot.oops.OopField threadGroupNameField;
  private static sun.jvm.hotspot.oops.IntField threadGroupNThreadsField;
  private static sun.jvm.hotspot.oops.OopField threadGroupThreadsField;
  private static sun.jvm.hotspot.oops.IntField threadGroupNGroupsField;
  private static sun.jvm.hotspot.oops.OopField threadGroupGroupsField;
  // Thread fields
  private static sun.jvm.hotspot.oops.OopField threadNameField;
  private static sun.jvm.hotspot.oops.OopField threadGroupField;
  private static sun.jvm.hotspot.oops.LongField threadEETopField;
  // threadStatus field is new since 1.5
  private static sun.jvm.hotspot.oops.IntField threadStatusField;
  // parkBlocker field is new since 1.6
  private static sun.jvm.hotspot.oops.OopField threadParkBlockerField;

  // possible values of java_lang_Thread::ThreadStatus
  private static int THREAD_STATUS_NEW;
  /*
    Other enum constants are not needed as of now. Uncomment these as and when needed.

    private static int THREAD_STATUS_RUNNABLE;
    private static int THREAD_STATUS_SLEEPING;
    private static int THREAD_STATUS_IN_OBJECT_WAIT;
    private static int THREAD_STATUS_IN_OBJECT_WAIT_TIMED;
    private static int THREAD_STATUS_PARKED;
    private static int THREAD_STATUS_PARKED_TIMED;
    private static int THREAD_STATUS_BLOCKED_ON_MONITOR_ENTER;
    private static int THREAD_STATUS_TERMINATED;
  */

  // java.util.concurrent.locks.AbstractOwnableSynchronizer fields
  private static sun.jvm.hotspot.oops.OopField absOwnSyncOwnerThreadField;

  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) {
    // FIXME: don't need this observer; however, do need a VM resumed
    // and suspended observer to refetch fields
  }

  public static String charArrayToString(sun.jvm.hotspot.oops.TypeArray charArray) {
    if (charArray == null) {
      return null;
    }
    return charArrayToString(charArray, 0, (int) charArray.getLength());
  }

  public static String charArrayToString(sun.jvm.hotspot.oops.TypeArray charArray, int offset, int length) {
    if (charArray == null) {
      return null;
    }
    final int limit = offset + length;
    if (Assert.ASSERTS_ENABLED) {
      Assert.that(offset >= 0 && limit <= charArray.getLength(), "out of bounds");
    }
    StringBuffer buf = new StringBuffer(length);
    for (int i = offset; i < limit; i++) {
      buf.append(charArray.getCharAt(i));
    }
    return buf.toString();
  }

  public static String escapeString(String s) {
    StringBuilder sb = null;
    for (int index = 0; index < s.length(); index++) {
      char value = s.charAt(index);
      if (value >= 32 && value < 127 || value == '\'' || value == '\\') {
        if (sb != null) {
          sb.append(value);
        }
      } else {
        if (sb == null) {
          sb = new StringBuilder(s.length() * 2);
          sb.append(s, 0, index);
        }
        sb.append("\\u");
        if (value < 0x10) sb.append("000");
        else if (value < 0x100) sb.append("00");
        else if (value < 0x1000) sb.append("0");
        sb.append(Integer.toHexString(value));
      }
    }
    if (sb != null) {
      return sb.toString();
    }
    return s;
  }

  public static String stringOopToString(sun.jvm.hotspot.oops.Oop stringOop) {
    if (offsetField == null) {
      InstanceKlass k = (InstanceKlass) stringOop.getKlass();
      offsetField = (sun.jvm.hotspot.oops.IntField) k.findField("offset", "I");   // optional
      countField  = (sun.jvm.hotspot.oops.IntField) k.findField("count",  "I");   // optional
      valueField  = (sun.jvm.hotspot.oops.OopField) k.findField("value",  "[C");
      if (Assert.ASSERTS_ENABLED) {
         Assert.that(valueField != null, "Field \'value\' of java.lang.String not found");
      }
    }
    if (offsetField != null && countField != null) {
      return charArrayToString((sun.jvm.hotspot.oops.TypeArray) valueField.getValue(stringOop),
                               offsetField.getValue(stringOop),
                               countField.getValue(stringOop));
    }
    return  charArrayToString((sun.jvm.hotspot.oops.TypeArray) valueField.getValue(stringOop));
  }

  public static String stringOopToEscapedString(sun.jvm.hotspot.oops.Oop stringOop) {
    return escapeString(stringOopToString(stringOop));
  }

  private static void initThreadGroupFields() {
    if (threadGroupParentField == null) {
      SystemDictionary sysDict = VM.getVM().getSystemDictionary();
      InstanceKlass k = sysDict.getThreadGroupKlass();
      threadGroupParentField   = (sun.jvm.hotspot.oops.OopField) k.findField("parent",   "Ljava/lang/ThreadGroup;");
      threadGroupNameField     = (sun.jvm.hotspot.oops.OopField) k.findField("name",     "Ljava/lang/String;");
      threadGroupNThreadsField = (sun.jvm.hotspot.oops.IntField) k.findField("nthreads", "I");
      threadGroupThreadsField  = (sun.jvm.hotspot.oops.OopField) k.findField("threads",  "[Ljava/lang/Thread;");
      threadGroupNGroupsField  = (sun.jvm.hotspot.oops.IntField) k.findField("ngroups",  "I");
      threadGroupGroupsField   = (sun.jvm.hotspot.oops.OopField) k.findField("groups",   "[Ljava/lang/ThreadGroup;");
      if (Assert.ASSERTS_ENABLED) {
        Assert.that(threadGroupParentField   != null &&
                    threadGroupNameField     != null &&
                    threadGroupNThreadsField != null &&
                    threadGroupThreadsField  != null &&
                    threadGroupNGroupsField  != null &&
                    threadGroupGroupsField   != null, "must find all java.lang.ThreadGroup fields");
      }
    }
  }

  public static sun.jvm.hotspot.oops.Oop threadGroupOopGetParent(sun.jvm.hotspot.oops.Oop threadGroupOop) {
    initThreadGroupFields();
    return threadGroupParentField.getValue(threadGroupOop);
  }

  public static String threadGroupOopGetName(sun.jvm.hotspot.oops.Oop threadGroupOop) {
    initThreadGroupFields();
    return stringOopToString(threadGroupNameField.getValue(threadGroupOop));
  }

  public static sun.jvm.hotspot.oops.Oop[] threadGroupOopGetThreads(sun.jvm.hotspot.oops.Oop threadGroupOop) {
    initThreadGroupFields();
    int nthreads = threadGroupNThreadsField.getValue(threadGroupOop);
    sun.jvm.hotspot.oops.Oop[] result = new sun.jvm.hotspot.oops.Oop[nthreads];
    sun.jvm.hotspot.oops.ObjArray threads = (sun.jvm.hotspot.oops.ObjArray) threadGroupThreadsField.getValue(threadGroupOop);
    for (int i = 0; i < nthreads; i++) {
      result[i] = threads.getObjAt(i);
    }
    return result;
  }

  public static sun.jvm.hotspot.oops.Oop[] threadGroupOopGetGroups(sun.jvm.hotspot.oops.Oop threadGroupOop) {
    initThreadGroupFields();
    int ngroups = threadGroupNGroupsField.getValue(threadGroupOop);
    sun.jvm.hotspot.oops.Oop[] result = new sun.jvm.hotspot.oops.Oop[ngroups];
    sun.jvm.hotspot.oops.ObjArray groups = (ObjArray) threadGroupGroupsField.getValue(threadGroupOop);
    for (int i = 0; i < ngroups; i++) {
      result[i] = groups.getObjAt(i);
    }
    return result;
  }

  private static void initThreadFields() {
    if (threadNameField == null) {
      SystemDictionary sysDict = VM.getVM().getSystemDictionary();
      InstanceKlass k = sysDict.getThreadKlass();
      threadNameField  = (sun.jvm.hotspot.oops.OopField) k.findField("name", "[C");
      threadGroupField = (sun.jvm.hotspot.oops.OopField) k.findField("group", "Ljava/lang/ThreadGroup;");
      threadEETopField = (LongField) k.findField("eetop", "J");
      threadStatusField = (IntField) k.findField("threadStatus", "I");
      threadParkBlockerField = (sun.jvm.hotspot.oops.OopField) k.findField("parkBlocker",
                                     "Ljava/lang/Object;");
      TypeDataBase db = VM.getVM().getTypeDataBase();
      THREAD_STATUS_NEW = db.lookupIntConstant("java_lang_Thread::NEW").intValue();
      /*
        Other enum constants are not needed as of now. Uncomment these as and when needed.

        THREAD_STATUS_RUNNABLE = db.lookupIntConstant("java_lang_Thread::RUNNABLE").intValue();
        THREAD_STATUS_SLEEPING = db.lookupIntConstant("java_lang_Thread::SLEEPING").intValue();
        THREAD_STATUS_IN_OBJECT_WAIT = db.lookupIntConstant("java_lang_Thread::IN_OBJECT_WAIT").intValue();
        THREAD_STATUS_IN_OBJECT_WAIT_TIMED = db.lookupIntConstant("java_lang_Thread::IN_OBJECT_WAIT_TIMED").intValue();
        THREAD_STATUS_PARKED = db.lookupIntConstant("java_lang_Thread::PARKED").intValue();
        THREAD_STATUS_PARKED_TIMED = db.lookupIntConstant("java_lang_Thread::PARKED_TIMED").intValue();
        THREAD_STATUS_BLOCKED_ON_MONITOR_ENTER = db.lookupIntConstant("java_lang_Thread::BLOCKED_ON_MONITOR_ENTER").intValue();
        THREAD_STATUS_TERMINATED = db.lookupIntConstant("java_lang_Thread::TERMINATED").intValue();
      */

      if (Assert.ASSERTS_ENABLED) {
        // it is okay to miss threadStatusField, because this was
        // introduced only in 1.5 JDK.
        Assert.that(threadNameField   != null &&
                    threadGroupField  != null &&
                    threadEETopField  != null, "must find all java.lang.Thread fields");
      }
    }
  }

  public static sun.jvm.hotspot.oops.Oop threadOopGetThreadGroup(sun.jvm.hotspot.oops.Oop threadOop) {
    initThreadFields();
    return threadGroupField.getValue(threadOop);
  }

  public static String threadOopGetName(sun.jvm.hotspot.oops.Oop threadOop) {
    initThreadFields();
    return charArrayToString((TypeArray) threadNameField.getValue(threadOop));
  }

  /** May return null if, e.g., thread was not started */
  public static JavaThread threadOopGetJavaThread(sun.jvm.hotspot.oops.Oop threadOop) {
    initThreadFields();
    Address addr = threadOop.getHandle().getAddressAt(threadEETopField.getOffset());
    if (addr == null) {
      return null;
    }
    return VM.getVM().getThreads().createJavaThreadWrapper(addr);
  }

  /** returns value of java.lang.Thread.threadStatus field */
  public static int threadOopGetThreadStatus(sun.jvm.hotspot.oops.Oop threadOop) {
    initThreadFields();
    // The threadStatus is only present starting in 1.5
    if (threadStatusField != null) {
      return (int) threadStatusField.getValue(threadOop);
    } else {
      // All we can easily figure out is if it is alive, but that is
      // enough info for a valid unknown status.
      JavaThread thr = threadOopGetJavaThread(threadOop);
      if (thr == null) {
        // the thread hasn't run yet or is in the process of exiting
        return THREAD_STATUS_NEW;
      } else {
        return JVMTI_THREAD_STATE_ALIVE;
      }
    }
  }

  /** returns value of java.lang.Thread.parkBlocker field */
  public static sun.jvm.hotspot.oops.Oop threadOopGetParkBlocker(sun.jvm.hotspot.oops.Oop threadOop) {
    initThreadFields();
    if (threadParkBlockerField != null) {
      return threadParkBlockerField.getValue(threadOop);
    }
    return null;
  }

  // initialize fields for j.u.c.l AbstractOwnableSynchornizer class
  private static void initAbsOwnSyncFields() {
    if (absOwnSyncOwnerThreadField == null) {
       SystemDictionary sysDict = VM.getVM().getSystemDictionary();
       InstanceKlass k = sysDict.getAbstractOwnableSynchronizerKlass();
       absOwnSyncOwnerThreadField =
           (OopField) k.findField("exclusiveOwnerThread",
                                  "Ljava/lang/Thread;");
    }
  }

  // return exclusiveOwnerThread field of AbstractOwnableSynchronizer class
  public static sun.jvm.hotspot.oops.Oop abstractOwnableSynchronizerGetOwnerThread(Oop oop) {
    initAbsOwnSyncFields();
    if (absOwnSyncOwnerThreadField == null) {
      return null; // pre-1.6 VM?
    } else {
      return absOwnSyncOwnerThreadField.getValue(oop);
    }
  }
}
