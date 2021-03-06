package com.oracle.app;

import java.util.ArrayList;
import java.util.List;

import com.oracle.app.nodes.GoRootNode;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameInstance;
import com.oracle.truffle.api.frame.FrameInstanceVisitor;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;


public class GoException extends RuntimeException {

    private static final long serialVersionUID = -6799734410727348507L;

    public GoException(String message) {
        super(message);
        CompilerAsserts.neverPartOfCompilation();
        initCause(new Throwable("Java stack trace"));
    }

    @Override
    @SuppressWarnings("sync-override")
    public Throwable fillInStackTrace() {
        CompilerAsserts.neverPartOfCompilation();
        return fillInSLStackTrace(this);
    }

    /**
     * Uses the Truffle API to iterate the stack frames and to create and set Java
     * {@link StackTraceElement} elements based on the source sections of the call nodes on the
     * stack.
     */
    public static Throwable fillInSLStackTrace(Throwable t) {
        final List<StackTraceElement> stackTrace = new ArrayList<>();
        Truffle.getRuntime().iterateFrames(new FrameInstanceVisitor<Void>() {
            public Void visitFrame(FrameInstance frame) {
                Node callNode = frame.getCallNode();
                if (callNode == null) {
                    return null;
                }
                RootNode root = callNode.getRootNode();

                /*
                 * There should be no RootNodes other than SLRootNodes on the stack. Just for the
                 * case if this would change.
                 */
                String methodName = "$unknownFunction";
                if (root instanceof GoRootNode) {
                    methodName = ((GoRootNode) root).getName();
                }

                SourceSection sourceSection = callNode.getEncapsulatingSourceSection();
                Source source = sourceSection != null ? sourceSection.getSource() : null;
                String sourceName = source != null ? source.getName() : null;
                int lineNumber;
                try {
                    lineNumber = sourceSection != null ? sourceSection.getStartLine() : -1;
                } catch (UnsupportedOperationException e) {
                    /*
                     * SourceSection#getLineLocation() may throw an UnsupportedOperationException.
                     */
                    lineNumber = -1;
                }
                stackTrace.add(new StackTraceElement("Go", methodName, sourceName, lineNumber));
                return null;
            }
        });
        t.setStackTrace(stackTrace.toArray(new StackTraceElement[stackTrace.size()]));
        return t;
    }
}