/*
 * Copyright (c) 2012, 2015, Oracle and/or its affiliates. All rights reserved.
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

package com.oracle.app.nodes;

import java.math.BigInteger;

import com.oracle.app.nodes.types.GoArray;
import com.oracle.app.nodes.types.GoSlice;
import com.oracle.app.runtime.GoContext;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.Instrumentable;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

/**
 * Base class for all SL nodes that produce a value and therefore benefit from type specialization.
 * The annotation {@link TypeSystemReference} specifies the SL types. Specifying it here defines the
 * type system for all subclasses.
 */
@TypeSystemReference(GoTypes.class)
@NodeInfo(description = "The abstract base node for all expressions")
@Instrumentable(factory = GoExpressionNodeWrapper.class)
public abstract class GoExpressionNode extends GoStatementNode {

    /**
     * The execute method when no specialization is possible. This is the most general case,
     * therefore it must be provided by all subclasses.
     */
    public abstract Object executeGeneric(VirtualFrame frame);

    /**
     * When we use an expression at places where a {@link SLStatementNode statement} is already
     * sufficient, the return value is just discarded.
     */
    @Override
    public void executeVoid(VirtualFrame frame) {
        executeGeneric(frame);
    }
    
    /**
     * Implement this method for types so that their identifiers are known
     * @return The name of the variable
     */
    public String getName(){
    	return "GoExpressionNode";
    }

    /*
     * Execute methods for specialized types. They all follow the same pattern: they call the
     * generic execution method and then expect a result of their return type. Type-specialized
     * subclasses overwrite the appropriate methods.
     */

    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return GoTypesGen.expectBoolean(executeGeneric(frame));
    }
    
    public int executeInteger(VirtualFrame frame) throws UnexpectedResultException {
    	return GoTypesGen.expectInteger(executeGeneric(frame));
    }

	public String executeString(VirtualFrame frame) throws UnexpectedResultException {
		return GoTypesGen.expectString(executeGeneric(frame));
	}

	public float executeFloat(VirtualFrame frame) throws UnexpectedResultException {
		return GoTypesGen.expectFloat(executeGeneric(frame));
	}

	public GoArray executeGoArray(VirtualFrame frame) throws UnexpectedResultException {
		return GoTypesGen.expectGoArray(executeGeneric(frame));
	}

	public  GoSlice executeGoSlice(VirtualFrame frame) throws UnexpectedResultException {
		return GoTypesGen.expectGoSlice(executeGeneric(frame));
	}

	public double executeDouble(VirtualFrame frame) throws UnexpectedResultException{
		return GoTypesGen.expectDouble(executeGeneric(frame));
	}

}
