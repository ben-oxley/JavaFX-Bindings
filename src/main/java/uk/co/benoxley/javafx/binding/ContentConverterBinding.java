////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package uk.co.benoxley.javafx.binding;
//
//import java.lang.ref.WeakReference;
//import java.util.List;
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
//public class ContentConverterBinding {
//    public ContentConverterBinding() {
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
//    public static <A,B> Object bind(List<A> list1, ObservableList<? extends B> list2, ObjectConverter<A,B> converter) {
//        checkParameters(list1, list2);
//        ContentConverterBinding.ListContentBinding<E> contentBinding = new ContentConverterBinding.ListContentBinding(list1);
//        if (list1 instanceof ObservableList) {
//            ((ObservableList)list1).setAll(list2);
//        } else {
//            list1.clear();
//            list1.addAll(list2);
//        }
//
//        list2.removeListener(contentBinding);
//        list2.addListener(contentBinding);
//        return contentBinding;
//    }
//
//    public static <E> Object bind(Set<E> set1, ObservableSet<? extends E> set2) {
//        checkParameters(set1, set2);
//        ContentConverterBinding.SetContentBinding<E> contentBinding = new ContentConverterBinding.SetContentBinding(set1);
//        set1.clear();
//        set1.addAll(set2);
//        set2.removeListener(contentBinding);
//        set2.addListener(contentBinding);
//        return contentBinding;
//    }
//
//    public static <K, V> Object bind(Map<K, V> map1, ObservableMap<? extends K, ? extends V> map2) {
//        checkParameters(map1, map2);
//        ContentConverterBinding.MapContentBinding<K, V> contentBinding = new ContentConverterBinding.MapContentBinding(map1);
//        map1.clear();
//        map1.putAll(map2);
//        map2.removeListener(contentBinding);
//        map2.addListener(contentBinding);
//        return contentBinding;
//    }
//
//    public static void unbind(Object obj1, Object obj2) {
//        checkParameters(obj1, obj2);
//        if (obj1 instanceof List && obj2 instanceof ObservableList) {
//            ((ObservableList)obj2).removeListener(new ContentConverterBinding.ListContentBinding((List)obj1));
//        } else if (obj1 instanceof Set && obj2 instanceof ObservableSet) {
//            ((ObservableSet)obj2).removeListener(new ContentConverterBinding.SetContentBinding((Set)obj1));
//        } else if (obj1 instanceof Map && obj2 instanceof ObservableMap) {
//            ((ObservableMap)obj2).removeListener(new ContentConverterBinding.MapContentBinding((Map)obj1));
//        }
//
//    }
//
//    private static class MapContentBinding<K, V> implements MapChangeListener<K, V>, WeakListener {
//        private final WeakReference<Map<K, V>> mapRef;
//
//        public MapContentBinding(Map<K, V> map) {
//            this.mapRef = new WeakReference(map);
//        }
//
//        public void onChanged(Change<? extends K, ? extends V> change) {
//            Map<K, V> map = (Map)this.mapRef.get();
//            if (map == null) {
//                change.getMap().removeListener(this);
//            } else {
//                if (change.wasRemoved()) {
//                    map.remove(change.getKey());
//                }
//
//                if (change.wasAdded()) {
//                    map.put(change.getKey(), change.getValueAdded());
//                }
//            }
//
//        }
//
//        public boolean wasGarbageCollected() {
//            return this.mapRef.get() == null;
//        }
//
//        public int hashCode() {
//            Map<K, V> map = (Map)this.mapRef.get();
//            return map == null ? 0 : map.hashCode();
//        }
//
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            } else {
//                Map<K, V> map1 = (Map)this.mapRef.get();
//                if (map1 == null) {
//                    return false;
//                } else if (obj instanceof ContentConverterBinding.MapContentBinding) {
//                    ContentConverterBinding.MapContentBinding<?, ?> other = (ContentConverterBinding.MapContentBinding)obj;
//                    Map<?, ?> map2 = (Map)other.mapRef.get();
//                    return map1 == map2;
//                } else {
//                    return false;
//                }
//            }
//        }
//    }
//
//    private static class SetContentBinding<E> implements SetChangeListener<E>, WeakListener {
//        private final WeakReference<Set<E>> setRef;
//
//        public SetContentBinding(Set<E> set) {
//            this.setRef = new WeakReference(set);
//        }
//
//        public void onChanged(javafx.collections.SetChangeListener.Change<? extends E> change) {
//            Set<E> set = (Set)this.setRef.get();
//            if (set == null) {
//                change.getSet().removeListener(this);
//            } else if (change.wasRemoved()) {
//                set.remove(change.getElementRemoved());
//            } else {
//                set.add(change.getElementAdded());
//            }
//
//        }
//
//        public boolean wasGarbageCollected() {
//            return this.setRef.get() == null;
//        }
//
//        public int hashCode() {
//            Set<E> set = (Set)this.setRef.get();
//            return set == null ? 0 : set.hashCode();
//        }
//
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            } else {
//                Set<E> set1 = (Set)this.setRef.get();
//                if (set1 == null) {
//                    return false;
//                } else if (obj instanceof ContentConverterBinding.SetContentBinding) {
//                    ContentConverterBinding.SetContentBinding<?> other = (ContentConverterBinding.SetContentBinding)obj;
//                    Set<?> set2 = (Set)other.setRef.get();
//                    return set1 == set2;
//                } else {
//                    return false;
//                }
//            }
//        }
//    }
//
//    private static class ListContentBinding<E> implements ListChangeListener<E>, WeakListener {
//        private final WeakReference<List<E>> listRef;
//
//        public ListContentBinding(List<E> list) {
//            this.listRef = new WeakReference(list);
//        }
//
//        public void onChanged(javafx.collections.ListChangeListener.Change<? extends E> change) {
//            List<E> list = (List)this.listRef.get();
//            if (list == null) {
//                change.getList().removeListener(this);
//            } else {
//                while(change.next()) {
//                    if (change.wasPermutated()) {
//                        list.subList(change.getFrom(), change.getTo()).clear();
//                        list.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()));
//                    } else {
//                        if (change.wasRemoved()) {
//                            list.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
//                        }
//
//                        if (change.wasAdded()) {
//                            list.addAll(change.getFrom(), change.getAddedSubList());
//                        }
//                    }
//                }
//            }
//
//        }
//
//        public boolean wasGarbageCollected() {
//            return this.listRef.get() == null;
//        }
//
//        public int hashCode() {
//            List<E> list = (List)this.listRef.get();
//            return list == null ? 0 : list.hashCode();
//        }
//
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            } else {
//                List<E> list1 = (List)this.listRef.get();
//                if (list1 == null) {
//                    return false;
//                } else if (obj instanceof ContentConverterBinding.ListContentBinding) {
//                    ContentConverterBinding.ListContentBinding<?> other = (ContentConverterBinding.ListContentBinding)obj;
//                    List<?> list2 = (List)other.listRef.get();
//                    return list1 == list2;
//                } else {
//                    return false;
//                }
//            }
//        }
//    }
//}
