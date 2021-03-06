package uri;/*
 * Copyright (c) 2012, 2016, Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Scope;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import builtins.SLBuiltinNode;
import builtins.SLDefineFunctionBuiltin;
import builtins.SLNanoTimeBuiltin;
import builtins.SLPrintlnBuiltin;
import builtins.SLReadlnBuiltin;
import builtins.SLStackTraceBuiltin;
import nodes.SLEvalRootNode;
import nodes.SLRootNode;
import nodes.SLTypes;
import nodes.access.SLReadPropertyCacheNode;
import nodes.access.SLReadPropertyNode;
import nodes.access.SLWritePropertyCacheNode;
import nodes.access.SLWritePropertyNode;
import nodes.call.SLDispatchNode;
import nodes.call.SLInvokeNode;
import nodes.controlflow.SLBlockNode;
import nodes.controlflow.SLBreakNode;
import nodes.controlflow.SLContinueNode;
import nodes.controlflow.SLDebuggerNode;
import nodes.controlflow.SLIfNode;
import nodes.controlflow.SLReturnNode;
import nodes.controlflow.SLWhileNode;
import nodes.expression.SLAddNode;
import nodes.expression.SLBigIntegerLiteralNode;
import nodes.expression.SLDivNode;
import nodes.expression.SLEqualNode;
import nodes.expression.SLFunctionLiteralNode;
import nodes.expression.SLLessOrEqualNode;
import nodes.expression.SLLessThanNode;
import nodes.expression.SLLogicalAndNode;
import nodes.expression.SLLogicalOrNode;
import nodes.expression.SLMulNode;
import nodes.expression.SLStringLiteralNode;
import nodes.expression.SLSubNode;
import nodes.local.SLLexicalScope;
import nodes.local.SLReadLocalVariableNode;
import nodes.local.SLWriteLocalVariableNode;
import parser.ParseException;
import parser.Parser;
import parser.SLNodeFactory;
import runtime.SLBigNumber;
import runtime.SLContext;
import runtime.SLFunction;
import runtime.SLFunctionRegistry;
import runtime.SLNull;

/**
 * SL is a simple language to demonstrate and showcase features of Truffle. The implementation is as
 * simple and clean as possible in order to help understanding the ideas and concepts of Truffle.
 * The language has first class functions, and objects are key-value stores.
 * <p>
 * SL is dynamically typed, i.e., there are no type names specified by the programmer. SL is
 * strongly typed, i.e., there is no automatic conversion between types. If an operation is not
 * available for the types encountered at run time, a type error is reported and execution is
 * stopped. For example, {@code 4 - "2"} results in a type error because subtraction is only defined
 * for numbers.
 *
 * <p>
 * <b>Types:</b>
 * <ul>
 * <li>Number: arbitrary precision integer numbers. The implementation uses the Java primitive type
 * {@code long} to represent numbers that fit into the 64 bit range, and {@link BigInteger} for
 * numbers that exceed the range. Using a primitive type such as {@code long} is crucial for
 * performance.
 * <li>Boolean: implemented as the Java primitive type {@code boolean}.
 * <li>String: implemented as the Java standard type {@link String}.
 * <li>Function: implementation type {@link SLFunction}.
 * <li>Object: efficient implementation using the object model provided by Truffle. The
 * implementation type of objects is a subclass of {@link DynamicObject}.
 * <li>Null (with only one value {@code null}): implemented as the singleton
 * {@link SLNull#SINGLETON}.
 * </ul>
 * The class {@link SLTypes} lists these types for the Truffle DSL, i.e., for type-specialized
 * operations that are specified using Truffle DSL annotations.
 *
 * <p>
 * <b>Language concepts:</b>
 * <ul>
 * <li>Literals for {@link SLBigIntegerLiteralNode numbers} , {@link SLStringLiteralNode strings},
 * and {@link SLFunctionLiteralNode functions}.
 * <li>Basic arithmetic, logical, and comparison operations: {@link SLAddNode +}, {@link SLSubNode
 * -}, {@link SLMulNode *}, {@link SLDivNode /}, {@link SLLogicalAndNode logical and},
 * {@link SLLogicalOrNode logical or}, {@link SLEqualNode ==}, !=, {@link SLLessThanNode &lt;},
 * {@link SLLessOrEqualNode &le;}, &gt;, &ge;.
 * <li>Local variables: local variables must be defined (via a {@link SLWriteLocalVariableNode
 * write}) before they can be used (by a {@link SLReadLocalVariableNode read}). Local variables are
 * not visible outside of the block where they were first defined.
 * <li>Basic control flow statements: {@link SLBlockNode blocks}, {@link SLIfNode if},
 * {@link SLWhileNode while} with {@link SLBreakNode break} and {@link SLContinueNode continue},
 * {@link SLReturnNode return}.
 * <li>Debugging control: {@link SLDebuggerNode debugger} statement uses
 * {@link DebuggerTags} tag to halt the execution when run under the debugger.
 * <li>Function calls: {@link SLInvokeNode invocations} are efficiently implemented with
 * {@link SLDispatchNode polymorphic inline caches}.
 * <li>Object access: {@link SLReadPropertyNode} uses {@link SLReadPropertyCacheNode} as the
 * polymorphic inline cache for property reads. {@link SLWritePropertyNode} uses
 * {@link SLWritePropertyCacheNode} as the polymorphic inline cache for property writes.
 * </ul>
 *
 * <p>
 * <b>Syntax and parsing:</b><br>
 * The syntax is described as an attributed grammar. The {@link Parser} is
 * automatically generated by the parser generator JavaCC. The grammar contains semantic
 * actions that build the AST for a method. To keep these semantic actions short, they are mostly
 * calls to the {@link SLNodeFactory} that performs the actual node creation. All functions found in
 * the SL source are added to the {@link SLFunctionRegistry}, which is accessible from the
 * {@link SLContext}.
 *
 * <p>
 * <b>Builtin functions:</b><br>
 * Library functions that are available to every SL source without prior definition are called
 * builtin functions. They are added to the {@link SLFunctionRegistry} when the {@link SLContext} is
 * created. Some of the current builtin functions are
 * <ul>
 * <li>{@link SLReadlnBuiltin readln}: Read a String from the {@link SLContext#getInput() standard
 * input}.
 * <li>{@link SLPrintlnBuiltin println}: Write a value to the {@link SLContext#getOutput() standard
 * output}.
 * <li>{@link SLNanoTimeBuiltin nanoTime}: Returns the value of a high-resolution time, in
 * nanoseconds.
 * <li>{@link SLDefineFunctionBuiltin defineFunction}: Parses the functions provided as a String
 * argument and adds them to the function registry. Functions that are already defined are replaced
 * with the new version.
 * <li>{@link SLStackTraceBuiltin stckTrace}: Print all function activations with all local
 * variables.
 * </ul>
 */
@TruffleLanguage.Registration(id = SLLanguage.ID, name = "SL", version = "0.30", mimeType = SLLanguage.MIME_TYPE)
@ProvidedTags({StandardTags.CallTag.class, StandardTags.StatementTag.class, StandardTags.RootTag.class, DebuggerTags.AlwaysHalt.class})
public final class SLLanguage extends TruffleLanguage<SLContext> {
    public static volatile int counter;

    public static final String ID = "sl";
    public static final String MIME_TYPE = "application/x-sl";

    public SLLanguage() {
        counter++;
    }

    @Override
    protected SLContext createContext(Env env) {
        return new SLContext(this, env, new ArrayList<>(EXTERNAL_BUILTINS));
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();
        Map<String, SLRootNode> functions;
        Map<String, DynamicObject> classes;
        /*
         * Parse the provided source. At this point, we do not have a SLContext yet. Registration of
         * the functions with the SLContext happens lazily in SLEvalRootNode.
         */
        if (request.getArgumentNames().isEmpty()) {
            Parser p = new Parser(source.getInputStream());
            try {
                functions = p.parseUri(this, source);
                classes = p.getClasses();
            } catch (ParseException e) {
                throw new IOException("Failed to parse !" + e.getMessage());
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("def main(");
            String sep = "";
            for (String argumentName : request.getArgumentNames()) {
                sb.append(sep);
                sb.append(argumentName);
                sep = ",";
            }
            sb.append(")\n{\n return ");
            sb.append(request.getSource().getCharacters());
            sb.append(";\n}\n");
            Source decoratedSource = Source.newBuilder(sb.toString()).mimeType(request.getSource().getMimeType()).name(request.getSource().getName()).build();
            Parser p = new Parser(decoratedSource.getInputStream());
            try {
                functions = p.parseUri(this, decoratedSource);
                classes = p.getClasses();
            } catch (ParseException e) {
                throw new IOException("Failed to parse !" + e.getMessage());
            }
        }
        SLRootNode main = functions.get("main");
        SLRootNode evalMain;
        if (main != null) {
            /*
             * We have a main function, so "evaluating" the parsed source means invoking that main
             * function. However, we need to lazily register functions into the SLContext first, so
             * we cannot use the original SLRootNode for the main function. Instead, we create a new
             * SLEvalRootNode that does everything we need.
             */
            evalMain = new SLEvalRootNode(this, main.getFrameDescriptor(), main.getBodyNode(), main.getSourceSection(), main.getName(), functions, classes);
        } else {
            /*
             * Even without a main function, "evaluating" the parsed source needs to register the
             * functions into the SLContext.
             */
            evalMain = new SLEvalRootNode(this, null, null, null, "[no_main]", functions, classes);
        }
//        NodeUtil.printCompactTree(System.out, evalMain);
        return Truffle.getRuntime().createCallTarget(evalMain);
    }

    @Override
    protected Object findExportedSymbol(SLContext context, String globalName, boolean onlyExplicit) {
        return context.getFunctionRegistry().lookup(globalName, false);
    }

    @Override
    protected Object getLanguageGlobal(SLContext context) {
        /*
         * The context itself is the global function registry. SL does not have global variables.
         */
        return context;
    }

    @Override
    protected boolean isVisible(SLContext context, Object value) {
        return value != SLNull.SINGLETON;
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        if (!(object instanceof TruffleObject)) {
            return false;
        }
        TruffleObject truffleObject = (TruffleObject) object;
        return truffleObject instanceof SLFunction || truffleObject instanceof SLBigNumber || SLContext.isSLObject(truffleObject);
    }

    @Override
    protected String toString(SLContext context, Object value) {
        if (value == SLNull.SINGLETON) {
            return "NULL";
        }
        if (value instanceof SLBigNumber) {
            return super.toString(context, ((SLBigNumber) value).getValue());
        }
        if (value instanceof Long) {
            return Long.toString((Long) value);
        }
        return super.toString(context, value);
    }

    @Override
    protected Object findMetaObject(SLContext context, Object value) {
        if (value instanceof Number || value instanceof SLBigNumber) {
            return "Number";
        }
        if (value instanceof Boolean) {
            return "Boolean";
        }
        if (value instanceof String) {
            return "String";
        }
        if (value == SLNull.SINGLETON) {
            return "Null";
        }
        if (value instanceof SLFunction) {
            return "Function";
        }
        return "Object";
    }

    @Override
    protected SourceSection findSourceLocation(SLContext context, Object value) {
        if (value instanceof SLFunction) {
            SLFunction f = (SLFunction) value;
            return f.getCallTarget().getRootNode().getSourceSection();
        }
        return null;
    }

    @Override
    public Iterable<Scope> findLocalScopes(SLContext context, Node node, Frame frame) {
        final SLLexicalScope scope = SLLexicalScope.createScope(node);
        return new Iterable<Scope>() {
            @Override
            public Iterator<Scope> iterator() {
                return new Iterator<Scope>() {
                    private SLLexicalScope previousScope;
                    private SLLexicalScope nextScope = scope;

                    @Override
                    public boolean hasNext() {
                        if (nextScope == null) {
                            nextScope = previousScope.findParent();
                        }
                        return nextScope != null;
                    }

                    @Override
                    public Scope next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        Scope vscope = Scope.newBuilder(nextScope.getName(), nextScope.getVariables(frame)).node(nextScope.getNode()).arguments(nextScope.getArguments(frame)).build();
                        previousScope = nextScope;
                        nextScope = null;
                        return vscope;
                    }
                };
            }
        };
    }

    @Override
    protected Iterable<Scope> findTopScopes(SLContext context) {
        return context.getTopScopes();
    }

    public static SLContext getCurrentContext() {
        return getCurrentContext(SLLanguage.class);
    }

    private static final List<NodeFactory<? extends SLBuiltinNode>> EXTERNAL_BUILTINS = Collections.synchronizedList(new ArrayList<>());

    public static void installBuiltin(NodeFactory<? extends SLBuiltinNode> builtin) {
        EXTERNAL_BUILTINS.add(builtin);
    }

}
