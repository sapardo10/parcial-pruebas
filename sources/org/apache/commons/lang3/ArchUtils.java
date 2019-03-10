package org.apache.commons.lang3;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.arch.Processor;
import org.apache.commons.lang3.arch.Processor.Arch;
import org.apache.commons.lang3.arch.Processor.Type;

public class ArchUtils {
    private static final Map<String, Processor> ARCH_TO_PROCESSOR = new HashMap();

    static {
        init();
    }

    private static void init() {
        init_X86_32Bit();
        init_X86_64Bit();
        init_IA64_32Bit();
        init_IA64_64Bit();
        init_PPC_32Bit();
        init_PPC_64Bit();
    }

    private static void init_X86_32Bit() {
        addProcessors(new Processor(Arch.BIT_32, Type.X86), "x86", "i386", "i486", "i586", "i686", "pentium");
    }

    private static void init_X86_64Bit() {
        addProcessors(new Processor(Arch.BIT_64, Type.X86), "x86_64", "amd64", "em64t", "universal");
    }

    private static void init_IA64_32Bit() {
        addProcessors(new Processor(Arch.BIT_32, Type.IA_64), "ia64_32", "ia64n");
    }

    private static void init_IA64_64Bit() {
        addProcessors(new Processor(Arch.BIT_64, Type.IA_64), "ia64", "ia64w");
    }

    private static void init_PPC_32Bit() {
        addProcessors(new Processor(Arch.BIT_32, Type.PPC), "ppc", "power", "powerpc", "power_pc", "power_rs");
    }

    private static void init_PPC_64Bit() {
        addProcessors(new Processor(Arch.BIT_64, Type.PPC), "ppc64", "power64", "powerpc64", "power_pc64", "power_rs64");
    }

    private static void addProcessor(String key, Processor processor) throws IllegalStateException {
        if (ARCH_TO_PROCESSOR.containsKey(key)) {
            String msg = new StringBuilder();
            msg.append("Key ");
            msg.append(key);
            msg.append(" already exists in processor map");
            throw new IllegalStateException(msg.toString());
        }
        ARCH_TO_PROCESSOR.put(key, processor);
    }

    private static void addProcessors(Processor processor, String... keys) throws IllegalStateException {
        for (String key : keys) {
            addProcessor(key, processor);
        }
    }

    public static Processor getProcessor() {
        return getProcessor(SystemUtils.OS_ARCH);
    }

    public static Processor getProcessor(String value) {
        return (Processor) ARCH_TO_PROCESSOR.get(value);
    }
}
