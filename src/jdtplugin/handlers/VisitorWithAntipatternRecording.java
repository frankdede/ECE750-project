package jdtplugin.handlers;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Statement;

public abstract class VisitorWithAntipatternRecording<T extends VisitorWithAntipatternRecording<T>> extends BaseVisitor<T> {
	public abstract String getAntiPatternType();
	
	public VisitorWithAntipatternRecording(String sourceCode) {
		super(sourceCode);
	}

	protected String getFullPackageName(ASTNode node) {
		PackageDeclaration packageDeclaration = ((CompilationUnit) node.getRoot()).getPackage();
		// Get the line number of the statement
		if (packageDeclaration != null) {
			return packageDeclaration.getName().getFullyQualifiedName();
		} else {
			return "unknown";
		}
	}
	
	
	protected void recordAntipattern(ASTNode node, Statement statement) {
		int lineNumber = ((CompilationUnit) node.getRoot()).getLineNumber(statement.getStartPosition());
		String statementName = statement.toString().trim();
		System.out.println(this.getAntiPatternType() + "," + this.getFullPackageName(node) + "," + lineNumber + "," + statementName);
	}
	

	protected void recordAntipattern(ASTNode node, String sourceCode) {
		// Get the line number of the statement
		
		int lineNumber = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
		int start = node.getStartPosition();
        int end = start + node.getLength();
        String statementName = sourceCode.substring(start, end).split("\n")[0]; 
        
		System.out.println(this.getAntiPatternType() + "," + this.getFullPackageName(node) + "," + lineNumber + "," + statementName);
	}

}
