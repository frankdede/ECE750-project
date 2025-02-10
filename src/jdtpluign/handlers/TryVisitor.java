package jdtpluign.handlers;

import java.util.concurrent.ForkJoinPool;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.TryStatement;

public class TryVisitor extends ASTVisitor{
	private ThrowVisitor throwVisitor = new ThrowVisitor();
	
	public boolean visit(TryStatement node) {
		// TODO Auto-generated method stub
		if(node.getFinally() != null) {
			System.out.println("test");
			node.getFinally().accept(throwVisitor);
			
		}
		return super.visit(node);
	}
}


