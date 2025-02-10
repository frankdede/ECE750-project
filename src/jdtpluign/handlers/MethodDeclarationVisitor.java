package jdtpluign.handlers;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDeclarationVisitor extends ASTVisitor {
	
	private int methodCount = 0;
	@Override
	public boolean visit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		System.out.println("method:" + node.getName().toString());
		this.setMethodCount(this.getMethodCount() + 1);
		return super.visit(node);
	}
	public int getMethodCount() {
		return methodCount;
	}
	public void setMethodCount(int methodCount) {
		this.methodCount = methodCount;
	}
}
