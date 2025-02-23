package jdtplugin.handlers;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Statement;

public abstract class VisitorWithAntipatternRecording<T extends VisitorWithAntipatternRecording<T>> extends BaseVisitor<T> {
	
	public OutputStreamWriter writer;
	public abstract String getAntiPatternType();
	
	public VisitorWithAntipatternRecording() {
		super();
		this.writer = new OutputStreamWriter(System.out); // default writer
	}
	
	
	public void setOutputStreamWriter(OutputStreamWriter writer) {
		this.writer = writer;
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
	
	protected ICompilationUnit getICompilationUnit(ASTNode node) {
		ICompilationUnit iCU = (ICompilationUnit) ((CompilationUnit) node.getRoot()).getJavaElement();
		return iCU;
	}
	
	protected String getCompilationUnitName(ASTNode node) {
		ICompilationUnit iCU = this.getICompilationUnit(node);		
		return iCU.getElementName();
	}
	
	protected String getCompilationUnitSource(ASTNode node) throws JavaModelException {
		ICompilationUnit iCU = this.getICompilationUnit(node);		
		return iCU.getSource();
	}
	
	

	protected void recordAntipattern(ASTNode node, Statement statement){
		int lineNumber = ((CompilationUnit) node.getRoot()).getLineNumber(statement.getStartPosition());
		String statementName = statement.toString().trim();
		String csvLine = this.getAntiPatternType() + "," + this.getFullPackageName(node) + ","
				+ this.getCompilationUnitName(node) + "," + lineNumber + "," + statementName + "\n";
		try {
			writer.write(csvLine);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	

	protected void recordAntipattern(ASTNode node) {
		// Get the line number of the statement
		int lineNumber = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
		int start = node.getStartPosition();
        int end = start + node.getLength();
        String sourceCode;
		
        try {
			sourceCode = this.getCompilationUnitSource(node);
		} catch (JavaModelException e) {
			return;
		}
        
        String statementName = sourceCode.substring(start, end).split("\n")[0]; 
        
		String csvLine = this.getAntiPatternType() + "," + this.getFullPackageName(node) 
						+ "," + this.getCompilationUnitName(node) + "," + lineNumber + "," + statementName + "\n";
		
		try {
	        writer.write(csvLine);
	        writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
