package jdtplugin.handlers;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MultilineLogVisitor extends VisitorWithAntipatternRecording<MultilineLogVisitor> {

	public MultilineLogVisitor(String sourceCode) {
		super(sourceCode);
	}

	@Override
	public boolean visit(CatchClause node) {
		// Get the body of the catch block
		Block catchBlock = node.getBody();

		List<ExpressionStatement> loggingExprs = new LinkedList<ExpressionStatement>();

		// Iterate over the statements in the catch block
		for (Object statement : catchBlock.statements()) {
			if (statement instanceof ExpressionStatement) {
				ExpressionStatement exprStmt = (ExpressionStatement) statement;
				Expression expression = exprStmt.getExpression();

				// Check if the expression is a method invocation
				if (expression instanceof MethodInvocation) {
					MethodInvocation methodInvocation = (MethodInvocation) expression;
					// Check if the method is logging
					if (isLoggingCall(methodInvocation)) {
						loggingExprs.add(exprStmt);
					} else {
						int currentSize = loggingExprs.size();
						if (currentSize == 1) {
							loggingExprs = new LinkedList<ExpressionStatement>();
						} else if (currentSize > 1) {
							this.recordAntipattern(node, loggingExprs.getFirst());
							return super.visit(node);
						}
					}
				}
			}

		}
		
		// handle the case where the last line is a logging method call
		if (loggingExprs.size() > 1) {
			this.recordAntipattern(node, loggingExprs.getFirst());
		}
		return super.visit(node);
	}


	private boolean isLoggingCall(MethodInvocation methodInvocation) {
		// Check if the method name matches Log4j/java.util.Logger logging methods (e.g., error, warn,
		// info, debug)
		String methodName = methodInvocation.getName().getIdentifier();
		switch (methodName) {
			case "warning":
			case "fine":
			case "severe":
			case "fatal":
			case "error":
			case "warn":
			case "info":
			case "debug":
				Expression expression = methodInvocation.getExpression();
				ITypeBinding typeBinding = expression.resolveTypeBinding();
	
				if (typeBinding != null && typeBinding.getQualifiedName().endsWith("Logger")) {
					return true;
				}
				;
				break;
			default:
				break;
		}

		return false;
	}

	@Override
	public String getAntiPatternType() {
		// TODO Auto-generated method stub
		return "multiline_log";
	}

	@Override
	protected MultilineLogVisitor createSubclassInstance(String sourceCode) {
		// TODO Auto-generated method stub
		return new MultilineLogVisitor(sourceCode);
	}

}
