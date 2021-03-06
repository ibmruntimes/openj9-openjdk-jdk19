/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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

import javax.swing.JButton;
import javax.swing.JTabbedPane;
import java.awt.Component;

import static javax.swing.SwingUtilities.invokeAndWait;

/*
 * @test
 * @bug 4873983 6943780
 * @summary Tests JTabbedPane with SCROLL_TAB_LAYOUT
 * @author Sergey Malenkov
 */
public class Test6943780 implements Runnable, Thread.UncaughtExceptionHandler {
    public static void main(String[] args) throws Exception {
        invokeAndWait(new Test6943780());
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();
        throw new RuntimeException(throwable);
    }

    @Override
    public void run() {
        JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        pane.addTab("first", new JButton("first"));
        pane.addTab("second", new JButton("second"));
        for (Component component : pane.getComponents()) {
            component.setSize(100, 100);
        }
    }
}
