/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.Source;
import nodes.SLRootNode;
import parser.Parser;
import uri.SLLanguage;

import java.util.*;

/**
 * Manages the mapping from function names to {@link SLFunction function objects}.
 */
public final class SLClassRegistry {

    private final SLLanguage language;
    private final Map<String, DynamicObject> classObjects = new HashMap<>();

    public SLClassRegistry(SLLanguage language) {
        this.language = language;
    }

    /**
     * Returns the canonical {@link SLFunction} object for the given name. If it does not exist yet,
     * it is created.
     */
    public DynamicObject lookup(String name) {
        return classObjects.get(name);
    }

    /**
     * Associates the {@link SLFunction} with the given name with the given implementation root
     * node. If the function did not exist before, it defines the function. If the function existed
     * before, it redefines the function and the old implementation is discarded.
     */
    public void register(String name, DynamicObject obj) {
          classObjects.put(name, obj);
//        RootCallTarget callTarget = Truffle.getRuntime().createCallTarget(rootNode);
//        function.setCallTarget(callTarget);
    }

    public void register(Map<String, DynamicObject> newClasses) {
        for (Map.Entry<String, DynamicObject> entry : newClasses.entrySet()) {
            register(entry.getKey(), entry.getValue());
        }
    }

//    public void register(Source newClasses) {
//        try {
//            register(new Parser(newClasses.getInputStream()).parseUri(language, newClasses));
//        } catch (Exception e) {
//            // XXX
//        }
//    }

    /**
     * Returns the sorted list of all functions, for printing purposes only.
     */
    public List<DynamicObject> getClasses() {
        List<DynamicObject> result = new ArrayList<>(classObjects.values());
//        Collections.sort(result, new Comparator<DynamicObject>() {
//            public int compare(SLFunction f1, SLFunction f2) {
//                return f1.toString().compareTo(f2.toString());
//            }
//        });
        return result;
    }

}