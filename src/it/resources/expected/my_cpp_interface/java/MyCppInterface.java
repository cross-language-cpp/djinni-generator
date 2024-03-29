// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_cpp_interface.djinni

package djinni.it;

import java.util.concurrent.atomic.AtomicBoolean;

/** interface comment */
public abstract class MyCppInterface {
    /** Interfaces can also have constants */
    public static final int VERSION = 1;

    /** method comment */
    public abstract void methodReturningNothing(int value);

    public abstract int methodReturningSomeType(String key);

    public abstract int methodChangingNothing();

    public static int getVersion()
    {
        return CppProxy.getVersion();
    }

    private static final class CppProxy extends MyCppInterface
    {
        private final long nativeRef;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);

        private CppProxy(long nativeRef)
        {
            if (nativeRef == 0) throw new RuntimeException("nativeRef is zero");
            this.nativeRef = nativeRef;
        }

        private native void nativeDestroy(long nativeRef);
        public void _djinni_private_destroy()
        {
            boolean destroyed = this.destroyed.getAndSet(true);
            if (!destroyed) nativeDestroy(this.nativeRef);
        }
        @SuppressWarnings("deprecation")
        protected void finalize() throws java.lang.Throwable
        {
            _djinni_private_destroy();
            super.finalize();
        }

        @Override
        public void methodReturningNothing(int value)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            native_methodReturningNothing(this.nativeRef, value);
        }
        private native void native_methodReturningNothing(long _nativeRef, int value);

        @Override
        public int methodReturningSomeType(String key)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_methodReturningSomeType(this.nativeRef, key);
        }
        private native int native_methodReturningSomeType(long _nativeRef, String key);

        @Override
        public int methodChangingNothing()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_methodChangingNothing(this.nativeRef);
        }
        private native int native_methodChangingNothing(long _nativeRef);

        public static native int getVersion();
    }
}
