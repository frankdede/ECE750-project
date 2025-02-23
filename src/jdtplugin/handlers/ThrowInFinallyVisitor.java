package jdtplugin.handlers;

import java.io.OutputStreamWriter;

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
	public OutputStreamWriter writer;

	public ThrowInFinallyVisitor() {
		super();
		this.writer = new OutputStreamWriter(System.out); 
	}
	
	public void setOutputStreamWriter(OutputStreamWriter writer) {
		this.writer = writer;
	}

	@Override
	protected ThrowInFinallyVisitor createSubclassInstance() {
		// TODO Auto-generated method stub
		return new ThrowInFinallyVisitor();
	}

	public class ThrowVisitor extends VisitorWithAntipatternRecording<ThrowVisitor>{

		public ThrowVisitor() {
			super();
		}

		@Override
		public boolean visit(ThrowStatement node) {
			this.recordAntipattern(node); // TODO: provide source code
			return true;
		}
			

		@Override
		public String getAntiPatternType() {
			// TODO Auto-generated method stub
			return "throw_in_finally";
		}

		@Override
		protected ThrowVisitor createSubclassInstance() {
			// TODO Auto-generated method stub
			ThrowVisitor throwVistor = new ThrowVisitor();
			throwVistor.setOutputStreamWriter(writer);
			return throwVistor;
		}
	}

	
	@Override
	public boolean visit(TryStatement node) {
		if(node.getFinally() != null) {
			Statement stmt = node.getFinally();
			ThrowVisitor throwVisitor = new ThrowVisitor();
			throwVisitor.setOutputStreamWriter(writer);
			stmt.accept(throwVisitor);
		}
		return super.visit(node);
	}

}


