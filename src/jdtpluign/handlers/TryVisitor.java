package jdtpluign.handlers;


import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Statement;


public class TryVisitor extends ASTVisitor{
	List<ThrowVisitor> throwVisitors = new LinkedList<>();
	@Override
	public boolean visit(TryStatement node) {

		// TODO Auto-generated method stub
		if(node.getFinally() != null) {
			Statement stmt = node.getFinally();
			
			ThrowVisitor throwVisitor = new ThrowVisitor(stmt);
			throwVisitors.add(throwVisitor);
			
			System.out.println("TryVisitor - Visited finally block: " + node);

			stmt.accept(throwVisitor);
		}
		return super.visit(node);
	}
	
	@Override
    public boolean visit(MethodInvocation node) {
		System.out.println("TryVisitor - Visited method call: " + node);
		IMethodBinding binding = (IMethodBinding) node.getName().resolveBinding();
		ICompilationUnit unit = (ICompilationUnit) binding.getJavaElement().getAncestor( IJavaElement.COMPILATION_UNIT );
		
		if ( unit == null ) {
			return false;
		}
		
		ASTParser parser =  ASTParser.newParser(AST.JLS22); 
		parser.setSource(unit);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		MethodDeclaration decl = (MethodDeclaration)cu.findDeclaringNode(binding.getKey());
		decl.accept(this);
		return false;
    }
}


