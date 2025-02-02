package hotspot.test.runtime/*
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
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
 */

/*
 * @test Test6981737.java
 * @bug 6981737
 * @summary check for correct vm properties
 * @run main Test6981737
 * @author kamg
*/

public class Test6981737 {

    /**
     * Check the 'vendor' properties java.vm.specification.version
     * property.  Before jdk7, they should be "Sun Micro..." and "1.0".
     * In jdk7 onwards they should be "Oracle..." and "1.<major_version>"
     */
    public static void main(String[] args) throws Exception {

        String version = verifyProperty("java.version", "[0-9]+\\.[0-9]+\\..*");
        String major_version_spec = version.split("\\.")[1];
        int major_version = new Integer(major_version_spec).intValue();

        String vendor_re = "Oracle Corporation";
        String vm_spec_version_re = "1\\." + major_version_spec;
        if (major_version < 7) {
            vendor_re = "Sun Microsystems Inc\\.";
            vm_spec_version_re = "1\\.0";
        }
        verifyProperty("java.vendor", vendor_re);
        verifyProperty("java.vm.vendor", vendor_re);
        verifyProperty("java.vm.specification.vendor", vendor_re);
        verifyProperty("java.specification.vendor", vendor_re);
        verifyProperty("java.vm.specification.version", vm_spec_version_re);
        System.out.println("PASS");
    }

    public static String verifyProperty(String name, String expected_re) {
        String value = System.getProperty(name, "");
        System.out.print("Checking " + name + ": \"" + value +
          "\".matches(\"" + expected_re + "\")... ");
        if (!value.matches(expected_re)) {
            System.out.println("no.");
            throw new RuntimeException("FAIL: Wrong value for " + name +
                " property, \"" + value + "\", expected to be of form: \"" +
                expected_re + "\"");
        }
        System.out.println("yes.");
        return value;
    }
}
