package unneeded;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

import javafx.beans.property.ObjectPropertyBase;

//https://ugate.wordpress.com/2012/06/06/javafx-pojo-bindings/

//Example:
/*PathProperty prop = new PathProperty(mainApp.addVideo, "title", String.class);
        PathProperty prop2 = new PathProperty(mainApp.addVideo, "cpu", String.class);
        PathProperty prop3 = new PathProperty(mainApp.addVideo, "year", Integer.class);
        PathProperty prop4 = new PathProperty(mainApp.addVideo, "category", String.class);
        //PathProperty prop5= new PathProperty(mainApp.addVideo, "text", String.class);
        Bindings.bindBidirectional(title.textProperty(), prop);
        Bindings.bindBidirectional(cpu.textProperty(), prop2);
        Bindings.bindBidirectional(year.textProperty(), prop3, new IntegerStringConverter());
        Bindings.bindBidirectional(category.valueProperty(), prop4);*/

public class PathProperty<B, T> extends ObjectPropertyBase<T> {

    private final String fieldPath;
    private PropertyMethodHandles propMethHandles;
    private final B bean;


    public PathProperty(final B bean, final String fieldPath, final Class<T> type) {
        super();
        this.bean = bean;
        this.fieldPath = fieldPath;
        try {
            this.propMethHandles = PropertyMethodHandles.build(getBean(), getName());
        } catch (final Throwable t) {
            throw new RuntimeException(String.format(
                    "Unable to instantiate expression %1$s on %2$s",
                    getBean(), getName()), t);
        }
    }

    @Override
    public void set(T v) {
        try {
            getPropMethHandles().getSetter().invoke(v);
            super.set(v);
        } catch (final Throwable t) {
            throw new RuntimeException("Unable to set value: " + v, t);
        }
    };

    @Override
    public T get() {
        try {
            return (T) getPropMethHandles().getAccessor().invoke();
            //return super.get();
        } catch (final Throwable t) {
            throw new RuntimeException("Unable to get value", t);
        }
    }

    @Override
    public B getBean() {
        return bean;
    }

    public PropertyMethodHandles getPropMethHandles() {
        return propMethHandles;
    }

    @Override
    public String getName() {
        return fieldPath;
    }

    public static class PropertyMethodHandles {

        private final MethodHandle accessor;
        private final MethodHandle setter;
        private Object setterArgument;

        protected PropertyMethodHandles(final Object target, final String fieldName,
                                        final boolean insertSetterArgument) throws NoSuchMethodException {
            this.accessor = buildGetter(target, fieldName);
            this.setter = buildSetter(getAccessor(), target, fieldName, insertSetterArgument);
        }

        public static PropertyMethodHandles build(final Object initialTarget,
                                                  final String expString) throws NoSuchMethodException, IllegalStateException {
            final String[] expStr = expString.split("\\.");
            Object target = initialTarget;
            PropertyMethodHandles pmh = null;
            for (int i = 0; i < expStr.length; i++) {
                pmh = new PropertyMethodHandles(target, expStr[i], i < (expStr.length - 1));
                target = pmh.getSetterArgument();
            }
            return pmh;
        }

        protected MethodHandle buildGetter(final Object target, final String fieldName)
                throws NoSuchMethodException {
            final MethodHandle mh = buildAccessor(target, fieldName, "get", "is", "has");
            if (mh == null) {
                throw new NoSuchMethodException(fieldName);
            }
            return mh;
        }

        protected MethodHandle buildSetter(final MethodHandle accessor,
                                           final Object target, final String fieldName,
                                           final boolean insertSetterArgument) {
            if (insertSetterArgument) {
                try {
                    setSetterArgument(accessor.invoke());
                } catch (final Throwable t) {
                    setSetterArgument(null);
                }
                if (getSetterArgument() == null) {
                    try {
                        setSetterArgument(accessor.type().returnType().newInstance());
                    } catch (final Exception e) {
                        throw new IllegalArgumentException(
                                String.format("Unable to build setter expression for %1$s using %2$s.",
                                        fieldName, accessor.type().returnType()));
                    }
                }
            }
            try {
                final MethodHandle mh1 = MethodHandles.lookup().findVirtual(target.getClass(),
                        buildMethodName("set", fieldName),
                        MethodType.methodType(void.class,
                                accessor.type().returnType())).bindTo(target);
                if (getSetterArgument() != null) {
//					final MethodHandle mh2 = MethodHandles.insertArguments(mh1, 0,
//							getSetterArgument());
                    mh1.invoke(getSetterArgument());
                    return mh1;
                }
                return mh1;
            } catch (final Throwable t) {
                throw new IllegalArgumentException("Unable to resolve setter "
                        + fieldName, t);
            }
        }

        protected static String buildMethodName(final String prefix,
                                                final String fieldName) {
            return (fieldName.startsWith(prefix) ? fieldName : prefix +
                    fieldName.substring(0, 1).toUpperCase() +
                    fieldName.substring(1));
        }

        protected static MethodHandle buildAccessor(final Object target,
                                                    final String fieldName, final String... fieldNamePrefix) {
            final String accessorName = buildMethodName(fieldNamePrefix[0], fieldName);
            try {
                return MethodHandles.lookup().findVirtual(target.getClass(), accessorName,
                        MethodType.methodType(
                                target.getClass().getMethod(
                                        accessorName).getReturnType())).bindTo(target);
            } catch (final NoSuchMethodException e) {
                return fieldNamePrefix.length <= 1 ? null :
                        buildAccessor(target, fieldName,
                                Arrays.copyOfRange(fieldNamePrefix, 1,
                                        fieldNamePrefix.length));
            } catch (final Throwable t) {
                throw new IllegalArgumentException(
                        "Unable to resolve accessor " + accessorName, t);
            }
        }

        /**
         * @return the getter
         */
        public MethodHandle getAccessor() {
            return accessor;
        }

        /**
         * @return the setter
         */
        public MethodHandle getSetter() {
            return setter;
        }

        public Object getSetterArgument() {
            return setterArgument;
        }

        public void setSetterArgument(final Object setterArgument) {
            this.setterArgument = setterArgument;
        }
    }
}