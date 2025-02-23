package jdtplugin.handlers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class GetCauseInCatchVisitor extends VisitorWithAntipatternRecording<GetCauseInCatchVisitor> {
	
    public GetCauseInCatchVisitor() {
		super();
		// TODO Auto-generated constructor stub
	}


	public boolean visit(CatchClause node) {
        // Get the body of the catch block
        Block catchBlock = node.getBody();

        // Iterate over the statements in the catch block
        for (Object statement : catchBlock.statements()) {
            if (statement instanceof ExpressionStatement) {
                ExpressionStatement exprStmt = (ExpressionStatement) statement;
                Expression expression = exprStmt.getExpression();

                // Check if the expression is a method invocation
                if (expression instanceof MethodInvocation) {
                    MethodInvocation methodInvocation = (MethodInvocation) expression;
                    // Check if the method is getCause
                    if (methodInvocation.getName().getIdentifier().equals("getCause")) {
                        this.recordAntipattern(node, exprStmt);
                    }
                }
            }
        }
        return super.visit(node);
    }


	@Override
	public String getAntiPatternType() {
		// TODO Auto-generated method stub
		return "get_cause_in_catch";
	}


	@Override
	protected GetCauseInCatchVisitor createSubclassInstance() {
		// TODO Auto-generated method stub
		GetCauseInCatchVisitor visitor = new GetCauseInCatchVisitor();
		visitor.setOutputStreamWriter(writer);
		return visitor;
	}
}
