package org.awaitility.core;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Arrays;

public class DeadlockException extends Throwable {
    private final ThreadInfo[] threadInfos;

    public DeadlockException(long[] threads) {
        super("Deadlocked threads detected");
        this.threadInfos = ManagementFactory.getThreadMXBean().getThreadInfo(threads, true, true);
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getMessage());
        sb.append(":\n\n");
        for (ThreadInfo info : this.threadInfos) {
            sb.append(info.toString());
        }
        return sb.toString();
    }

    public ThreadInfo[] getThreadInfos() {
        ThreadInfo[] threadInfoArr = this.threadInfos;
        return (ThreadInfo[]) Arrays.copyOf(threadInfoArr, threadInfoArr.length);
    }
}
