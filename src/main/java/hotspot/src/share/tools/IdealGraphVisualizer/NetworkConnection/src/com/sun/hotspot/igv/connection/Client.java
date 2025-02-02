/*
 * Copyright (c) 1998, 2007, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
package hotspot.src.share.tools.IdealGraphVisualizer.NetworkConnection.src.com.sun.hotspot.igv.connection;

import com.sun.hotspot.igv.data.Group;
import com.sun.hotspot.igv.data.services.GroupCallback;
import com.sun.hotspot.igv.data.serialization.Parser;
import com.sun.hotspot.igv.data.Properties.RegexpPropertyMatcher;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import javax.swing.JTextField;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author Thomas Wuerthinger
 */
public class Client implements Runnable, GroupCallback {

    private Socket socket;
    private JTextField networkTextField;
    private GroupCallback callback;

    public Client(Socket socket, JTextField networkTextField, GroupCallback callback) {
        this.callback = callback;
        this.socket = socket;
        this.networkTextField = networkTextField;
    }

    public void run() {

        try {
            InputStream inputStream = socket.getInputStream();

            if (networkTextField.isEnabled()) {

                socket.getOutputStream().write('y');
                InputSource is = new InputSource(inputStream);

                try {
                    XMLReader reader = XMLUtil.createXMLReader();
                    Parser parser = new Parser(this);
                    parser.parse(reader, is, null);
                } catch (SAXException ex) {
                    ex.printStackTrace();
                }
            } else {
                socket.getOutputStream().write('n');
            }

            socket.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void started(final Group g) {
        try {
            RegexpPropertyMatcher matcher = new RegexpPropertyMatcher("name", ".*" + networkTextField.getText() + ".*");
            if (g.getProperties().selectSingle(matcher) != null && networkTextField.isEnabled()) {
                socket.getOutputStream().write('y');
                callback.started(g);
            } else {
                socket.getOutputStream().write('n');
            }
        } catch (IOException e) {
        }
    }
}
