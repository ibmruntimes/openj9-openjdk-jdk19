/*
 * Copyright (c) 2004, 2021, Oracle and/or its affiliates. All rights reserved.
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
 * @test
 * @bug 5047639 8132785
 * @summary Check that the "java-level" APIs provide a consistent view of
 *          the thread list
 * @comment Must run in othervm mode to avoid interference from other tests.
 * @run main/othervm ThreadLists
 */
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;

public class ThreadLists {
    public static void main(String args[]) {

        // Bug id : JDK-8151797
        // Use a lambda expression so that call-site cleaner thread is started
        Runnable printLambda = () -> {System.out.println("Starting Test");};
        printLambda.run();

        // get top-level thread group
        ThreadGroup top = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        do {
            parent = top.getParent();
            if (parent != null) top = parent;
        } while (parent != null);

        // get the thread count
        int activeCount = top.activeCount();

        // Now enumerate to see if we find any extras yet.
        // Ensure the array is big enough for a few extras.
        Thread[] threads = new Thread[activeCount * 2];
        int newCount = top.enumerate(threads);
        if (newCount != activeCount) {
            System.out.println("Found different threads after enumeration:");
        } else {
            System.out.println("Initial set of enumerated threads:");
        }
        for (int i = 0; i < newCount; i++) {
            System.out.println(" - Thread: " + threads[i].getName());
        }

        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();

        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        int threadCount = threadBean.getThreadCount();
        long[] threadIds = threadBean.getAllThreadIds();

        System.out.println("ThreadGroup: " + activeCount + " active thread(s)");
        System.out.println("Thread: " + stackTraces.size() + " stack trace(s) returned");
        System.out.println("ThreadMXBean: " + threadCount + " live threads(s)");
        System.out.println("ThreadMXBean: " + threadIds.length + " thread Id(s)");

        // check results are consistent
        boolean failed = false;
        if (activeCount != stackTraces.size()) failed = true;
        if (activeCount != threadCount) failed = true;
        if (activeCount != threadIds.length) failed = true;

        if (failed) {
            System.out.println("Set of stack-traced threads:");
            for (Thread t : stackTraces.keySet()) {
                System.out.println(" - Thread: " +
                                   (t != null ? t.getName() : "null!"));
            }
            throw new RuntimeException("inconsistent results");
        }
    }
}
