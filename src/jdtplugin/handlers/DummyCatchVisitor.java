package jdtplugin.handlers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class DummyCatchVisitor extends VisitorWithAntipatternRecording<DummyCatchVisitor>{ 
	public DummyCatchVisitor() {
		super();
		// TODO Auto-generated constructor stub
	}

	private int catchCount = 0;
	@Override
	public boolean visit(CatchClause node) {
	    // Check if the catch block is empty (Dummy Handler)
	    if (node.getBody() != null && node.getBody().statements().isEmpty()) {
	        this.recordAntipattern(node);
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
	protected DummyCatchVisitor createSubclassInstance() {
		// TODO Auto-generated method stub
		
		DummyCatchVisitor dummyCatchVisitor = new DummyCatchVisitor();
		dummyCatchVisitor.setOutputStreamWriter(writer);
		return dummyCatchVisitor;
	}

}


