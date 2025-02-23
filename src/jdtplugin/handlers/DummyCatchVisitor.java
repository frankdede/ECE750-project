package jdtplugin.handlers;

import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class DummyCatchVisitor extends VisitorWithAntipatternRecording<DummyCatchVisitor>{ 
	public DummyCatchVisitor(String sourceCode) {
		super(sourceCode);
		// TODO Auto-generated constructor stub
	}

	private int catchCount = 0;
	@Override
	public boolean visit(CatchClause node) {
	    // Check if the catch block is empty (Dummy Handler)
	    if (node.getBody() != null && node.getBody().statements().isEmpty()) {
	        this.recordAntipattern(node, sourceCode); // TODO: get source code !!!!!!
	    }

	    return super.visit(node);
	}

	public int getCatchCount() {
		return catchCount;
	}

	@Override
	public String getAntiPatternType() {
		// TODO Auto-generated method stub
		return "dummy_catch";
	}

	@Override
	protected DummyCatchVisitor createSubclassInstance(String sourceCode) {
		// TODO Auto-generated method stub
		return new DummyCatchVisitor(sourceCode);
	}

}


