/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.codegen;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.mysema.codegen.model.ClassType;
import com.mysema.codegen.model.Type;
import com.mysema.codegen.model.TypeCategory;
import com.mysema.codegen.model.Types;
import com.mysema.codegen.support.Cat;

public class ComplexEvaluationTest {

    private EvaluatorFactory factory = new ECJEvaluatorFactory(getClass().getClassLoader());

    @Test
    @SuppressWarnings("unchecked")
    public void Complex() {
        ClassType resultType = new ClassType(TypeCategory.LIST, List.class, Types.STRING);
        StringBuilder source = new StringBuilder();
        source.append("java.util.List<String> rv = new java.util.ArrayList<String>();\n");
        source.append("for (String a : a_){\n");
        source.append("    for (String b : b_){\n");
        source.append("        if (a.equals(b)){\n");
        source.append("            rv.add(a);\n");
        source.append("        }\n");
        source.append("    }\n");
        source.append("}\n");
        source.append("return rv;");

        Evaluator<List> evaluator = factory.createEvaluator(source.toString(), resultType,
                new String[] { "a_", "b_" }, new Type[] { resultType, resultType }, new Class[] {
                        List.class, List.class }, Collections.<String, Object> emptyMap());

        List<String> a_ = Arrays.asList("1", "2", "3", "4");
        List<String> b_ = Arrays.asList("2", "4", "6", "8");

        assertEquals(Arrays.asList("2", "4"), evaluator.evaluate(a_, b_));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ComplexClassLoading() {
        ClassType resultType = new ClassType(TypeCategory.LIST, List.class, Types.OBJECTS);
        StringBuilder source = new StringBuilder();
        source.append("java.util.List<Object[]> rv = new java.util.ArrayList<Object[]>();\n");
        source.append("for (com.mysema.codegen.support.Cat cat : (java.util.List<com.mysema.codegen.support.Cat>)cat_){\n");
        source.append("for (com.mysema.codegen.support.Cat otherCat : (java.util.List<com.mysema.codegen.support.Cat>)otherCat_){\n");
        source.append("rv.add(new Object[]{cat,otherCat});\n");
        source.append("}\n");
        source.append("}\n");
        source.append("return rv;\n");

        Cat fuzzy = new Cat("fuzzy");
        Cat spot = new Cat("spot");
        Cat mittens = new Cat("mittens");
        Cat sparkles = new Cat("sparkles");

        List<Cat> a_ = Arrays.asList(fuzzy, spot);
        List<Cat> b_ = Arrays.asList(mittens, sparkles);

        ClassType argType = new ClassType(TypeCategory.LIST, List.class, new ClassType(Cat.class));
        Evaluator<List> evaluator = factory.createEvaluator(source.toString(), resultType,
                new String[] { "cat_", "otherCat_" }, new Type[] { argType, argType }, new Class[] {
                        List.class, List.class }, Collections.<String, Object> emptyMap());
        
        Object[][] expResults = { {fuzzy, mittens}, {fuzzy, sparkles}, {spot, mittens}, {spot, sparkles} };
        List<Object[]> result = evaluator.evaluate(a_, b_);
        assertEquals(expResults.length, result.size());
        
        for (int i = 0; i < expResults.length; i++) {
            assertEquals(expResults[i].length, result.get(i).length);
            for (int j = 0; j < expResults[i].length; j++) {
                assertEquals(expResults[i][j], result.get(i)[j]);
            }
        }
    }
    
    @Test(expected=CodegenException.class)
    @SuppressWarnings("unchecked")
    public void ComplexClassLoadingFailure() {
        ClassType resultType = new ClassType(TypeCategory.LIST, List.class, Types.STRING);
        StringBuilder source = new StringBuilder();
        source.append("java.util.List<String> rv = (java.util.List<String>) new java.util.ArrayList<Franklin>();\n");
        source.append("for (String a : a_){\n");
        source.append("    for (String b : b_){\n");
        source.append("        if (a.equals(b)){\n");
        source.append("            rv.add(a);\n");
        source.append("        }\n");
        source.append("    }\n");
        source.append("}\n");
        source.append("return rv;");

        Evaluator<List> evaluator = factory.createEvaluator(source.toString(), resultType,
                new String[] { "a_", "b_" }, new Type[] { resultType, resultType }, new Class[] {
                        List.class, List.class }, Collections.<String, Object> emptyMap());

        List<String> a_ = Arrays.asList("1", "2", "3", "4");
        List<String> b_ = Arrays.asList("2", "4", "6", "8");

        assertEquals(Arrays.asList("2", "4"), evaluator.evaluate(a_, b_));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ComplexPrimitiveType() {
        ClassType resultType = new ClassType(TypeCategory.LIST, List.class, Types.BOOLEAN);
        StringBuilder source = new StringBuilder();
        source.append("java.util.List<Boolean> rv = new java.util.ArrayList<Boolean>();\n");
        source.append("for (boolean a : a_){\n");
        source.append("    for (boolean b : b_){\n");
        source.append("        if (a == b){\n");
        source.append("            rv.add(a);\n");
        source.append("        }\n");
        source.append("    }\n");
        source.append("}\n");
        source.append("return rv;");

        Evaluator<List> evaluator = factory.createEvaluator(source.toString(), resultType,
                new String[] { "a_", "b_" }, new Type[] { resultType, resultType }, new Class[] {
                        List.class, List.class }, Collections.<String, Object> emptyMap());

        List<Boolean> a_ = Arrays.asList(true, true, true);
        List<Boolean> b_ = Arrays.asList(false, false, true);

        assertEquals(Arrays.asList(true, true, true), evaluator.evaluate(a_, b_));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void ComplexEmbeddedClass() {
        ClassType resultType = new ClassType(TypeCategory.LIST, List.class, Types.BOOLEAN);
        StringBuilder source = new StringBuilder();
        source.append("java.util.List<Boolean> rv = new java.util.ArrayList<Boolean>();\n");
        source.append("for (boolean a : a_){\n");
        source.append("    for (boolean b : b_){\n");
        source.append("        if (a == b && new TestEmbedded().DO_RETURN()){\n");
        source.append("            rv.add(a);\n");
        source.append("        }\n");
        source.append("    }\n");
        source.append("}\n");
        source.append("return rv;} private static class TestEmbedded { public TestEmbedded() {} public boolean DO_RETURN() { return true; } ");

        Evaluator<List> evaluator = factory.createEvaluator(source.toString(), resultType,
                new String[] { "a_", "b_" }, new Type[] { resultType, resultType }, new Class[] {
                        List.class, List.class }, Collections.<String, Object> emptyMap());

        List<Boolean> a_ = Arrays.asList(true, true, true);
        List<Boolean> b_ = Arrays.asList(false, false, true);

        assertEquals(Arrays.asList(true, true, true), evaluator.evaluate(a_, b_));
    }
}
