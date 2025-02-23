package jdtplugin.handlers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;


public class ThrowInFinallyVisitor extends BaseVisitor<ThrowInFinallyVisitor> {

	public ThrowInFinallyVisitor(String sourceCode) {
		super(sourceCode);
	}

	@Override
	protected ThrowInFinallyVisitor createSubclassInstance(String sourceCode) {
		// TODO Auto-generated method stub
		return new ThrowInFinallyVisitor(sourceCode);
	}

	public class ThrowVisitor extends VisitorWithAntipatternRecording<ThrowVisitor>{

		public ThrowVisitor(String sourceCode) {
			super(sourceCode);
		}

		@Override
		public boolean visit(ThrowStatement node) {
			this.recordAntipattern(node, sourceCode); // TODO: provide source code
			return false;
		}
			

		@Override
		public String getAntiPatternType() {
			// TODO Auto-generated method stub
			return "throw_in_finally";
		}

		@Override
		protected ThrowVisitor createSubclassInstance(String sourceCode) {
			// TODO Auto-generated method stub
			return new ThrowVisitor(sourceCode);
		}
	}

	
	@Override
	public boolean visit(TryStatement node) {
		if(node.getFinally() != null) {
			Statement stmt = node.getFinally();
			ThrowVisitor throwVisitor = new ThrowVisitor(sourceCode);
			stmt.accept(throwVisitor);
		}
		return super.visit(node);
	}

}


