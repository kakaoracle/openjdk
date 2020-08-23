/*
 * Copyright (c) 2002, 2012, Oracle and/or its affiliates. All rights reserved.
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

package hotspot.agent.src.share.classes.sun.jvm.hotspot.interpreter;

import java.util.*;
import java.lang.reflect.Constructor;

import sun.jvm.hotspot.interpreter.*;
import sun.jvm.hotspot.interpreter.Bytecode;
import sun.jvm.hotspot.interpreter.BytecodeANewArray;
import sun.jvm.hotspot.interpreter.BytecodeBipush;
import sun.jvm.hotspot.interpreter.BytecodeCheckCast;
import sun.jvm.hotspot.interpreter.BytecodeGetField;
import sun.jvm.hotspot.interpreter.BytecodeGetStatic;
import sun.jvm.hotspot.interpreter.BytecodeGoto;
import sun.jvm.hotspot.interpreter.BytecodeGotoW;
import sun.jvm.hotspot.interpreter.BytecodeIf;
import sun.jvm.hotspot.interpreter.BytecodeIinc;
import sun.jvm.hotspot.interpreter.BytecodeInstanceOf;
import sun.jvm.hotspot.interpreter.BytecodeInvoke;
import sun.jvm.hotspot.interpreter.BytecodeJsr;
import sun.jvm.hotspot.interpreter.BytecodeJsrW;
import sun.jvm.hotspot.interpreter.BytecodeLoad;
import sun.jvm.hotspot.interpreter.BytecodeNew;
import sun.jvm.hotspot.interpreter.BytecodeNewArray;
import sun.jvm.hotspot.interpreter.BytecodePutField;
import sun.jvm.hotspot.interpreter.BytecodePutStatic;
import sun.jvm.hotspot.interpreter.BytecodeRet;
import sun.jvm.hotspot.interpreter.BytecodeSipush;
import sun.jvm.hotspot.interpreter.BytecodeStore;
import sun.jvm.hotspot.interpreter.BytecodeStream;
import sun.jvm.hotspot.interpreter.BytecodeTableswitch;
import sun.jvm.hotspot.interpreter.BytecodeVisitor;
import sun.jvm.hotspot.interpreter.Bytecodes;
import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.utilities.*;

public class BytecodeDisassembler {
   private Method method;

   private static Map bytecode2Class = new HashMap(); // Map<int, Class>

   private static void addBytecodeClass(int bytecode, Class clazz) {
      bytecode2Class.put(new Integer(bytecode), clazz);
   }

   private static Class getBytecodeClass(int bytecode) {
      return (Class) bytecode2Class.get(new Integer(bytecode));
   }

   static {
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._anewarray, BytecodeANewArray.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._bipush, BytecodeBipush.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._checkcast, BytecodeCheckCast.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._getfield, BytecodeGetField.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._getstatic, BytecodeGetStatic.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._goto, BytecodeGoto.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._goto_w, BytecodeGotoW.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ifeq, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ifne, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._iflt, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ifge, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ifgt, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ifle, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._if_icmpeq, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._if_icmpne, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._if_icmplt, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._if_icmpge, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._if_icmpgt, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._if_icmple, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._if_acmpeq, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._if_acmpne, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ifnull, sun.jvm.hotspot.interpreter.BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ifnonnull, BytecodeIf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._iinc, BytecodeIinc.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._instanceof, BytecodeInstanceOf.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._invokevirtual, sun.jvm.hotspot.interpreter.BytecodeInvoke.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._invokestatic, sun.jvm.hotspot.interpreter.BytecodeInvoke.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._invokespecial, sun.jvm.hotspot.interpreter.BytecodeInvoke.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._invokeinterface, sun.jvm.hotspot.interpreter.BytecodeInvoke.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._invokedynamic, BytecodeInvoke.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._jsr, BytecodeJsr.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._jsr_w, BytecodeJsrW.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._iload, sun.jvm.hotspot.interpreter.BytecodeLoad.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._lload, sun.jvm.hotspot.interpreter.BytecodeLoad.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._fload, sun.jvm.hotspot.interpreter.BytecodeLoad.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._dload, sun.jvm.hotspot.interpreter.BytecodeLoad.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._aload, BytecodeLoad.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ldc,   BytecodeLoadConstant.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ldc_w, BytecodeLoadConstant.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ldc2_w, BytecodeLoadConstant.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._lookupswitch, BytecodeLookupswitch.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._multianewarray, BytecodeMultiANewArray.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._new, BytecodeNew.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._newarray, BytecodeNewArray.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._putfield, BytecodePutField.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._putstatic, BytecodePutStatic.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._ret, BytecodeRet.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._sipush, BytecodeSipush.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._istore, sun.jvm.hotspot.interpreter.BytecodeStore.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._lstore, sun.jvm.hotspot.interpreter.BytecodeStore.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._fstore, sun.jvm.hotspot.interpreter.BytecodeStore.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._dstore, sun.jvm.hotspot.interpreter.BytecodeStore.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._astore, BytecodeStore.class);
      addBytecodeClass(sun.jvm.hotspot.interpreter.Bytecodes._tableswitch, BytecodeTableswitch.class);
   }

   public BytecodeDisassembler(Method method) {
      this.method = method;
   }

   public Method getMethod() {
      return method;
   }

   public void decode(BytecodeVisitor visitor) {
      visitor.prologue(method);

      sun.jvm.hotspot.interpreter.BytecodeStream stream = new BytecodeStream(method);
      int javacode = sun.jvm.hotspot.interpreter.Bytecodes._illegal;
      while ( (javacode = stream.next()) != Bytecodes._illegal) {
         // look for special Bytecode class
         int bci = stream.bci();
         int hotspotcode = method.getBytecodeOrBPAt(bci);
         Class clazz = getBytecodeClass(javacode);
         if (clazz == null) {
            // check for fast_(i|a)_access_0
            clazz = getBytecodeClass(hotspotcode);
            if (clazz == null) {
               // use generic bytecode class
               clazz = sun.jvm.hotspot.interpreter.Bytecode.class;
            }
         }

         // All bytecode classes must have a constructor with signature
         // (Lsun/jvm/hotspot/oops/Method;I)V

         Constructor cstr = null;
         try {
            cstr = clazz.getDeclaredConstructor(new Class[] { Method.class, Integer.TYPE });
         } catch(NoSuchMethodException nomethod) {
            if (Assert.ASSERTS_ENABLED) {
               Assert.that(false, "Bytecode class without proper constructor!");
            }
         }

         sun.jvm.hotspot.interpreter.Bytecode bytecodeObj = null;
         try {
            bytecodeObj = (sun.jvm.hotspot.interpreter.Bytecode)cstr.newInstance(new Object[] { method, new Integer(bci) });
         } catch (Exception exp) {
            if (Assert.ASSERTS_ENABLED) {
               Assert.that(false, "Bytecode instance of class "
                           + clazz.getName() + " can not be created!");
            }
         }

         if (stream.isWide()) {
            visitor.visit(new Bytecode(method, bci - 1));
         }

         try {
            visitor.visit(bytecodeObj);
         } catch(ClassCastException castfail) {
             castfail.printStackTrace();
             System.err.println(method.getAddress() + " " + bci);
         }
      }

      visitor.epilogue();
   }
}
