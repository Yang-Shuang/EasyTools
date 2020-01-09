package com.yang.utils;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by yangshuang
 * on 2019/7/25.
 * <p>
 * copy from org.apache.lucene.util.RamUsageEstimator
 */
public class RamUsageEstimator {

    private static final EnumSet<JvmFeature> supportedFeatures;
    private static final Method objectFieldOffsetMethod;
    private static final Object theUnsafe;

    public static enum JvmFeature {
        OBJECT_REFERENCE_SIZE("Object reference size estimated using array index scale"), ARRAY_HEADER_SIZE("Array header size estimated using array based offset"), FIELD_OFFSETS("Shallow instance size based on field offsets"), OBJECT_ALIGNMENT("Object alignment retrieved from HotSpotDiagnostic MX bean");

        public final String description;

        private JvmFeature(String description) {
            this.description = description;
        }

        public String toString() {
            return super.name() + " (" + this.description + ")";
        }
    }

    private static final Map<Class<?>, Integer> primitiveSizes = new IdentityHashMap();
    public static final int NUM_BYTES_OBJECT_ALIGNMENT;
    public static final int NUM_BYTES_ARRAY_HEADER;
    public static final int NUM_BYTES_OBJECT_HEADER;
    public static final int NUM_BYTES_OBJECT_REF;
    public static final int NUM_BYTES_DOUBLE = 8;
    public static final int NUM_BYTES_LONG = 8;
    public static final int NUM_BYTES_FLOAT = 4;
    public static final int NUM_BYTES_INT = 4;
    public static final int NUM_BYTES_SHORT = 2;
    public static final int NUM_BYTES_CHAR = 2;
    public static final int NUM_BYTES_BYTE = 1;
    public static final int NUM_BYTES_BOOLEAN = 1;
    public static final long ONE_GB = 1073741824L;
    public static final long ONE_MB = 1048576L;
    public static final long ONE_KB = 1024L;

    static {
        primitiveSizes.put(Boolean.TYPE, Integer.valueOf(1));
        primitiveSizes.put(Byte.TYPE, Integer.valueOf(1));
        primitiveSizes.put(Character.TYPE, Integer.valueOf(2));
        primitiveSizes.put(Short.TYPE, Integer.valueOf(2));
        primitiveSizes.put(Integer.TYPE, Integer.valueOf(4));
        primitiveSizes.put(Float.TYPE, Integer.valueOf(4));
        primitiveSizes.put(Double.TYPE, Integer.valueOf(8));
        primitiveSizes.put(Long.TYPE, Integer.valueOf(8));

        int referenceSize = Constants.JRE_IS_64BIT ? 8 : 4;
        int objectHeader = Constants.JRE_IS_64BIT ? 16 : 8;
        int arrayHeader = Constants.JRE_IS_64BIT ? 24 : 12;

        supportedFeatures = EnumSet.noneOf(JvmFeature.class);

        Class<?> unsafeClass = null;
        Object tempTheUnsafe = null;
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            tempTheUnsafe = unsafeField.get(null);
        } catch (Exception e) {
        }
        theUnsafe = tempTheUnsafe;
        try {
            Method arrayIndexScaleM = unsafeClass.getMethod("arrayIndexScale", new Class[]{Class.class});
            referenceSize = ((Number) arrayIndexScaleM.invoke(theUnsafe, new Object[]{Object[].class})).intValue();
            supportedFeatures.add(JvmFeature.OBJECT_REFERENCE_SIZE);
        } catch (Exception e) {
        }
        objectHeader = Constants.JRE_IS_64BIT ? 8 + referenceSize : 8;
        arrayHeader = Constants.JRE_IS_64BIT ? 8 + 2 * referenceSize : 12;

        Method tempObjectFieldOffsetMethod = null;
        try {
            Method objectFieldOffsetM = unsafeClass.getMethod("objectFieldOffset", new Class[]{Field.class});
            Field dummy1Field = DummyTwoLongObject.class.getDeclaredField("dummy1");
            int ofs1 = ((Number) objectFieldOffsetM.invoke(theUnsafe, new Object[]{dummy1Field})).intValue();
            Field dummy2Field = DummyTwoLongObject.class.getDeclaredField("dummy2");
            int ofs2 = ((Number) objectFieldOffsetM.invoke(theUnsafe, new Object[]{dummy2Field})).intValue();
            if (Math.abs(ofs2 - ofs1) == 8) {
                Field baseField = DummyOneFieldObject.class.getDeclaredField("base");
                objectHeader = ((Number) objectFieldOffsetM.invoke(theUnsafe, new Object[]{baseField})).intValue();
                supportedFeatures.add(JvmFeature.FIELD_OFFSETS);
                tempObjectFieldOffsetMethod = objectFieldOffsetM;
            }
        } catch (Exception e) {
        }
        objectFieldOffsetMethod = tempObjectFieldOffsetMethod;
        try {
            Method arrayBaseOffsetM = unsafeClass.getMethod("arrayBaseOffset", new Class[]{Class.class});

            arrayHeader = ((Number) arrayBaseOffsetM.invoke(theUnsafe, new Object[]{byte[].class})).intValue();
            supportedFeatures.add(JvmFeature.ARRAY_HEADER_SIZE);
        } catch (Exception e) {
        }
        NUM_BYTES_OBJECT_REF = referenceSize;
        NUM_BYTES_OBJECT_HEADER = objectHeader;
        NUM_BYTES_ARRAY_HEADER = arrayHeader;

        int objectAlignment = 8;
        try {
            Class<?> beanClazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
            Object hotSpotBean = ManagementFactory.newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(), "com.sun.management:type=HotSpotDiagnostic", beanClazz);

            Method getVMOptionMethod = beanClazz.getMethod("getVMOption", new Class[]{String.class});
            Object vmOption = getVMOptionMethod.invoke(hotSpotBean, new Object[]{"ObjectAlignmentInBytes"});
            objectAlignment = Integer.parseInt(vmOption.getClass().getMethod("getValue", new Class[0]).invoke(vmOption, new Object[0]).toString());

            supportedFeatures.add(JvmFeature.OBJECT_ALIGNMENT);
        } catch (Exception e) {
        }
        NUM_BYTES_OBJECT_ALIGNMENT = objectAlignment;
    }

    public static final String JVM_INFO_STRING = "[JVM: " + Constants.JVM_NAME + ", " + Constants.JVM_VERSION + ", " + Constants.JVM_VENDOR + ", " + Constants.JAVA_VENDOR + ", " + Constants.JAVA_VERSION + "]";

    private static final class ClassCache {
        public final long alignedShallowInstanceSize;
        public final Field[] referenceFields;

        public ClassCache(long alignedShallowInstanceSize, Field[] referenceFields) {
            this.alignedShallowInstanceSize = alignedShallowInstanceSize;
            this.referenceFields = referenceFields;
        }
    }

    public static boolean isSupportedJVM() {
        return supportedFeatures.size() == JvmFeature.values().length;
    }

    public static long alignObjectSize(long size) {
        size += NUM_BYTES_OBJECT_ALIGNMENT - 1L;
        return size - size % NUM_BYTES_OBJECT_ALIGNMENT;
    }

    public static long sizeOf(byte[] arr) {
        return alignObjectSize(NUM_BYTES_ARRAY_HEADER + arr.length);
    }

    public static long sizeOf(boolean[] arr) {
        return alignObjectSize(NUM_BYTES_ARRAY_HEADER + arr.length);
    }

    public static long sizeOf(char[] arr) {
        return alignObjectSize(NUM_BYTES_ARRAY_HEADER + 2L * arr.length);
    }

    public static long sizeOf(short[] arr) {
        return alignObjectSize(NUM_BYTES_ARRAY_HEADER + 2L * arr.length);
    }

    public static long sizeOf(int[] arr) {
        return alignObjectSize(NUM_BYTES_ARRAY_HEADER + 4L * arr.length);
    }

    public static long sizeOf(float[] arr) {
        return alignObjectSize(NUM_BYTES_ARRAY_HEADER + 4L * arr.length);
    }

    public static long sizeOf(long[] arr) {
        return alignObjectSize(NUM_BYTES_ARRAY_HEADER + 8L * arr.length);
    }

    public static long sizeOf(double[] arr) {
        return alignObjectSize(NUM_BYTES_ARRAY_HEADER + 8L * arr.length);
    }

    public static long sizeOf(Object obj) {
        return measureObjectSize(obj);
    }

    public static long shallowSizeOf(Object obj) {
        if (obj == null) {
            return 0L;
        }
        Class<?> clz = obj.getClass();
        if (clz.isArray()) {
            return shallowSizeOfArray(obj);
        }
        return shallowSizeOfInstance(clz);
    }

    public static long shallowSizeOfInstance(Class<?> clazz) {
        if (clazz.isArray()) {
            throw new IllegalArgumentException("This method does not work with array classes.");
        }
        if (clazz.isPrimitive()) {
            return ((Integer) primitiveSizes.get(clazz)).intValue();
        }
        long size = NUM_BYTES_OBJECT_HEADER;
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    size = adjustForField(size, f);
                }
            }
        }
        return alignObjectSize(size);
    }

    private static long shallowSizeOfArray(Object array) {
        long size = NUM_BYTES_ARRAY_HEADER;
        int len = Array.getLength(array);
        if (len > 0) {
            Class<?> arrayElementClazz = array.getClass().getComponentType();
            if (arrayElementClazz.isPrimitive()) {
                size += len * ((Integer) primitiveSizes.get(arrayElementClazz)).intValue();
            } else {
                size += NUM_BYTES_OBJECT_REF * len;
            }
        }
        return alignObjectSize(size);
    }

    private static long measureObjectSize(Object root) {
        IdentityHashSet<Object> seen = new IdentityHashSet();

        IdentityHashMap<Class<?>, ClassCache> classCache = new IdentityHashMap();

        ArrayList<Object> stack = new ArrayList();
        stack.add(root);

        long totalSize = 0L;
        while (!stack.isEmpty()) {
            Object ob = stack.remove(stack.size() - 1);
            if ((ob != null) && (!seen.contains(ob))) {
                seen.add(ob);

                Class<?> obClazz = ob.getClass();
                if (obClazz.isArray()) {
                    long size = NUM_BYTES_ARRAY_HEADER;
                    int len = Array.getLength(ob);
                    if (len > 0) {
                        Class<?> componentClazz = obClazz.getComponentType();
                        if (componentClazz.isPrimitive()) {
                            size += len * ((Integer) primitiveSizes.get(componentClazz)).intValue();
                        } else {
                            size += NUM_BYTES_OBJECT_REF * len;

                            int i = len;
                            for (; ; ) {
                                i--;
                                if (i < 0) {
                                    break;
                                }
                                Object o = Array.get(ob, i);
                                if ((o != null) && (!seen.contains(o))) {
                                    stack.add(o);
                                }
                            }
                        }
                    }
                    totalSize += alignObjectSize(size);
                } else {
                    try {
                        ClassCache cachedInfo = (ClassCache) classCache.get(obClazz);
                        if (cachedInfo == null) {
                            classCache.put(obClazz, cachedInfo = createCacheEntry(obClazz));
                        }
                        for (Field f : cachedInfo.referenceFields) {
                            Object o = f.get(ob);
                            if ((o != null) && (!seen.contains(o))) {
                                stack.add(o);
                            }
                        }
                        totalSize += cachedInfo.alignedShallowInstanceSize;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Reflective field access failed?", e);
                    }
                }
            }
        }
        seen.clear();
        stack.clear();
        classCache.clear();

        return totalSize;
    }

    private static ClassCache createCacheEntry(Class<?> clazz) {
        long shallowInstanceSize = NUM_BYTES_OBJECT_HEADER;
        ArrayList<Field> referenceFields = new ArrayList(32);
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    shallowInstanceSize = adjustForField(shallowInstanceSize, f);
                    if (!f.getType().isPrimitive()) {
                        f.setAccessible(true);
                        referenceFields.add(f);
                    }
                }
            }
        }
        ClassCache cachedInfo = new ClassCache(alignObjectSize(shallowInstanceSize), (Field[]) referenceFields.toArray(new Field[referenceFields.size()]));

        return cachedInfo;
    }

    private static long adjustForField(long sizeSoFar, Field f) {
        Class<?> type = f.getType();
        int fsize = type.isPrimitive() ? ((Integer) primitiveSizes.get(type)).intValue() : NUM_BYTES_OBJECT_REF;
        if (objectFieldOffsetMethod != null) {
            try {
                long offsetPlusSize = ((Number) objectFieldOffsetMethod.invoke(theUnsafe, new Object[]{f})).longValue() + fsize;

                return Math.max(sizeSoFar, offsetPlusSize);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Access problem with sun.misc.Unsafe", ex);
            } catch (InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                if ((cause instanceof RuntimeException)) {
                    throw ((RuntimeException) cause);
                }
                if ((cause instanceof Error)) {
                    throw ((Error) cause);
                }
                throw new RuntimeException("Call to Unsafe's objectFieldOffset() throwed checked Exception when accessing field " + f.getDeclaringClass().getName() + "#" + f.getName(), cause);
            }
        }
        return sizeSoFar + fsize;
    }

    public static EnumSet<JvmFeature> getUnsupportedFeatures() {
        EnumSet<JvmFeature> unsupported = EnumSet.allOf(JvmFeature.class);
        unsupported.removeAll(supportedFeatures);
        return unsupported;
    }

    public static EnumSet<JvmFeature> getSupportedFeatures() {
        return EnumSet.copyOf(supportedFeatures);
    }

    public static String humanReadableUnits(long bytes) {
        return humanReadableUnits(bytes, new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT)));
    }

    public static String humanReadableUnits(long bytes, DecimalFormat df) {
        if (bytes / 1073741824L > 0L) {
            return df.format((float) bytes / 1.07374182E9F) + " GB";
        }
        if (bytes / 1048576L > 0L) {
            return df.format((float) bytes / 1048576.0F) + " MB";
        }
        if (bytes / 1024L > 0L) {
            return df.format((float) bytes / 1024.0F) + " KB";
        }
        return bytes + " bytes";
    }

    public static String humanSizeOf(Object object) {
        return humanReadableUnits(sizeOf(object));
    }

    private static final class DummyOneFieldObject {
        public byte base;
    }

    private static final class DummyTwoLongObject {
        public long dummy1;
        public long dummy2;
    }

    static final class IdentityHashSet<KType> implements Iterable<KType> {
        public static final float DEFAULT_LOAD_FACTOR = 0.75F;
        public static final int MIN_CAPACITY = 4;
        public Object[] keys;
        public int assigned;
        public final float loadFactor;
        private int resizeThreshold;

        public IdentityHashSet() {
            this(16, 0.75F);
        }

        public IdentityHashSet(int initialCapacity) {
            this(initialCapacity, 0.75F);
        }

        public IdentityHashSet(int initialCapacity, float loadFactor) {
            initialCapacity = Math.max(4, initialCapacity);

            assert (initialCapacity > 0) : "Initial capacity must be between (0, 2147483647].";

            assert ((loadFactor > 0.0F) && (loadFactor < 1.0F)) : "Load factor must be between (0, 1).";
            this.loadFactor = loadFactor;
            allocateBuffers(roundCapacity(initialCapacity));
        }

        public boolean add(KType e) {
            assert (e != null) : "Null keys not allowed.";
            if (this.assigned >= this.resizeThreshold) {
                expandAndRehash();
            }
            int mask = this.keys.length - 1;
            int slot = rehash(e) & mask;
            Object existing;
            while ((existing = this.keys[slot]) != null) {
                if (e == existing) {
                    return false;
                }
                slot = slot + 1 & mask;
            }
            this.assigned += 1;
            this.keys[slot] = e;
            return true;
        }

        public boolean contains(KType e) {
            int mask = this.keys.length - 1;
            int slot = rehash(e) & mask;
            Object existing;
            while ((existing = this.keys[slot]) != null) {
                if (e == existing) {
                    return true;
                }
                slot = slot + 1 & mask;
            }
            return false;
        }

        private static int rehash(Object o) {
            int k = System.identityHashCode(o);
            k ^= k >>> 16;
            k *= -2048144789;
            k ^= k >>> 13;
            k *= -1028477387;
            k ^= k >>> 16;
            return k;
        }

        private void expandAndRehash() {
            Object[] oldKeys = this.keys;

            assert (this.assigned >= this.resizeThreshold);
            allocateBuffers(nextCapacity(this.keys.length));

            int mask = this.keys.length - 1;
            for (int i = 0; i < oldKeys.length; i++) {
                Object key = oldKeys[i];
                if (key != null) {
                    int slot = rehash(key) & mask;
                    while (this.keys[slot] != null) {
                        slot = slot + 1 & mask;
                    }
                    this.keys[slot] = key;
                }
            }
            Arrays.fill(oldKeys, null);
        }

        private void allocateBuffers(int capacity) {
            this.keys = new Object[capacity];
            this.resizeThreshold = ((int) (capacity * 0.75F));
        }

        protected int nextCapacity(int current) {
            assert ((current > 0) && (Long.bitCount(current) == 1)) : "Capacity must be a power of two.";
            assert (current << 1 > 0) : "Maximum capacity exceeded (1073741824).";
            if (current < 2) {
                current = 2;
            }
            return current << 1;
        }

        protected int roundCapacity(int requestedCapacity) {
            if (requestedCapacity > 1073741824) {
                return 1073741824;
            }
            int capacity = 4;
            while (capacity < requestedCapacity) {
                capacity <<= 1;
            }
            return capacity;
        }

        public void clear() {
            this.assigned = 0;
            Arrays.fill(this.keys, null);
        }

        public int size() {
            return this.assigned;
        }

        public boolean isEmpty() {
            return size() == 0;
        }

        public Iterator<KType> iterator() {
            return new Iterator() {
                int pos = -1;
                Object nextElement = fetchNext();

                public boolean hasNext() {
                    return this.nextElement != null;
                }

                public KType next() {
                    Object r = this.nextElement;
                    if (r == null) {
                        throw new NoSuchElementException();
                    }
                    this.nextElement = fetchNext();
                    return (KType) r;
                }

                private Object fetchNext() {
                    this.pos += 1;
                    while ((this.pos < IdentityHashSet.this.keys.length) && (IdentityHashSet.this.keys[this.pos] == null)) {
                        this.pos += 1;
                    }
                    return this.pos >= IdentityHashSet.this.keys.length ? null : IdentityHashSet.this.keys[this.pos];
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}
