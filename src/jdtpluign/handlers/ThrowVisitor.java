package jdtpluign.handlers;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ThrowStatement;

public class ThrowVisitor extends ASTVisitor{
	private boolean visited = false; 
	public boolean visit(ThrowStatement node) {
		// TODO Auto-generated method stub
		System.out.println(node);
		this.visited = true;
		return true;
	}
}
