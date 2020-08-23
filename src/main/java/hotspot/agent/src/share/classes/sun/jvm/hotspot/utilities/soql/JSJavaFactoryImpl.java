/*
 * Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.utilities.soql;

import java.lang.ref.*;
import java.util.*;
import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.utilities.*;
import sun.jvm.hotspot.utilities.soql.*;
import sun.jvm.hotspot.utilities.soql.JSJavaClass;
import sun.jvm.hotspot.utilities.soql.JSJavaFactory;
import sun.jvm.hotspot.utilities.soql.JSJavaField;
import sun.jvm.hotspot.utilities.soql.JSJavaFrame;
import sun.jvm.hotspot.utilities.soql.JSJavaHeap;
import sun.jvm.hotspot.utilities.soql.JSJavaInstance;
import sun.jvm.hotspot.utilities.soql.JSJavaKlass;
import sun.jvm.hotspot.utilities.soql.JSJavaMethod;
import sun.jvm.hotspot.utilities.soql.JSJavaObjArray;
import sun.jvm.hotspot.utilities.soql.JSJavaObject;
import sun.jvm.hotspot.utilities.soql.JSJavaString;
import sun.jvm.hotspot.utilities.soql.JSJavaThread;
import sun.jvm.hotspot.utilities.soql.JSJavaTypeArray;
import sun.jvm.hotspot.utilities.soql.JSJavaVM;
import sun.jvm.hotspot.utilities.soql.JSList;
import sun.jvm.hotspot.utilities.soql.JSMap;

public class JSJavaFactoryImpl implements JSJavaFactory {
   public sun.jvm.hotspot.utilities.soql.JSJavaObject newJSJavaObject(Oop oop) {
      if (oop == null) return null;
      SoftReference sref = (SoftReference) om.get(oop);
      sun.jvm.hotspot.utilities.soql.JSJavaObject res = (sref != null)? (sun.jvm.hotspot.utilities.soql.JSJavaObject) sref.get() : null;
      if (res == null) {
         if (oop instanceof TypeArray) {
            res = new JSJavaTypeArray((TypeArray)oop, this);
         } else if (oop instanceof ObjArray) {
             res = new JSJavaObjArray((ObjArray)oop, this);
         } else if (oop instanceof Instance) {
            res = newJavaInstance((Instance) oop);
         }
      }
      if (res != null) {
         om.put(oop, new SoftReference(res));
      }
      return res;
   }

   public sun.jvm.hotspot.utilities.soql.JSJavaKlass newJSJavaKlass(Klass klass) {
      sun.jvm.hotspot.utilities.soql.JSJavaKlass res = null;
      if (klass instanceof InstanceKlass) {
          res = new JSJavaInstanceKlass((InstanceKlass) klass, this);
      } else if (klass instanceof ObjArrayKlass) {
          res = new JSJavaObjArrayKlass((ObjArrayKlass) klass, this);
      } else if (klass instanceof TypeArrayKlass) {
          res = new JSJavaTypeArrayKlass((TypeArrayKlass) klass, this);
      }
      if (res != null) {
         om.put(klass, new SoftReference(res));
      }
      return res;
   }

   public sun.jvm.hotspot.utilities.soql.JSJavaMethod newJSJavaMethod(Method method) {
      sun.jvm.hotspot.utilities.soql.JSJavaMethod res = new JSJavaMethod(method, this);
      if (res != null) {
         om.put(method, new SoftReference(res));
      }
      return res;
   }

   public sun.jvm.hotspot.utilities.soql.JSJavaField newJSJavaField(Field field) {
      if (field == null) return null;
      return new JSJavaField(field, this);
   }

   public sun.jvm.hotspot.utilities.soql.JSJavaThread newJSJavaThread(JavaThread jthread) {
      if (jthread == null) return null;
      return new sun.jvm.hotspot.utilities.soql.JSJavaThread(jthread, this);
   }

   public sun.jvm.hotspot.utilities.soql.JSJavaFrame newJSJavaFrame(JavaVFrame jvf) {
      if (jvf == null) return null;
      return new JSJavaFrame(jvf, this);
   }

   public sun.jvm.hotspot.utilities.soql.JSList newJSList(List list) {
      if (list == null) return null;
      return new JSList(list, this);
   }

   public sun.jvm.hotspot.utilities.soql.JSMap newJSMap(Map map) {
      if (map == null) return null;
      return new JSMap(map, this);
   }

   public Object newJSJavaWrapper(Object item) {
      if (item == null) return null;
      if (item instanceof Oop) {
         return newJSJavaObject((Oop) item);
      } else if (item instanceof Field) {
         return newJSJavaField((Field) item);
      } else if (item instanceof JavaThread) {
         return newJSJavaThread((JavaThread) item);
      } else if (item instanceof JavaVFrame) {
         return newJSJavaFrame((JavaVFrame) item);
      } else if (item instanceof List) {
         return newJSList((List) item);
      } else if (item instanceof Map) {
         return newJSMap((Map) item);
      } else {
         // not-a-special-type, just return the input item
         return item;
      }
   }

   public sun.jvm.hotspot.utilities.soql.JSJavaHeap newJSJavaHeap() {
      return new JSJavaHeap(this);
   }

   public sun.jvm.hotspot.utilities.soql.JSJavaVM newJSJavaVM() {
      return new JSJavaVM(this);
   }

   // -- Internals only below this point
   private Symbol javaLangString() {
      if (javaLangString == null) {
         javaLangString = getSymbol("java/lang/String");
      }
      return javaLangString;
   }

   private Symbol javaLangThread() {
      if (javaLangThread == null) {
         javaLangThread = getSymbol("java/lang/Thread");
      }
      return javaLangThread;
   }

   private Symbol javaLangClass() {
      if (javaLangClass == null) {
         javaLangClass = getSymbol("java/lang/Class");
      }
      return javaLangClass;
   }

   private Symbol getSymbol(String str) {
      return VM.getVM().getSymbolTable().probe(str);
   }

   private sun.jvm.hotspot.utilities.soql.JSJavaObject newJavaInstance(Instance instance) {
      // look for well-known classes
      Symbol className = instance.getKlass().getName();
      if (Assert.ASSERTS_ENABLED) {
         Assert.that(className != null, "Null class name");
      }
      JSJavaObject res = null;
      if (className.equals(javaLangString())) {
         res = new JSJavaString(instance, this);
      } else if (className.equals(javaLangThread())) {
         res = new sun.jvm.hotspot.utilities.soql.JSJavaThread(instance, this);
      } else if (className.equals(javaLangClass())) {
         Klass reflectedType = java_lang_Class.asKlass(instance);
         if (reflectedType != null) {
             JSJavaKlass jk = newJSJavaKlass(reflectedType);
             // we don't support mirrors of VM internal Klasses
             if (jk == null) return null;
             res = new JSJavaClass(instance, jk, this);
         } else {
             // for primitive Classes, the reflected type is null
             return null;
         }
      } else {
         // not a well-known class. But the base class may be
         // one of the known classes.
         Klass kls = instance.getKlass().getSuper();
         while (kls != null) {
            className = kls.getName();
            // java.lang.Class and java.lang.String are final classes
            if (className.equals(javaLangThread())) {
               res = new JSJavaThread(instance, this);
               break;
            }
            kls = kls.getSuper();
         }
      }
      if (res == null) {
         res = new JSJavaInstance(instance, this);
      }
      return res;
   }

   // Map<Oop, SoftReference<JSJavaObject>>
   private Map om = new HashMap();
   private Symbol javaLangString;
   private Symbol javaLangThread;
   private Symbol javaLangClass;
}
