package jdtpluign.handlers;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;

public class CatchVisitor extends ASTVisitor{
	private int catchCount = 0;
	@Override
	public boolean visit(CatchClause node) {
		// TODO Auto-generated method stub
		this.catchCount += 1;
		
		String excepitonTypeString = node.getException().getType().toString();
		if (node.getBody() != null && node.getBody().statements().isEmpty()) {
			System.out.println("[ANTIPATTERN] Found 'Catch and do nothig exception'");
		} else {
			System.out.println("Catch clause body: " + node.getBody());
		}
		
		if ("Exception".equals(excepitonTypeString)) {
			System.out.println("[ANTIPATTERN] Found 'Generic exception'");
		} else {
			System.out.println("Catch clause exception type: " + excepitonTypeString);
		}
		return super.visit(node);
	}
	public int getCatchCount() {
		return catchCount;
	}
}
