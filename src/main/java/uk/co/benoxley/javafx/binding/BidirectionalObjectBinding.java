/*
 * Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
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

package uk.co.benoxley.javafx.binding;

import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import uk.co.benoxley.javafx.util.ObjectConverter;
import javafx.util.StringConverter;

import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.ParseException;
import java.util.logging.Level;

public abstract class BidirectionalObjectBinding<T> implements ChangeListener<T>, WeakListener {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(BidirectionalObjectBinding.class.getName());

    private static void checkParameters(Object property1, Object property2) {
        if ((property1 == null) || (property2 == null)) {
            throw new NullPointerException("Both properties must be specified.");
        }
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind property to itself");
        }
    }

    public static <T> BidirectionalObjectBinding bind(Property<T> property1, Property<T> property2) {
        checkParameters(property1, property2);
        final BidirectionalObjectBinding binding =
                ((property1 instanceof DoubleProperty) && (property2 instanceof DoubleProperty)) ?
                        new BidirectionalDoubleBinding((DoubleProperty) property1, (DoubleProperty) property2)
                : ((property1 instanceof FloatProperty) && (property2 instanceof FloatProperty)) ?
                        new BidirectionalFloatBinding((FloatProperty) property1, (FloatProperty) property2)
                : ((property1 instanceof IntegerProperty) && (property2 instanceof IntegerProperty)) ?
                        new BidirectionalIntegerBinding((IntegerProperty) property1, (IntegerProperty) property2)
                : ((property1 instanceof LongProperty) && (property2 instanceof LongProperty)) ?
                        new BidirectionalLongBinding((LongProperty) property1, (LongProperty) property2)
                : ((property1 instanceof BooleanProperty) && (property2 instanceof BooleanProperty)) ?
                        new BidirectionalBooleanBinding((BooleanProperty) property1, (BooleanProperty) property2)
                : new TypedGenericBidirectionalObjectBinding<T>(property1, property2);
        property1.setValue(property2.getValue());
        property1.addListener(binding);
        property2.addListener(binding);
        return binding;
    }

    public static Object bind(Property<String> stringProperty, Property<?> otherProperty, Format format) {
        checkParameters(stringProperty, otherProperty);
        if (format == null) {
            throw new NullPointerException("Format cannot be null");
        }
        final StringConversionBidirectionalObjectBinding<?> binding = new StringFormatBidirectionalObjectBinding(stringProperty, otherProperty, format);
        stringProperty.setValue(format.format(otherProperty.getValue()));
        stringProperty.addListener(binding);
        otherProperty.addListener(binding);
        return binding;
    }

    public static <T> Object bind(Property<String> stringProperty, Property<T> otherProperty, StringConverter<T> converter) {
        checkParameters(stringProperty, otherProperty);
        if (converter == null) {
            throw new NullPointerException("Converter cannot be null");
        }
        final StringConversionBidirectionalObjectBinding<T> binding = new StringConverterBidirectionalObjectBinding<T>(stringProperty, otherProperty, converter);
        stringProperty.setValue(converter.toString(otherProperty.getValue()));
        stringProperty.addListener(binding);
        otherProperty.addListener(binding);
        return binding;
    }

    public static <T> void unbind(Property<T> property1, Property<T> property2) {
        checkParameters(property1, property2);
        final BidirectionalObjectBinding binding = new UntypedGenericBidirectionalObjectBinding(property1, property2);
        property1.removeListener(binding);
        property2.removeListener(binding);
    }

    public static void unbind(Object property1, Object property2) {
        checkParameters(property1, property2);
        final BidirectionalObjectBinding binding = new UntypedGenericBidirectionalObjectBinding(property1, property2);
        if (property1 instanceof ObservableValue) {
            ((ObservableValue) property1).removeListener(binding);
        }
        if (property2 instanceof Observable) {
            ((ObservableValue) property2).removeListener(binding);
        }
    }

    public static BidirectionalObjectBinding bindNumber(Property<Integer> property1, IntegerProperty property2) {
        return bindNumber(property1, (Property<Number>)property2);
    }

    public static BidirectionalObjectBinding bindNumber(Property<Long> property1, LongProperty property2) {
        return bindNumber(property1, (Property<Number>)property2);
    }

    public static BidirectionalObjectBinding bindNumber(Property<Float> property1, FloatProperty property2) {
        return bindNumber(property1, (Property<Number>)property2);
    }

    public static BidirectionalObjectBinding bindNumber(Property<Double> property1, DoubleProperty property2) {
        return bindNumber(property1, (Property<Number>)property2);
    }

    public static BidirectionalObjectBinding bindNumber(IntegerProperty property1, Property<Integer> property2) {
        return bindNumberObject(property1, property2);
    }

    public static BidirectionalObjectBinding bindNumber(LongProperty property1, Property<Long> property2) {
        return bindNumberObject(property1, property2);
    }

    public static BidirectionalObjectBinding bindNumber(FloatProperty property1, Property<Float> property2) {
        return bindNumberObject(property1, property2);
    }

    public static BidirectionalObjectBinding bindNumber(DoubleProperty property1, Property<Double> property2) {
        return bindNumberObject(property1, property2);
    }

    private static <T extends Number> BidirectionalObjectBinding bindNumberObject(Property<Number> property1, Property<T> property2) {
        checkParameters(property1, property2);

        final BidirectionalObjectBinding<Number> binding = new TypedNumberBidirectionalObjectBinding<T>(property2, property1);

        property1.setValue(property2.getValue());
        property1.addListener(binding);
        property2.addListener(binding);
        return binding;
    }

    private static <T extends Number> BidirectionalObjectBinding bindNumber(Property<T> property1, Property<Number> property2) {
        checkParameters(property1, property2);

        final BidirectionalObjectBinding<Number> binding = new TypedNumberBidirectionalObjectBinding<T>(property1, property2);

        property1.setValue((T)property2.getValue());
        property1.addListener(binding);
        property2.addListener(binding);
        return binding;
    }

    public static <T extends Number> void unbindNumber(Property<T> property1, Property<Number> property2) {
        checkParameters(property1, property2);
        final BidirectionalObjectBinding binding = new UntypedGenericBidirectionalObjectBinding(property1, property2);
        if (property1 instanceof ObservableValue) {
            ((ObservableValue) property1).removeListener(binding);
        }
        if (property2 instanceof Observable) {
            ((ObservableValue) property2).removeListener(binding);
        }
    }

    private final int cachedHashCode;

    private BidirectionalObjectBinding(Object property1, Object property2) {
        cachedHashCode = property1.hashCode() * property2.hashCode();
    }

    protected abstract Object getProperty1();

    protected abstract Object getProperty2();

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public boolean wasGarbageCollected() {
        return (getProperty1() == null) || (getProperty2() == null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final Object propertyA1 = getProperty1();
        final Object propertyA2 = getProperty2();
        if ((propertyA1 == null) || (propertyA2 == null)) {
            return false;
        }

        if (obj instanceof BidirectionalObjectBinding) {
            final BidirectionalObjectBinding otherBinding = (BidirectionalObjectBinding) obj;
            final Object propertyB1 = otherBinding.getProperty1();
            final Object propertyB2 = otherBinding.getProperty2();
            if ((propertyB1 == null) || (propertyB2 == null)) {
                return false;
            }

            if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
                return true;
            }
            if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
                return true;
            }
        }
        return false;
    }

    private static class BidirectionalBooleanBinding extends BidirectionalObjectBinding<Boolean> {
        private final WeakReference<BooleanProperty> propertyRef1;
        private final WeakReference<BooleanProperty> propertyRef2;
        private boolean updating = false;

        private BidirectionalBooleanBinding(BooleanProperty property1, BooleanProperty property2) {
            super(property1, property2);
            propertyRef1 = new WeakReference<BooleanProperty>(property1);
            propertyRef2 = new WeakReference<BooleanProperty>(property2);
        }

        @Override
        protected Property<Boolean> getProperty1() {
            return propertyRef1.get();
        }

        @Override
        protected Property<Boolean> getProperty2() {
            return propertyRef2.get();
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> sourceProperty, Boolean oldValue, Boolean newValue) {
            if (!updating) {
                final BooleanProperty property1 = propertyRef1.get();
                final BooleanProperty property2 = propertyRef2.get();
                if ((property1 == null) || (property2 == null)) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (property1 == sourceProperty) {
                            property2.set(newValue);
                        } else {
                            property1.set(newValue);
                        }
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.set(oldValue);
                            } else {
                                property2.set(oldValue);
                            }
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind(property1, property2);
                            throw new RuntimeException(
                                "Bidirectional binding failed together with an attempt"
                                        + " to restore the source property to the previous value."
                                        + " Removing the bidirectional binding from properties " +
                                        property1 + " and " + property2, e2);
                        }
                        throw new RuntimeException(
                                "Bidirectional binding failed, setting to the previous value", e);
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    private static class BidirectionalDoubleBinding extends BidirectionalObjectBinding<Number> {
        private final WeakReference<DoubleProperty> propertyRef1;
        private final WeakReference<DoubleProperty> propertyRef2;
        private boolean updating = false;

        private BidirectionalDoubleBinding(DoubleProperty property1, DoubleProperty property2) {
            super(property1, property2);
            propertyRef1 = new WeakReference<DoubleProperty>(property1);
            propertyRef2 = new WeakReference<DoubleProperty>(property2);
        }

        @Override
        protected Property<Number> getProperty1() {
            return propertyRef1.get();
        }

        @Override
        protected Property<Number> getProperty2() {
            return propertyRef2.get();
        }

        @Override
        public void changed(ObservableValue<? extends Number> sourceProperty, Number oldValue, Number newValue) {
            if (!updating) {
                final DoubleProperty property1 = propertyRef1.get();
                final DoubleProperty property2 = propertyRef2.get();
                if ((property1 == null) || (property2 == null)) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (property1 == sourceProperty) {
                            property2.set(newValue.doubleValue());
                        } else {
                            property1.set(newValue.doubleValue());
                        }
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.set(oldValue.doubleValue());
                            } else {
                                property2.set(oldValue.doubleValue());
                            }
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind(property1, property2);
                            throw new RuntimeException(
                                "Bidirectional binding failed together with an attempt"
                                        + " to restore the source property to the previous value."
                                        + " Removing the bidirectional binding from properties " +
                                        property1 + " and " + property2, e2);
                        }
                        throw new RuntimeException(
                                        "Bidirectional binding failed, setting to the previous value", e);
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    private static class BidirectionalFloatBinding extends BidirectionalObjectBinding<Number> {
        private final WeakReference<FloatProperty> propertyRef1;
        private final WeakReference<FloatProperty> propertyRef2;
        private boolean updating = false;

        private BidirectionalFloatBinding(FloatProperty property1, FloatProperty property2) {
            super(property1, property2);
            propertyRef1 = new WeakReference<FloatProperty>(property1);
            propertyRef2 = new WeakReference<FloatProperty>(property2);
        }

        @Override
        protected Property<Number> getProperty1() {
            return propertyRef1.get();
        }

        @Override
        protected Property<Number> getProperty2() {
            return propertyRef2.get();
        }

        @Override
        public void changed(ObservableValue<? extends Number> sourceProperty, Number oldValue, Number newValue) {
            if (!updating) {
                final FloatProperty property1 = propertyRef1.get();
                final FloatProperty property2 = propertyRef2.get();
                if ((property1 == null) || (property2 == null)) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (property1 == sourceProperty) {
                            property2.set(newValue.floatValue());
                        } else {
                            property1.set(newValue.floatValue());
                        }
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.set(oldValue.floatValue());
                            } else {
                                property2.set(oldValue.floatValue());
                            }
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind(property1, property2);
                            throw new RuntimeException(
                                "Bidirectional binding failed together with an attempt"
                                        + " to restore the source property to the previous value."
                                        + " Removing the bidirectional binding from properties " +
                                        property1 + " and " + property2, e2);
                        }
                        throw new RuntimeException(
                                "Bidirectional binding failed, setting to the previous value", e);
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    private static class BidirectionalIntegerBinding extends BidirectionalObjectBinding<Number>{
        private final WeakReference<IntegerProperty> propertyRef1;
        private final WeakReference<IntegerProperty> propertyRef2;
        private boolean updating = false;

        private BidirectionalIntegerBinding(IntegerProperty property1, IntegerProperty property2) {
            super(property1, property2);
            propertyRef1 = new WeakReference<IntegerProperty>(property1);
            propertyRef2 = new WeakReference<IntegerProperty>(property2);
        }

        @Override
        protected Property<Number> getProperty1() {
            return propertyRef1.get();
        }

        @Override
        protected Property<Number> getProperty2() {
            return propertyRef2.get();
        }

        @Override
        public void changed(ObservableValue<? extends Number> sourceProperty, Number oldValue, Number newValue) {
            if (!updating) {
                final IntegerProperty property1 = propertyRef1.get();
                final IntegerProperty property2 = propertyRef2.get();
                if ((property1 == null) || (property2 == null)) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (property1 == sourceProperty) {
                            property2.set(newValue.intValue());
                        } else {
                            property1.set(newValue.intValue());
                        }
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.set(oldValue.intValue());
                            } else {
                                property2.set(oldValue.intValue());
                            }
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind(property1, property2);
                            throw new RuntimeException(
                                "Bidirectional binding failed together with an attempt"
                                        + " to restore the source property to the previous value."
                                        + " Removing the bidirectional binding from properties " +
                                        property1 + " and " + property2, e2);
                        }
                        throw new RuntimeException(
                                        "Bidirectional binding failed, setting to the previous value", e);
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    private static class BidirectionalLongBinding extends BidirectionalObjectBinding<Number> {
        private final WeakReference<LongProperty> propertyRef1;
        private final WeakReference<LongProperty> propertyRef2;
        private boolean updating = false;

        private BidirectionalLongBinding(LongProperty property1, LongProperty property2) {
            super(property1, property2);
            propertyRef1 = new WeakReference<LongProperty>(property1);
            propertyRef2 = new WeakReference<LongProperty>(property2);
        }

        @Override
        protected Property<Number> getProperty1() {
            return propertyRef1.get();
        }

        @Override
        protected Property<Number> getProperty2() {
            return propertyRef2.get();
        }

        @Override
        public void changed(ObservableValue<? extends Number> sourceProperty, Number oldValue, Number newValue) {
            if (!updating) {
                final LongProperty property1 = propertyRef1.get();
                final LongProperty property2 = propertyRef2.get();
                if ((property1 == null) || (property2 == null)) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (property1 == sourceProperty) {
                            property2.set(newValue.longValue());
                        } else {
                            property1.set(newValue.longValue());
                        }
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.set(oldValue.longValue());
                            } else {
                                property2.set(oldValue.longValue());
                            }
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind(property1, property2);
                            throw new RuntimeException(
                                "Bidirectional binding failed together with an attempt"
                                        + " to restore the source property to the previous value."
                                        + " Removing the bidirectional binding from properties " +
                                        property1 + " and " + property2, e2);
                        }
                        throw new RuntimeException(
                                "Bidirectional binding failed, setting to the previous value", e);
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    private static class TypedGenericBidirectionalObjectBinding<T> extends BidirectionalObjectBinding<T> {
        private final WeakReference<Property<T>> propertyRef1;
        private final WeakReference<Property<T>> propertyRef2;
        private boolean updating = false;

        private TypedGenericBidirectionalObjectBinding(Property<T> property1, Property<T> property2) {
            super(property1, property2);
            propertyRef1 = new WeakReference<Property<T>>(property1);
            propertyRef2 = new WeakReference<Property<T>>(property2);
        }

        @Override
        protected Property<T> getProperty1() {
            return propertyRef1.get();
        }

        @Override
        protected Property<T> getProperty2() {
            return propertyRef2.get();
        }

        @Override
        public void changed(ObservableValue<? extends T> sourceProperty, T oldValue, T newValue) {
            if (!updating) {
                final Property<T> property1 = propertyRef1.get();
                final Property<T> property2 = propertyRef2.get();
                if ((property1 == null) || (property2 == null)) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (property1 == sourceProperty) {
                            property2.setValue(newValue);
                        } else {
                            property1.setValue(newValue);
                        }
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.setValue(oldValue);
                            } else {
                                property2.setValue(oldValue);
                            }
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind(property1, property2);
                            throw new RuntimeException(
                                "Bidirectional binding failed together with an attempt"
                                        + " to restore the source property to the previous value."
                                        + " Removing the bidirectional binding from properties " +
                                        property1 + " and " + property2, e2);
                        }
                        throw new RuntimeException(
                                "Bidirectional binding failed, setting to the previous value", e);
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    private static class TypedNumberBidirectionalObjectBinding<T extends Number> extends BidirectionalObjectBinding<Number> {
        private final WeakReference<Property<T>> propertyRef1;
        private final WeakReference<Property<Number>> propertyRef2;
        private boolean updating = false;

        private TypedNumberBidirectionalObjectBinding(Property<T> property1, Property<Number> property2) {
            super(property1, property2);
            propertyRef1 = new WeakReference<Property<T>>(property1);
            propertyRef2 = new WeakReference<Property<Number>>(property2);
        }

        @Override
        protected Property<T> getProperty1() {
            return propertyRef1.get();
        }

        @Override
        protected Property<Number> getProperty2() {
            return propertyRef2.get();
        }

        @Override
        public void changed(ObservableValue<? extends Number> sourceProperty, Number oldValue, Number newValue) {
            if (!updating) {
                final Property<T> property1 = propertyRef1.get();
                final Property<Number> property2 = propertyRef2.get();
                if ((property1 == null) || (property2 == null)) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (property1 == sourceProperty) {
                            property2.setValue(newValue);
                        } else {
                            property1.setValue((T)newValue);
                        }
                    } catch (RuntimeException e) {
                        try {
                            if (property1 == sourceProperty) {
                                property1.setValue((T)oldValue);
                            } else {
                                property2.setValue(oldValue);
                            }
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            unbind(property1, property2);
                            throw new RuntimeException(
                                "Bidirectional binding failed together with an attempt"
                                        + " to restore the source property to the previous value."
                                        + " Removing the bidirectional binding from properties " +
                                        property1 + " and " + property2, e2);
                        }
                        throw new RuntimeException(
                                        "Bidirectional binding failed, setting to the previous value", e);
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    private static class UntypedGenericBidirectionalObjectBinding extends BidirectionalObjectBinding<Object> {

        private final Object property1;
        private final Object property2;

        public UntypedGenericBidirectionalObjectBinding(Object property1, Object property2) {
            super(property1, property2);
            this.property1 = property1;
            this.property2 = property2;
        }

        @Override
        protected Object getProperty1() {
            return property1;
        }

        @Override
        protected Object getProperty2() {
            return property2;
        }

        @Override
        public void changed(ObservableValue<? extends Object> sourceProperty, Object oldValue, Object newValue) {
            throw new RuntimeException("Should not reach here");
        }
    }

    public abstract static class StringConversionBidirectionalObjectBinding<T> extends BidirectionalObjectBinding<Object> {

        private final WeakReference<Property<String>> stringPropertyRef;
        private final WeakReference<Property<T>> otherPropertyRef;
        private boolean updating;

        public StringConversionBidirectionalObjectBinding(Property<String> stringProperty, Property<T> otherProperty) {
            super(stringProperty, otherProperty);
            stringPropertyRef = new WeakReference<Property<String>>(stringProperty);
            otherPropertyRef = new WeakReference<Property<T>>(otherProperty);
        }

        protected abstract String toString(T value);

        protected abstract T fromString(String value) throws ParseException;

        @Override
        protected Object getProperty1() {
            return stringPropertyRef.get();
        }

        @Override
        protected Object getProperty2() {
            return otherPropertyRef.get();
        }

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            if (!updating) {
                final Property<String> property1 = stringPropertyRef.get();
                final Property<T> property2 = otherPropertyRef.get();
                if ((property1 == null) || (property2 == null)) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (property1 == observable) {
                            try {
                                property2.setValue(fromString(property1.getValue()));
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING,"Exception while parsing String in bidirectional binding", e);
                                property2.setValue(null);
                            }
                        } else {
                            try {
                                property1.setValue(toString(property2.getValue()));
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING,"Exception while converting Object to String in bidirectional binding", e);
                                property1.setValue("");
                            }
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    private static class StringFormatBidirectionalObjectBinding extends StringConversionBidirectionalObjectBinding {

        private final Format format;

        @SuppressWarnings("unchecked")
        public StringFormatBidirectionalObjectBinding(Property<String> stringProperty, Property<?> otherProperty, Format format) {
            super(stringProperty, otherProperty);
            this.format = format;
        }

        @Override
        protected String toString(Object value) {
            return format.format(value);
        }

        @Override
        protected Object fromString(String value) throws ParseException {
            return format.parseObject(value);
        }
    }

    private static class StringConverterBidirectionalObjectBinding<T> extends StringConversionBidirectionalObjectBinding<T> {

        private final StringConverter<T> converter;

        public StringConverterBidirectionalObjectBinding(Property<String> stringProperty, Property<T> otherProperty, StringConverter<T> converter) {
            super(stringProperty, otherProperty);
            this.converter = converter;
        }

        @Override
        protected String toString(T value) {
            return converter.toString(value);
        }

        @Override
        protected T fromString(String value) throws ParseException {
            return converter.fromString(value);
        }
    }

    public abstract static class ObjectConversionBidirectionalObjectBinding<A,B> extends BidirectionalObjectBinding<Object> {

        private final WeakReference<Property<A>> stringPropertyRef;
        private final WeakReference<Property<B>> otherPropertyRef;
        private boolean updating;

        public ObjectConversionBidirectionalObjectBinding(Property<A> stringProperty, Property<B> otherProperty) {
            super(stringProperty, otherProperty);
            stringPropertyRef = new WeakReference<Property<A>>(stringProperty);
            otherPropertyRef = new WeakReference<Property<B>>(otherProperty);
        }

        protected abstract A toA(B value);

        protected abstract B toB(A value);

        @Override
        protected Object getProperty1() {
            return stringPropertyRef.get();
        }

        @Override
        protected Object getProperty2() {
            return otherPropertyRef.get();
        }

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            if (!updating) {
                final Property<A> property1 = stringPropertyRef.get();
                final Property<B> property2 = otherPropertyRef.get();
                if ((property1 == null) || (property2 == null)) {
                    if (property1 != null) {
                        property1.removeListener(this);
                    }
                    if (property2 != null) {
                        property2.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (property1 == observable) {
                            try {
                                property2.setValue(toB(property1.getValue()));
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING,"Exception while parsing String in bidirectional binding", e);
                                property2.setValue(null);
                            }
                        } else {
                            try {
                                property1.setValue(toA(property2.getValue()));
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING,"Exception while converting Object to String in bidirectional binding", e);
                                property1.setValue(null);
                            }
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        }
    }

    private static class ObjectConverterBidirectionalObjectBinding<A,B> extends ObjectConversionBidirectionalObjectBinding<A,B> {

        private final ObjectConverter<A,B> converter;

        public ObjectConverterBidirectionalObjectBinding(Property<A> propertyA, Property<B> propertyB, ObjectConverter<A,B> converter) {
            super(propertyA, propertyB);
            this.converter = converter;
        }

        @Override
        protected A toA(B value) {
            return converter.toA(value);
        }

        @Override
        protected B toB(A value) {
            return converter.toB(value);
        }
    }
}
