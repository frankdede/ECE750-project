package jdtplugin.handlers;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;

public class DummyVisitor extends ASTVisitor{ 
	private int catchCount = 0;
	@Override
	public boolean visit(CatchClause node) {
	    // Extract exception type
	    String exceptionTypeString = node.getException().getType().toString();

	    // Check if the catch block is empty (Dummy Handler)
	    if (node.getBody() != null && node.getBody().statements().isEmpty()) {
	        System.out.println("[ANTIPATTERN] Found 'Dummy Handler'");
	        this.catchCount += 1;  // Count only dummy handlers
	    } else {
	        System.out.println("Catch clause body: " + node.getBody());
	    }

	    // Check for generic exception handling
	    if ("Exception".equals(exceptionTypeString)) {
	        System.out.println("[ANTIPATTERN] Found 'Generic exception'");
	    } else {
	        System.out.println("Catch clause exception type: " + exceptionTypeString);
	    }

	    return super.visit(node);
	}

	public int getCatchCount() {
		return catchCount;
	}

}


