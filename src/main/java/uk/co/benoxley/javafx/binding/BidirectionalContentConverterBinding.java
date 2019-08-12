////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package uk.co.benoxley.javafx.binding;
//
//import java.lang.ref.WeakReference;
//import java.util.Map;
//import java.util.Set;
//import javafx.beans.WeakListener;
//import javafx.collections.ListChangeListener;
//import javafx.collections.MapChangeListener;
//import javafx.collections.ObservableList;
//import javafx.collections.ObservableMap;
//import javafx.collections.ObservableSet;
//import javafx.collections.SetChangeListener;
//import uk.co.benoxley.javafx.util.ObjectConverter;
//
//public class BidirectionalContentConverterBinding {
//    public BidirectionalContentConverterBinding() {
//    }
//
//    private static void checkParameters(Object property1, Object property2) {
//        if (property1 != null && property2 != null) {
//            if (property1 == property2) {
//                throw new IllegalArgumentException("Cannot bind object to itself");
//            }
//        } else {
//            throw new NullPointerException("Both parameters must be specified.");
//        }
//    }
//
//    public static <E> Object bind(ObservableList<E> list1, ObservableList<E> list2) {
//        checkParameters(list1, list2);
//        BidirectionalContentConverterBinding.ListContentBinding<E> binding = new BidirectionalContentConverterBinding.ListContentBinding(list1, list2);
//        list1.setAll(list2);
//        list1.addListener(binding);
//        list2.addListener(binding);
//        return binding;
//    }
//
//    public static <E> Object bind(ObservableSet<E> set1, ObservableSet<E> set2) {
//        checkParameters(set1, set2);
//        BidirectionalContentConverterBinding.SetContentBinding<E> binding = new BidirectionalContentConverterBinding.SetContentBinding(set1, set2);
//        set1.clear();
//        set1.addAll(set2);
//        set1.addListener(binding);
//        set2.addListener(binding);
//        return binding;
//    }
//
//    public static <K, V> Object bind(ObservableMap<K, V> map1, ObservableMap<K, V> map2) {
//        checkParameters(map1, map2);
//        BidirectionalContentConverterBinding.MapContentBinding<K, V> binding = new BidirectionalContentConverterBinding.MapContentBinding(map1, map2);
//        map1.clear();
//        map1.putAll(map2);
//        map1.addListener(binding);
//        map2.addListener(binding);
//        return binding;
//    }
//
//    public static void unbind(Object obj1, Object obj2) {
//        checkParameters(obj1, obj2);
//        if (obj1 instanceof ObservableList && obj2 instanceof ObservableList) {
//            ObservableList list1 = (ObservableList)obj1;
//            ObservableList list2 = (ObservableList)obj2;
//            BidirectionalContentConverterBinding.ListContentBinding binding = new BidirectionalContentConverterBinding.ListContentBinding(list1, list2);
//            list1.removeListener(binding);
//            list2.removeListener(binding);
//        } else if (obj1 instanceof ObservableSet && obj2 instanceof ObservableSet) {
//            ObservableSet set1 = (ObservableSet)obj1;
//            ObservableSet set2 = (ObservableSet)obj2;
//            BidirectionalContentConverterBinding.SetContentBinding binding = new BidirectionalContentConverterBinding.SetContentBinding(set1, set2);
//            set1.removeListener(binding);
//            set2.removeListener(binding);
//        } else if (obj1 instanceof ObservableMap && obj2 instanceof ObservableMap) {
//            ObservableMap map1 = (ObservableMap)obj1;
//            ObservableMap map2 = (ObservableMap)obj2;
//            BidirectionalContentConverterBinding.MapContentBinding binding = new BidirectionalContentConverterBinding.MapContentBinding(map1, map2);
//            map1.removeListener(binding);
//            map2.removeListener(binding);
//        }
//
//    }
//
//    private static class MapContentBinding<K, V> implements MapChangeListener<K, V>, WeakListener {
//        private final WeakReference<ObservableMap<K, V>> propertyRef1;
//        private final WeakReference<ObservableMap<K, V>> propertyRef2;
//        private boolean updating = false;
//
//        public MapContentBinding(ObservableMap<K, V> list1, ObservableMap<K, V> list2) {
//            this.propertyRef1 = new WeakReference(list1);
//            this.propertyRef2 = new WeakReference(list2);
//        }
//
//        public void onChanged(Change<? extends K, ? extends V> change) {
//            if (!this.updating) {
//                ObservableMap<K, V> map1 = (ObservableMap)this.propertyRef1.get();
//                ObservableMap<K, V> map2 = (ObservableMap)this.propertyRef2.get();
//                if (map1 != null && map2 != null) {
//                    try {
//                        this.updating = true;
//                        Map<K, V> dest = map1 == change.getMap() ? map2 : map1;
//                        if (change.wasRemoved()) {
//                            dest.remove(change.getKey());
//                        }
//
//                        if (change.wasAdded()) {
//                            dest.put(change.getKey(), change.getValueAdded());
//                        }
//                    } finally {
//                        this.updating = false;
//                    }
//                } else {
//                    if (map1 != null) {
//                        map1.removeListener(this);
//                    }
//
//                    if (map2 != null) {
//                        map2.removeListener(this);
//                    }
//                }
//            }
//
//        }
//
//        public boolean wasGarbageCollected() {
//            return this.propertyRef1.get() == null || this.propertyRef2.get() == null;
//        }
//
//        public int hashCode() {
//            ObservableMap<K, V> map1 = (ObservableMap)this.propertyRef1.get();
//            ObservableMap<K, V> map2 = (ObservableMap)this.propertyRef2.get();
//            int hc1 = map1 == null ? 0 : map1.hashCode();
//            int hc2 = map2 == null ? 0 : map2.hashCode();
//            return hc1 * hc2;
//        }
//
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            } else {
//                Object propertyA1 = this.propertyRef1.get();
//                Object propertyA2 = this.propertyRef2.get();
//                if (propertyA1 != null && propertyA2 != null) {
//                    if (obj instanceof BidirectionalContentConverterBinding.MapContentBinding) {
//                        BidirectionalContentConverterBinding.MapContentBinding otherBinding = (BidirectionalContentConverterBinding.MapContentBinding)obj;
//                        Object propertyB1 = otherBinding.propertyRef1.get();
//                        Object propertyB2 = otherBinding.propertyRef2.get();
//                        if (propertyB1 == null || propertyB2 == null) {
//                            return false;
//                        }
//
//                        if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
//                            return true;
//                        }
//
//                        if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
//                            return true;
//                        }
//                    }
//
//                    return false;
//                } else {
//                    return false;
//                }
//            }
//        }
//    }
//
//    private static class SetContentBinding<E> implements SetChangeListener<E>, WeakListener {
//        private final WeakReference<ObservableSet<E>> propertyRef1;
//        private final WeakReference<ObservableSet<E>> propertyRef2;
//        private boolean updating = false;
//
//        public SetContentBinding(ObservableSet<E> list1, ObservableSet<E> list2) {
//            this.propertyRef1 = new WeakReference(list1);
//            this.propertyRef2 = new WeakReference(list2);
//        }
//
//        public void onChanged(javafx.collections.SetChangeListener.Change<? extends E> change) {
//            if (!this.updating) {
//                ObservableSet<E> set1 = (ObservableSet)this.propertyRef1.get();
//                ObservableSet<E> set2 = (ObservableSet)this.propertyRef2.get();
//                if (set1 != null && set2 != null) {
//                    try {
//                        this.updating = true;
//                        Set<E> dest = set1 == change.getSet() ? set2 : set1;
//                        if (change.wasRemoved()) {
//                            dest.remove(change.getElementRemoved());
//                        } else {
//                            dest.add(change.getElementAdded());
//                        }
//                    } finally {
//                        this.updating = false;
//                    }
//                } else {
//                    if (set1 != null) {
//                        set1.removeListener(this);
//                    }
//
//                    if (set2 != null) {
//                        set2.removeListener(this);
//                    }
//                }
//            }
//
//        }
//
//        public boolean wasGarbageCollected() {
//            return this.propertyRef1.get() == null || this.propertyRef2.get() == null;
//        }
//
//        public int hashCode() {
//            ObservableSet<E> set1 = (ObservableSet)this.propertyRef1.get();
//            ObservableSet<E> set2 = (ObservableSet)this.propertyRef2.get();
//            int hc1 = set1 == null ? 0 : set1.hashCode();
//            int hc2 = set2 == null ? 0 : set2.hashCode();
//            return hc1 * hc2;
//        }
//
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            } else {
//                Object propertyA1 = this.propertyRef1.get();
//                Object propertyA2 = this.propertyRef2.get();
//                if (propertyA1 != null && propertyA2 != null) {
//                    if (obj instanceof BidirectionalContentConverterBinding.SetContentBinding) {
//                        BidirectionalContentConverterBinding.SetContentBinding otherBinding = (BidirectionalContentConverterBinding.SetContentBinding)obj;
//                        Object propertyB1 = otherBinding.propertyRef1.get();
//                        Object propertyB2 = otherBinding.propertyRef2.get();
//                        if (propertyB1 == null || propertyB2 == null) {
//                            return false;
//                        }
//
//                        if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
//                            return true;
//                        }
//
//                        if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
//                            return true;
//                        }
//                    }
//
//                    return false;
//                } else {
//                    return false;
//                }
//            }
//        }
//    }
//
//    private static class ListContentBinding<A,B> implements ListChangeListener<Object>, WeakListener {
//        private final WeakReference<ObservableList<A>> propertyRef1;
//        private final WeakReference<ObservableList<B>> propertyRef2;
//        private boolean updating = false;
//
//        public ListContentBinding(ObservableList<A> list1, ObservableList<B> list2, ObjectConverter<A,B> converter) {
//            this.propertyRef1 = new WeakReference(list1);
//            this.propertyRef2 = new WeakReference(list2);
//        }
//
//
//
//        public boolean wasGarbageCollected() {
//            return this.propertyRef1.get() == null || this.propertyRef2.get() == null;
//        }
//
//        public int hashCode() {
//            ObservableList<A> list1 = (ObservableList)this.propertyRef1.get();
//            ObservableList<B> list2 = (ObservableList)this.propertyRef2.get();
//            int hc1 = list1 == null ? 0 : list1.hashCode();
//            int hc2 = list2 == null ? 0 : list2.hashCode();
//            return hc1 * hc2;
//        }
//
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            } else {
//                Object propertyA1 = this.propertyRef1.get();
//                Object propertyA2 = this.propertyRef2.get();
//                if (propertyA1 != null && propertyA2 != null) {
//                    if (obj instanceof BidirectionalContentConverterBinding.ListContentBinding) {
//                        BidirectionalContentConverterBinding.ListContentBinding otherBinding = (BidirectionalContentConverterBinding.ListContentBinding)obj;
//                        Object propertyB1 = otherBinding.propertyRef1.get();
//                        Object propertyB2 = otherBinding.propertyRef2.get();
//                        if (propertyB1 == null || propertyB2 == null) {
//                            return false;
//                        }
//
//                        if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
//                            return true;
//                        }
//
//                        if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
//                            return true;
//                        }
//                    }
//
//                    return false;
//                } else {
//                    return false;
//                }
//            }
//        }
//
//        @Override
//        public void onChanged(Change<?> change) {
//            if (!this.updating) {
//                ObservableList<A> list1 = (ObservableList)this.propertyRef1.get();
//                ObservableList<B> list2 = (ObservableList)this.propertyRef2.get();
//                if (list1 != null && list2 != null) {
//                    try {
//                        this.updating = true;
//                        ObservableList dest = list1 == change.getList() ? list2 : list1;
//
//                        while(change.next()) {
//                            if (change.wasPermutated()) {
//                                dest.remove(change.getFrom(), change.getTo());
//                                dest.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()));
//                            } else {
//                                if (change.wasRemoved()) {
//                                    dest.remove(change.getFrom(), change.getFrom() + change.getRemovedSize());
//                                }
//
//                                if (change.wasAdded()) {
//                                    dest.addAll(change.getFrom(), change.getAddedSubList());
//                                }
//                            }
//                        }
//                    } finally {
//                        this.updating = false;
//                    }
//                } else {
//                    if (list1 != null) {
//                        list1.removeListener(this);
//                    }
//
//                    if (list2 != null) {
//                        list2.removeListener(this);
//                    }
//                }
//            }
//
//        }
//    }
//}
