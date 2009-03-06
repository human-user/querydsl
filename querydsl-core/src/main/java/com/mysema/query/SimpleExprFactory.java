/*
 * Copyright (c) 2008 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.LazyMap;
import org.apache.commons.lang.StringUtils;

import com.mysema.query.grammar.types.Expr;
import com.mysema.query.grammar.types.PathMetadata;
import com.mysema.query.grammar.types.Expr.EBoolean;
import com.mysema.query.grammar.types.Expr.EComparable;
import com.mysema.query.grammar.types.Expr.ENumber;
import com.mysema.query.grammar.types.ExtTypes.ExtString;
import com.mysema.query.grammar.types.Path.*;

/**
 * SimpleExprFactory is a factory implementation for the creation of Path instances
 *
 * @author tiwe
 * @version $Id$
 */
public class SimpleExprFactory implements ExprFactory {
    
    private final ExtString strExt = new ExtString(PathMetadata.forVariable("str"));
    
    private final PBoolean btrue = new PBoolean(md()), bfalse = new PBoolean(md());
    
    private long counter = 0;
    
    private final Map<Object,PBooleanArray> baToPath = new PathFactory<Object,PBooleanArray>(new Transformer<Object,PBooleanArray>(){
        @SuppressWarnings("unchecked")
        public PBooleanArray transform(Object arg) {
            return new PBooleanArray(md());
        }    
    });
    
    private final Map<Object,PComparableArray<?>> caToPath = new PathFactory<Object,PComparableArray<?>>(new Transformer<Object,PComparableArray<?>>(){
        @SuppressWarnings("unchecked")
        public PComparableArray<?> transform(Object arg) {
            return new PComparableArray(((List)arg).get(0).getClass(), md());
        }    
    });
    
    private final Map<Collection<?>,PEntityCollection<?>> ecToPath = new PathFactory<Collection<?>,PEntityCollection<?>>(new Transformer<Collection<?>,PEntityCollection<?>>(){
        @SuppressWarnings("unchecked")
        public PEntityCollection<?> transform(Collection<?> arg) {
            if (!arg.isEmpty()){
                Class<?> cl = ((Collection)arg).iterator().next().getClass();
                return new PEntityCollection(cl, cl.getSimpleName(), md());    
            }else{
                return new PEntityCollection(null, null, md());
            }            
        }    
    });
        
    private final Map<List<?>,PEntityList<?>> elToPath = new PathFactory<List<?>,PEntityList<?>>(new Transformer<List<?>,PEntityList<?>>(){
        @SuppressWarnings("unchecked")
        public PEntityList<?> transform(List<?> arg) {
            if (!arg.isEmpty()){
                Class<?> cl = arg.get(0).getClass();
                return new PEntityList(cl, cl.getSimpleName(), md());    
            }else{
                return new PEntityList(null, null, md());
            }            
        }    
    });
    
    private final Map<Object,PComparable<?>> comToPath = new PathFactory<Object,PComparable<?>>(new Transformer<Object,PComparable<?>>(){
        @SuppressWarnings("unchecked")
        public PComparable<?> transform(Object arg) {
            return new PComparable(arg.getClass(), md());
        }    
    });
    
    private final Map<Object,PNumber<?>> numToPath = new PathFactory<Object,PNumber<?>>(new Transformer<Object,PNumber<?>>(){
        @SuppressWarnings("unchecked")
        public PNumber<?> transform(Object arg) {
            return new PNumber(arg.getClass(), md());
        }    
    });
        
    private final Map<Object,PStringArray> saToPath = new PathFactory<Object,PStringArray>(new Transformer<Object,PStringArray>(){
        public PStringArray transform(Object arg) {
            return new PStringArray(md());
        }    
    });
        
    private final Map<Object,PEntity<?>> entityToPath = new PathFactory<Object,PEntity<?>>(new Transformer<Object,PEntity<?>>(){
        @SuppressWarnings("unchecked")
        public PEntity<?> transform(Object arg) {
            return new PEntity(arg.getClass(), arg.getClass().getSimpleName(), md());
        }    
    });
    
    private final Map<String,ExtString> strToExtPath = new PathFactory<String,ExtString>(new Transformer<String,ExtString>(){
        public ExtString transform(String str) {
            return new ExtString(md());
        }        
    });
    
    public <D> Expr<D> createAny(D arg){
        throw new UnsupportedOperationException();
    }
    
    public EBoolean createBoolean(Boolean arg){
        return arg.booleanValue() ? btrue : bfalse;
    }
    
    public PBooleanArray createBooleanArray(Boolean[] args){
        return baToPath.get(Arrays.asList(args));
    }
    
    @SuppressWarnings("unchecked")
    public <D> PEntityCollection<D> createEntityCollection(Collection<D> arg) {
        return (PEntityCollection<D>) ecToPath.get(arg);
    }
    
    @SuppressWarnings("unchecked")
    public <D extends Comparable<? super D>> EComparable<D> createComparable(D arg){
        return (EComparable<D>) comToPath.get(arg);
    }
    
    @SuppressWarnings("unchecked")
    public <D extends Number & Comparable<? super D>> ENumber<D> createNumber(D arg) {
        return (ENumber<D>) numToPath.get(arg);
    }
    
    @SuppressWarnings("unchecked")
    public <D> PEntity<D> createEntity(D arg){
        return (PEntity<D>) entityToPath.get(arg);
    }
 
    @SuppressWarnings("unchecked")
    public <D extends Comparable<? super D>> PComparableArray<D> createComparableArray(D[] args){
        return (PComparableArray<D>) caToPath.get(Arrays.asList(args));
    }

    @SuppressWarnings("unchecked")
    public <D> PEntityList<D> createEntityList(List<D> arg) {
        return (PEntityList<D>) elToPath.get(arg);
    }

    public ExtString createString(String arg){
        return StringUtils.isEmpty(arg) ? strExt : strToExtPath.get(arg);
    }
    
    public PStringArray createStringArray(String[] args){
        return saToPath.get(Arrays.asList(args));
    }
    
    private PathMetadata<String> md(){
        return PathMetadata.forVariable("v"+String.valueOf(++counter));
    }
    
    private static class PathFactory<K,V> extends LazyMap<K,V>{

        private static final long serialVersionUID = -2443097467085594792L;
        
        protected PathFactory(Transformer<K,V> transformer) {
            super(new WeakHashMap<K,V>(), transformer);
        }
                
    }


   
}
