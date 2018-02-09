package com.oracle.app.nodes.call;

import com.oracle.app.nodes.GoExpressionNode;
import com.oracle.app.runtime.GoFunction;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

/**
 * The node for function invocation in SL. Since SL has first class functions, the {@link SLFunction
 * target function} can be computed by an arbitrary expression. This node is responsible for
 * evaluating this expression, as well as evaluating the {@link #argumentNodes arguments}. The
 * actual dispatch is then delegated to a chain of {@link SLDispatchNode} that form a polymorphic
 * inline cache.
 */
@NodeInfo(shortName = "invoke")
public class GoInvokeNode extends GoExpressionNode {

    @Child protected GoExpressionNode functionNode;
    @Children protected final GoExpressionNode[] argumentNodes;
    @Child protected GoGenericDispatchNode dispatchNode;

    public GoInvokeNode(GoExpressionNode functionNode, GoExpressionNode[] argumentNodes) {
        this.functionNode = functionNode;
        this.argumentNodes = argumentNodes;
        this.dispatchNode = new GoGenericDispatchNode();
    }
    

    /*
     * Executes only the generic function call. So only the slow route is available for function calls
     */
    @ExplodeLoop
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        GoFunction function = (GoFunction) functionNode.executeGeneric(frame);

        CompilerAsserts.compilationConstant(argumentNodes.length);

        Object[] argumentValues = new Object[argumentNodes.length];
        for (int i = 0; i < argumentNodes.length; i++) {
            argumentValues[i] = argumentNodes[i].executeGeneric(frame);
        }
        return dispatchNode.executeDispatch(function, argumentValues);
    }

    @Override
    protected boolean isTaggedWith(Class<?> tag) {
        if (tag == StandardTags.CallTag.class) {
            return true;
        }
        return super.isTaggedWith(tag);
    }
}